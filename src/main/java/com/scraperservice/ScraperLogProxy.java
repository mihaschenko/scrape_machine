package com.scraperservice;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.helper.LogHelper;
import com.scraperservice.manager.StatisticManager;
import com.scraperservice.scraper.Scraper;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;

import java.util.List;
import java.util.logging.Logger;

public class ScraperLogProxy extends Scraper {
    private final Scraper scraper;
    private final StatisticManager statisticManager;
    private final Logger logger;

    public ScraperLogProxy(Scraper scraper) {
        this.scraper = scraper;
        statisticManager = StatisticManager.getInstance();
        logger = LogHelper.getLogger();
    }

    @Override
    public List<DataArray> scrapeData(PageData pageData) {
        List<DataArray> result = scraper.scrapeData(pageData);
        statisticManager.recordProductDataStatistic(result);
        //logger.log(Level.FINEST, "scrape product data (" + (result != null ? result.size() : 0) + ")");
        return result;
    }

    @Override
    public PageType getPageType(PageData pageData) {
        PageType result = scraper.getPageType(pageData);
        statisticManager.addPageTypeCounter(result, 1);
        //logger.log(Level.FINEST, "PAGE TYPE (" + result + ")");
        return result;
    }

    @Override
    public List<String> getStartLinks() {
        List<String> result = scraper.getStartLinks();
        statisticManager.addLinkAmountCounter((result != null ? result.size() : 0));
        return result;
    }

    @Override
    public List<String> scrapeCategories(PageData pageData) {
        List<String> result = scraper.scrapeCategories(pageData);
        statisticManager.addLinkAmountCounter(result != null ? result.size() : 0);
        //logger.log(Level.FINEST, "scrape categories (" + (result != null ? result.size() : 0) + ")");
        return result;
    }

    @Override
    public List<String> scrapeSubCategories(PageData pageData) {
        List<String> result = scraper.scrapeSubCategories(pageData);
        statisticManager.addLinkAmountCounter(result != null ? result.size() : 0);
        //logger.log(Level.FINEST, "scrape subcategories (" + (result != null ? result.size() : 0) + ")");
        return result;
    }

    @Override
    public List<String> scrapeLinksToProductPages(PageData pageData) {
        List<String> result = scraper.scrapeLinksToProductPages(pageData);
        statisticManager.addLinkAmountCounter(result != null ? result.size() : 0);
        //logger.log(Level.FINEST, "scrape product pages (" + (result != null ? result.size() : 0) + ")");
        return result;
    }

    @Override
    public String goToNextPage(PageData pageData) {
        String result = scraper.goToNextPage(pageData);
        statisticManager.addLinkAmountCounter(result != null && !result.isEmpty() ? 1 : 0);
        //logger.log(Level.FINEST, "scrape link to next page (" + (result != null ? result : "null") + ")");
        return result;
    }

    @Override
    public ConnectionProperties getDefaultConnectionProperties() {
        return scraper.getDefaultConnectionProperties();
    }
}
