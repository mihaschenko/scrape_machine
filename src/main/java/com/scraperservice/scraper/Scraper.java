package com.scraperservice.scraper;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;
import org.jsoup.nodes.Document;

import java.util.List;

public abstract class Scraper {
    public abstract List<DataArray> scrapeData(Document document, String productUrl) throws Exception;
    public abstract PageType getPageType(Document document) throws Exception;
    public abstract List<String> getStartLinks() throws Exception;
    public abstract List<String> scrapeCategories(Document document) throws Exception;
    public abstract List<String> scrapeSubCategories(Document document) throws Exception;
    public abstract List<String> scrapeLinksToProductPages(Document document) throws Exception;
    public abstract String goToNextPage(Document document) throws Exception;
    public abstract String goToNextPage(String url) throws Exception;
    public ConnectionProperties getDefaultConnectionSetting() { return ConnectionProperties.getDEFAULT_CONNECTION_PROPERTIES(); }
}