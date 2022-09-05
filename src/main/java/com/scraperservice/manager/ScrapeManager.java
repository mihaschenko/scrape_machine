package com.scraperservice.manager;

import com.scraperservice.UniqueValuesStorage;
import com.scraperservice.exception.UndefinedPageException;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.view.StatisticFrameBuilder;
import com.scraperservice.connection.Connection;
import com.scraperservice.connection.ConnectionPool;
import com.scraperservice.exception.DoNotHaveAnyProductLinksException;
import com.scraperservice.helper.LogHelper;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.scraper.Scraper;
import com.scraperservice.storage.DataArray;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final UniqueValuesStorage uniqueValuesStorage;
    @Autowired
    private final ConnectionPool connectionPool;
    @Autowired
    private final ExecutorService taskPool;
    @Autowired
    private final StatisticFrameBuilder statisticFrameManager;
    @Autowired
    @Qualifier("blockingQueue")
    private final BlockingQueue<Runnable> runnableBlockingQueue;
    private final AtomicInteger workStatus = new AtomicInteger(0);
    private static final int NOTHING_WORKS = 0;

    @Override
    public void run() {
        // Init scraper's start links
        try {
            if(uniqueValuesStorage.isEmpty())
                uniqueValuesStorage.addAll(scraper.getStartLinks());
        }
        catch (Exception e) {
            LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
            throw new UnsupportedOperationException(e);
        }

        // Scraper main part
        while((workStatus.get() != NOTHING_WORKS || !uniqueValuesStorage.isEmpty())
                && !Thread.currentThread().isInterrupted()) {
            if(runnableBlockingQueue.remainingCapacity() == 0) {
                try{ Thread.sleep(1000); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                continue;
            }

            final String link = uniqueValuesStorage.poll();
            if(link != null && !link.isEmpty()) {
                workStatus.incrementAndGet();
                taskPool.execute(() -> {
                    PageData pageData = new PageData(link);
                    try {
                        Connection connection = connectionPool.acquire();
                        try{
                            connection.getPage(pageData, scraper.getDefaultConnectionProperties());
                            pageData.setPageType(scraper.getPageType(pageData));

                            if(pageData.getPageType() == PageType.UNDEFINED)
                                throw new UndefinedPageException();
                        }
                        finally {
                            connectionPool.release(connection);
                        }

                        if(pageData.getPageType().isCategory()) {
                            Collection<String> category = scraper.scrapeCategories(pageData);
                            Collection<String> subCategory = scraper.scrapeSubCategories(pageData);
                            Collection<String> products = scraper.scrapeLinksToProductPages(pageData);
                            boolean isPageHasLinks = false;
                            if (category != null && category.size() > 0) {
                                uniqueValuesStorage.addAll(category);
                                pageData.setCategoryLinks(category.size());
                                isPageHasLinks = true;
                            }
                            if (subCategory != null && subCategory.size() > 0) {
                                uniqueValuesStorage.addAll(subCategory);
                                pageData.setSubcategoryLinks(subCategory.size());
                                isPageHasLinks = true;
                            }
                            if (products != null && products.size() > 0) {
                                uniqueValuesStorage.addAll(products);
                                pageData.setProductLinks(products.size());
                                isPageHasLinks = true;
                            }
                            if(!isPageHasLinks && pageData.getPageType() != PageType.CATEGORY_AND_PRODUCT_PAGE)
                                throw new DoNotHaveAnyProductLinksException();

                            String nextPage = scraper.goToNextPage(pageData);
                            if (nextPage != null && !nextPage.isEmpty()) {
                                pageData.setHasLinkToNextPage(true);
                                uniqueValuesStorage.add(nextPage);
                            }
                        }

                        if(pageData.getPageType().isProduct()) {
                            Collection<DataArray> result = scraper.scrapeData(pageData);
                            pageData.setProducts(result.size());
                            dataSaveManager.save(result
                                    .stream().filter(DataArray::checkAllNecessaryCells).collect(Collectors.toList()));
                        }
                        logCompleteStatus(pageData);
                    }
                    catch (Exception e) {
                        logException(pageData, e);
                    }
                    workStatus.decrementAndGet();
                });
            }
        }

        taskPool.shutdown();
        connectionPool.close();
        LogHelper.getLogger().log(Level.INFO, StatisticManager.getInstance().toString());
    }

    private void logException(PageData result, Throwable exception) {
        if(exception != null) {
            if(exception.getClass() == DoNotHaveAnyProductLinksException.class)
                LogHelper.getLogger().log(Level.WARNING, "DO_NOT_HAVE_ANY_PRODUCT_LINKS_EXCEPTION: " + result.getUrl());
            else if(exception.getClass() == UndefinedPageException.class)
                LogHelper.getLogger().log(Level.WARNING, "UNDEFINED: " + result.getUrl());
            else
                LogHelper.getLogger().log(Level.SEVERE, exception.getClass().getSimpleName() + ": " + result.getUrl()
                        + " (" + exception.getMessage() + ")", exception);
        }
    }

    private void logCompleteStatus(PageData result) {
        if(result != null) {
            if(result.getPageType().isCategory())
                LogHelper.getLogger().log(Level.INFO, String.format("CATEGORY | %s (C: %d, SB: %d , P: %d , N: %b)",
                        result.getUrl(), result.getCategoryLinks(), result.getSubcategoryLinks(), result.getProductLinks(),
                            result.isHasLinkToNextPage()));
            if(result.getPageType().isProduct())
                LogHelper.getLogger().log(Level.INFO, String.format("PRODUCT | %s (P: %d, SP: %d)",
                        result.getUrl(), result.getProducts(), result.getProductLinks()));
        }
    }
}