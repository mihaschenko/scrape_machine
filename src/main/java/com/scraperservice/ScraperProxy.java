package com.scraperservice;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.manager.StatisticManager;
import com.scraperservice.scraper.Scraper;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;

import java.util.*;

public class ScraperProxy extends Scraper {
    private final Scraper scraper;
    private final StatisticManager statisticManager;

    public ScraperProxy(Scraper scraper, StatisticManager statisticManager) {
        this.scraper = scraper;
        this.statisticManager = statisticManager;
    }

    @Override
    public Collection<DataArray> scrapeData(PageData pageData) {
        return scraper.scrapeData(pageData);
    }

    @Override
    public PageType getPageType(PageData pageData) {
        PageType result = scraper.getPageType(pageData);
        statisticManager.addPageTypeCounter(result, 1);
        return result;
    }

    @Override
    public Collection<String> getStartLinks() {
        return scraper.getStartLinks();
    }

    @Override
    public Collection<String> scrapeCategories(PageData pageData) {
        return scraper.scrapeCategories(pageData);
    }

    @Override
    public Collection<String> scrapeSubCategories(PageData pageData) {
        return scraper.scrapeSubCategories(pageData);
    }

    @Override
    public Collection<String> scrapeLinksToProductPages(PageData pageData) {
        return scraper.scrapeLinksToProductPages(pageData);
    }

    @Override
    public String goToNextPage(PageData pageData) {
        return scraper.goToNextPage(pageData);
    }

    @Override
    public ConnectionProperties getDefaultConnectionProperties() {
        return scraper.getDefaultConnectionProperties();
    }
}
