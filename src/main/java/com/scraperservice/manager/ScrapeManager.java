package com.scraperservice.manager;

import com.scraperservice.scraper.page.PageType;
import com.scraperservice.view.StatisticFrameBuilder;
import com.scraperservice.connection.Connection;
import com.scraperservice.connection.ConnectionPool;
import com.scraperservice.exception.DoNotHaveAnyProductLinksException;
import com.scraperservice.scraper.helper.LogHelper;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.queue.ConcurrentLinkedQueueUnique;
import com.scraperservice.scraper.Scraper;
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
    private final CompletableFutureManager<PageData> completableFutureManager;
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

                            return pageData;
                        }
                        finally {
                            connectionPool.release(connection);
                        }
                    }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new RuntimeException(e); }
                    catch (Exception e) { throw new RuntimeException(e); }
                }, taskPool);

                CompletableFuture<PageData> categoryCompletableFuture = pageDataFuture.thenApplyAsync(pageData -> {
                    // Scrape category links
                    if(pageData.getPageType().isCategory()) {
                        List<String> category = scraper.scrapeCategories(pageData);
                        List<String> subCategory = scraper.scrapeSubCategories(pageData);
                        List<String> products = scraper.scrapeLinksToProductPages(pageData);
                        boolean isPageHasLinks = false;
                        if (category != null && category.size() > 0) {
                            linksQueue.addAll(category);
                            pageData.setCategoryLinks(category.size());
                            isPageHasLinks = true;
                        }
                        if (subCategory != null && subCategory.size() > 0) {
                            linksQueue.addAll(subCategory);
                            pageData.setSubcategoryLinks(subCategory.size());
                            isPageHasLinks = true;
                        }
                        if (products != null && products.size() > 0) {
                            linksQueue.addAll(products);
                            pageData.setProductLinks(products.size());
                            isPageHasLinks = true;
                        }
                        if(!isPageHasLinks)
                            throw new DoNotHaveAnyProductLinksException();
                    }
                    return pageData;
                }, taskPool)
                .thenApplyAsync(pageData -> {
                    // Scrape link to next page
                    if(pageData.getPageType().isCategory()) {
                        String nextPage = scraper.goToNextPage(pageData);
                        if (nextPage != null && !nextPage.isEmpty()) {
                            pageData.setHasLinkToNextPage(true);
                            linksQueue.add(nextPage);
                        }
                    }
                    return pageData;
                }).whenComplete(this::logCompleteStatus);

                CompletableFuture<PageData> productCompletableFuture = pageDataFuture.thenApplyAsync(pageData -> {
                    // scrape product's data
                    if(pageData.getPageType().isProduct()) {
                        try {
                            List<DataArray> result = scraper.scrapeData(pageData);
                            pageData.setProducts(result.size());
                            dataSaveManager.save(result
                                    .stream().filter(DataArray::checkAllNecessaryCells).collect(Collectors.toList()));
                        } catch (IOException e) { throw new RuntimeException(e); }
                    }
                    return pageData;
                }, taskPool).whenComplete(this::logCompleteStatus);

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

    private void logCompleteStatus(PageData result, Throwable exception) {
        if(exception != null) {
            if(exception.getClass() == DoNotHaveAnyProductLinksException.class)
                LogHelper.getLogger().log(Level.WARNING, "DO_NOT_HAVE_ANY_PRODUCT_LINKS_EXCEPTION: " + result.getUrl());
            else
                LogHelper.getLogger().log(Level.SEVERE, result.getUrl() + " : " + exception.getMessage(), exception);
        }
        else {
            if(result.getPageType() == PageType.UNDEFINED)
                LogHelper.getLogger().log(Level.WARNING, PageType.UNDEFINED + " | " + result.getUrl());
            else if(result.getPageType().isCategory())
                LogHelper.getLogger().log(Level.INFO, String.format("CATEGORY | %s (C: %d, SB: %d , P: %d , N: %b)",
                        result.getUrl(), result.getCategoryLinks(), result.getSubcategoryLinks(), result.getProductLinks(),
                            result.isHasLinkToNextPage()));
            else if(result.getPageType().isProduct())
                LogHelper.getLogger().log(Level.INFO, String.format("PRODUCT | %s (P: %d, SP: %d)",
                        result.getUrl(), result.getProducts(), result.getProductLinks()));
        }
    }
}
