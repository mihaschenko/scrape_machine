package com.scraperservice.scraper;

import com.scraperservice.connection.ConnectionPool;
import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;
import org.jsoup.nodes.Document;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class Scraper {
    public Scraper() {}
    public Scraper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private ConnectionPool connectionPool;

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    private static final ConnectionProperties CONNECTION_PROPERTIES = new ConnectionProperties();

    public abstract Collection<DataArray> scrapeData(PageData pageData);
    public abstract PageType getPageType(PageData pageData);
    public abstract Collection<String> getStartLinks();
    public abstract Collection<String> scrapeCategories(PageData pageData);
    public abstract Collection<String> scrapeSubCategories(PageData pageData);
    public abstract Collection<String> scrapeLinksToProductPages(PageData pageData);
    public abstract String goToNextPage(PageData pageData);
    public ConnectionProperties getDefaultConnectionProperties() { return CONNECTION_PROPERTIES; }
}