package com.scraperservice.scraper;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;

import java.util.List;

public abstract class Scraper {
    public abstract List<DataArray> scrapeData(PageData pageData);
    public abstract PageType getPageType(PageData pageData);
    public abstract List<String> getStartLinks();
    public abstract List<String> scrapeCategories(PageData pageData);
    public abstract List<String> scrapeSubCategories(PageData pageData);
    public abstract List<String> scrapeLinksToProductPages(PageData pageData);
    public abstract String goToNextPage(PageData pageData);
    public ConnectionProperties getDefaultConnectionProperties() { return new ConnectionProperties(); }
}