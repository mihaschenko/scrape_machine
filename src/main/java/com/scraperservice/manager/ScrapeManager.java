package com.scraperservice.manager;

import com.scraperservice.view.StatisticFrameBuilder;
import com.scraperservice.connection.Connection;
import com.scraperservice.connection.ConnectionPool;
import com.scraperservice.exception.DoNotHaveAnyProductLinksException;
import com.scraperservice.scraper.helper.LogHelper;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.queue.ConcurrentLinkedQueueUnique;
import com.scraperservice.scraper.Scraper;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Component
@Data
public class ScrapeManager implements Runnable {
    @Autowired
    private final DataSaveManager dataSaveManager;
    @Autowired
    private final Scraper scraper;
    @Autowired
    private final ConcurrentLinkedQueueUnique linksQueue;
    @Autowired
    private final ConnectionPool connectionPool;
    @Autowired
    private final ExecutorService taskPool;
    @Autowired
    private final CompletableFutureManager<Void> completableFutureManager;
    @Autowired
    private final StatisticFrameBuilder statisticFrameManager;

    @Override
    public void run() {
        // Init scraper's start links
        if(linksQueue.size() == 0) {
            try {
                linksQueue.addAll(scraper.getStartLinks());
            } catch (Exception e) {
                LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
                throw new UnsupportedOperationException(e);
            }
        }

        // Scraper main part
        while((completableFutureManager.getAmountCompletableFutureIsWorking() > 0 || linksQueue.size() > 0)
                && !Thread.currentThread().isInterrupted()) {
            final String link = linksQueue.poll();
            if(link != null && !link.isEmpty()) {
                CompletableFuture<PageData> pageDataFuture = CompletableFuture.supplyAsync(() -> {
                    // Init page
                    try {
                        PageData pageData = new PageData(link);
                        Connection connection = connectionPool.acquire();
                        try{
                            pageData.setHtml(connection.getPage(link, scraper.getDefaultConnectionProperties()));
                            pageData.setPageType(scraper.getPageType(pageData));

                            if(pageData.getPageType() == PageType.UNDEFINED)
                                LogHelper.getLogger().log(Level.WARNING, "UNDEFINED: " + link);
                            else
                                LogHelper.getLogger().log(Level.FINE, pageData.getPageType() + ": " + link);
                            return pageData;
                        }
                        finally {
                            connectionPool.release(connection);
                        }
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, link + " : " + e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                }, taskPool);

                CompletableFuture<Void> categoryCompletableFuture = pageDataFuture.thenApplyAsync(pageData -> {
                    // Scrape category links
                    try{
                        if(pageData.getPageType().isCategory()) {
                            List<String> category = scraper.scrapeCategories(pageData);
                            List<String> subCategory = scraper.scrapeSubCategories(pageData);
                            List<String> products = scraper.scrapeLinksToProductPages(pageData);
                            boolean isPageHasLinks = false;
                            if (category != null && category.size() > 0) {
                                linksQueue.addAll(category);
                                isPageHasLinks = true;
                            }
                            if (subCategory != null && subCategory.size() > 0) {
                                linksQueue.addAll(subCategory);
                                isPageHasLinks = true;
                            }
                            if (products != null && products.size() > 0) {
                                linksQueue.addAll(products);
                                isPageHasLinks = true;
                            }
                            if(!isPageHasLinks)
                                throw new DoNotHaveAnyProductLinksException();
                        }
                        return pageData;
                    }
                    catch (DoNotHaveAnyProductLinksException e) {
                        throw new IllegalStateException(e);
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, link + " : " + e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                }, taskPool)
                .thenAccept(pageData -> {
                    // Scrape link to next page
                    try{
                        if(pageData.getPageType().isCategory()) {
                            String nextPage = scraper.goToNextPage(pageData);
                            if (nextPage != null && !nextPage.isEmpty())
                                linksQueue.add(nextPage);
                        }
                    }
                    catch (DoNotHaveAnyProductLinksException e) {
                        throw new IllegalStateException(e);
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, link + " : " + e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                }).whenComplete((result, exception) -> {
                    logCompleteStatus(link, exception);
                });

                CompletableFuture<Void> productCompletableFuture = pageDataFuture.thenAcceptAsync(pageData -> {
                    if(pageData.getPageType().isProduct()) {
                        try {
                            dataSaveManager.save(scraper.scrapeData(pageData)
                                    .stream().filter(DataArray::checkAllNecessaryCells).collect(Collectors.toList()));
                        } catch (IOException e) { throw new RuntimeException(e); }
                    }
                }, taskPool).whenComplete((result, exception) -> {
                    logCompleteStatus(link, exception);
                });

                completableFutureManager.add(categoryCompletableFuture);
                completableFutureManager.add(productCompletableFuture);
            }
        }

        linksQueue.close();
        taskPool.shutdown();
        connectionPool.close();
        dataSaveManager.close();
        statisticFrameManager.close();
        LogHelper.getLogger().log(Level.INFO, StatisticManager.getInstance().toString());
    }

    private void logCompleteStatus(String link, Throwable exception) {
        if(exception != null) {
            if(exception.getClass() == DoNotHaveAnyProductLinksException.class)
                ;
            else
                LogHelper.getLogger().log(Level.SEVERE, link + " : " + exception.getMessage(), exception);
        }
        else {

        }
    }
}
