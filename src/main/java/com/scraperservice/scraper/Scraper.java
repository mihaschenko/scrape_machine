package com.scraperservice.scraper;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;
import org.jsoup.nodes.Document;

import java.util.List;

public abstract class Scraper {
    public abstract List<DataArray> scrapeData(PageData pageData) throws Exception;
    public abstract PageType getPageType(PageData pageData) throws Exception;
    public abstract List<String> getStartLinks() throws Exception;
    public abstract List<String> scrapeCategories(PageData pageData) throws Exception;
    public abstract List<String> scrapeSubCategories(PageData pageData) throws Exception;
    public abstract List<String> scrapeLinksToProductPages(PageData pageData) throws Exception;
    public abstract String goToNextPage(PageData pageData) throws Exception;
    public ConnectionProperties getDefaultConnectionSetting() { return ConnectionProperties.getDEFAULT_CONNECTION_PROPERTIES(); }
}