package com.scraperservice;

import com.scraperservice.connection.ConnectionPool;
import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.manager.StatisticManager;
import com.scraperservice.scraper.Scraper;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;

import java.util.*;

public class ScraperLogProxy extends Scraper {
    private final Scraper scraper;
    private final StatisticManager statisticManager;

    public ScraperLogProxy(Scraper scraper) {
        this.scraper = scraper;
        statisticManager = StatisticManager.getInstance();
    }

    @Override
    public Collection<DataArray> scrapeData(PageData pageData) {
        Collection<DataArray> result = scraper.scrapeData(pageData);
        statisticManager.recordProductDataStatistic(result);
        return result;
    }

    @Override
    public PageType getPageType(PageData pageData) {
        PageType result = scraper.getPageType(pageData);
        statisticManager.addPageTypeCounter(result, 1);
        return result;
    }

    @Override
    public Collection<String> getStartLinks() {
        Collection<String> result = scraper.getStartLinks();
        statisticManager.addLinkAmountCounter((result != null ? result.size() : 0));
        return result;
    }

    @Override
    public Collection<String> scrapeCategories(PageData pageData) {
        Collection<String> result = scraper.scrapeCategories(pageData);
        statisticManager.addLinkAmountCounter(result != null ? result.size() : 0);
        return result;
    }

    @Override
    public Collection<String> scrapeSubCategories(PageData pageData) {
        Collection<String> result = scraper.scrapeSubCategories(pageData);
        statisticManager.addLinkAmountCounter(result != null ? result.size() : 0);
        return result;
    }

    @Override
    public Collection<String> scrapeLinksToProductPages(PageData pageData) {
        Collection<String> result = scraper.scrapeLinksToProductPages(pageData);
        statisticManager.addLinkAmountCounter(result != null ? result.size() : 0);
        return result;
    }

    @Override
    public String goToNextPage(PageData pageData) {
        String result = scraper.goToNextPage(pageData);
        statisticManager.addLinkAmountCounter(result != null && !result.isEmpty() ? 1 : 0);
        return result;
    }

    @Override
    public ConnectionProperties getDefaultConnectionProperties() {
        return scraper.getDefaultConnectionProperties();
    }
}
