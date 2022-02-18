package com.scraperservice.manager;

import com.scraperservice.connection.Connection;
import com.scraperservice.connection.pool.ConnectionPool;
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
    private final StatisticFrameManager statisticFrameManager;
    //private final ProxyManager proxyManager;

    @Override
    public void run() {
        Thread completableFutureManagerThread = new Thread(completableFutureManager);
        completableFutureManagerThread.setDaemon(true);
        completableFutureManagerThread.start();
        //if(proxyManager != null)
        //    proxyManager.setProxyProperties();

        if(linksQueue.size() == 0) {
            try {
                linksQueue.addAll(scraper.getStartLinks());
            } catch (Exception e) {
                LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
                throw new UnsupportedOperationException(e);
            }
        }

        while((linksQueue.size() > 0 || completableFutureManager.getAmountCompletableFutureIsWork() > 0)
                && !Thread.currentThread().isInterrupted()) {
            final String link = linksQueue.poll();
            if(link != null) {
                CompletableFuture<PageData> pageDataFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        PageData pageData = new PageData();
                        pageData.url = link;
                        Connection connection = connectionPool.acquire();
                        try{
                            pageData.html = connection.getPage(link, scraper.getDefaultConnectionSetting());
                        }
                        finally {
                            connectionPool.release(connection);
                        }
                        pageData.pageType = scraper.getPageType(pageData);
                        if(pageData.pageType == PageType.UNDEFINED)
                            LogHelper.getLogger().log(Level.WARNING, "UNDEFINED: " + link);
                        else
                            LogHelper.getLogger().log(Level.FINE, pageData.pageType + ": " + link);

                        return pageData;
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, link + " : " + e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                }, taskPool);

                CompletableFuture<Void> categoryCompletableFuture = pageDataFuture.thenApplyAsync(pageData -> {
                    try{
                        if(pageData.pageType.isCategory()) {
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
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, link + " : " + e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                }, taskPool)
                .thenAccept(pageData -> {
                    try{
                        if(pageData.pageType.isCategory()) {
                            String nextPageUrlFirstVariant = scraper.goToNextPage(pageData);
                            if (nextPageUrlFirstVariant != null && !nextPageUrlFirstVariant.isEmpty())
                                linksQueue.add(nextPageUrlFirstVariant);
                        }
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, link + " : " + e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                });

                CompletableFuture<Void> productCompletableFuture = pageDataFuture.thenAcceptAsync(pageData -> {
                    try{
                        if(pageData.pageType.isProduct())
                            dataSaveManager.writeDataArray(scraper.scrapeData(pageData)
                                    .stream().filter(DataArray::checkAllNecessaryCells).collect(Collectors.toList()));
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, link + " : " + e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                }, taskPool);

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
        LogHelper.getLogger().log(Level.INFO, "FINISH SCRAPE");
    }
}
