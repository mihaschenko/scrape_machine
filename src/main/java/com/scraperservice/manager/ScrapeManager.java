package com.scraperservice.manager;

import com.scraperservice.ScraperSetting;
import com.scraperservice.connection.Connection;
import com.scraperservice.connection.pool.ConnectionPool;
import com.scraperservice.exception.DoNotHaveAnyProductLinksException;
import com.scraperservice.helper.LogHelper;
import com.scraperservice.ScraperLogProxy;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.queue.ConcurrentLinkedQueueUnique;
import com.scraperservice.scraper.Scraper;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.writer.CSVDataWriter;

import java.io.FileReader;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ScrapeManager implements Runnable {
    private static ScrapeManager scraperManager;

    public synchronized static ScrapeManager getInstance() {
        if(scraperManager == null) {
            try{
                ScraperSetting scraperSetting = new ScraperSetting();
                scraperSetting.choice();
                scraperManager = new ScrapeManager(scraperSetting);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return scraperManager;
    }

    public synchronized static void init(ScraperSetting scraperSetting) {

    }

    private ScrapeManager(ScraperSetting scraperSetting) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/programSetting.properties"));

        dataSaveManager = new DataSaveManager();
        dataSaveManager.addDataWriter(new CSVDataWriter(scraperSetting.getScraper().getClass().getSimpleName()));

        linksQueue = new ConcurrentLinkedQueueUnique();
        linksQueue.addAll(scraperSetting.getStartLinks());

        scraper = new ScraperLogProxy(scraperSetting.getScraper());
        connectionPool = new ConnectionPool(Integer.parseInt(properties.getProperty("scrape.manager.connection.pool")),
                scraperSetting.getConnectionClass(), new Object[0]);
        taskPool = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty("scrape.manager.task.pool")));
        completableFutureManager = new CompletableFutureManager<>();
        statisticFrameManager = StatisticFrameManager.getInstance();

        proxyManager = scraperSetting.isUseProxy() ? ProxyManager.getInstance() : null;
    }

    private final DataSaveManager dataSaveManager;
    private final Scraper scraper;
    private final ConcurrentLinkedQueueUnique linksQueue;
    private final ConnectionPool connectionPool;
    private final ExecutorService taskPool;
    private final CompletableFutureManager<Void> completableFutureManager;
    private final StatisticFrameManager statisticFrameManager;
    private final ProxyManager proxyManager;

    public ConnectionPool getConnectionPool() {return connectionPool;}

    @Override
    public void run() {
        Thread completableFutureManagerThread = new Thread(completableFutureManager);
        completableFutureManagerThread.setDaemon(true);
        completableFutureManagerThread.start();
        if(proxyManager != null)
            proxyManager.setProxyProperties();

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
                        pageData.pageType = scraper.getPageType(pageData.html);
                        if(pageData.pageType == PageType.UNDEFINED)
                            LogHelper.getLogger().log(Level.WARNING, "UNDEFINED: " + link);

                        return pageData;
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                }, taskPool);

                CompletableFuture<Void> categoryCompletableFuture = pageDataFuture.thenApplyAsync(pageData -> {
                    try{
                        if(pageData.pageType.isCategory()) {
                            List<String> category = scraper.scrapeCategories(pageData.html);
                            if (category != null && category.size() > 0)
                                linksQueue.addAll(category);
                        }
                        return pageData;
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                }, taskPool).thenApply(pageData -> {
                    try{
                        if(pageData.pageType.isCategory()) {
                            List<String> subCategory = scraper.scrapeSubCategories(pageData.html);
                            if (subCategory != null && subCategory.size() > 0)
                                linksQueue.addAll(subCategory);
                        }
                        return pageData;
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                }).thenApply(pageData -> {
                    try{
                        if(pageData.pageType.isCategory()) {
                            List<String> products = scraper.scrapeLinksToProductPages(pageData.html);
                            if (products != null && products.size() > 0)
                                linksQueue.addAll(products);
                            else
                                throw new DoNotHaveAnyProductLinksException();
                        }
                        return pageData;
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                }).thenAccept(pageData -> {
                    try{
                        if(pageData.pageType.isCategory()) {
                            String nextPageUrlFirstVariant = scraper.goToNextPage(pageData.html);
                            String nextPageUrlSecondVariant = scraper.goToNextPage(pageData.url);
                            if (nextPageUrlFirstVariant != null && !nextPageUrlFirstVariant.isEmpty())
                                linksQueue.add(nextPageUrlFirstVariant);
                            if (nextPageUrlSecondVariant != null && !nextPageUrlSecondVariant.isEmpty())
                                linksQueue.add(nextPageUrlSecondVariant);
                        }
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
                        throw new IllegalStateException(e);
                    }
                });

                CompletableFuture<Void> productCompletableFuture = pageDataFuture.thenAcceptAsync(pageData -> {
                    try{
                        if(pageData.pageType.isProduct())
                            dataSaveManager.writeDataArray(scraper.scrapeData(pageData.html, pageData.url)
                                    .stream().filter(DataArray::checkAllNecessaryCells).collect(Collectors.toList()));
                    }
                    catch (Exception e) {
                        LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
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
