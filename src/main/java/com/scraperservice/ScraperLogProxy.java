package com.scraperservice;

import com.scraperservice.connection.setting.ConnectionSetting;
import com.scraperservice.helper.LogHelper;
import com.scraperservice.manager.StatisticManager;
import com.scraperservice.scraper.Scraper;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;
import org.jsoup.nodes.Document;

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
    public List<DataArray> scrapeData(Document document, String productUrl) throws Exception {
        List<DataArray> result = scraper.scrapeData(document, productUrl);
        statisticManager.recordProductDataStatistic(result);
        //logger.log(Level.FINEST, "scrape product data (" + (result != null ? result.size() : 0) + ")");
        return result;
    }

    @Override
    public PageType getPageType(Document document) throws Exception {
        PageType result = scraper.getPageType(document);
        statisticManager.addPageTypeCounter(result, 1);
        //logger.log(Level.FINEST, "PAGE TYPE (" + result + ")");
        return result;
    }

    @Override
    public List<String> getStartLinks() throws Exception {
        List<String> result = scraper.getStartLinks();
        statisticManager.addLinkAmountCounter((result != null ? result.size() : 0));
        return result;
    }

    @Override
    public List<String> scrapeCategories(Document document) throws Exception {
        List<String> result = scraper.scrapeCategories(document);
        statisticManager.addLinkAmountCounter(result != null ? result.size() : 0);
        //logger.log(Level.FINEST, "scrape categories (" + (result != null ? result.size() : 0) + ")");
        return result;
    }

    @Override
    public List<String> scrapeSubCategories(Document document) throws Exception {
        List<String> result = scraper.scrapeSubCategories(document);
        statisticManager.addLinkAmountCounter(result != null ? result.size() : 0);
        //logger.log(Level.FINEST, "scrape subcategories (" + (result != null ? result.size() : 0) + ")");
        return result;
    }

    @Override
    public List<String> scrapeLinksToProductPages(Document document) throws Exception {
        List<String> result = scraper.scrapeLinksToProductPages(document);
        statisticManager.addLinkAmountCounter(result != null ? result.size() : 0);
        //logger.log(Level.FINEST, "scrape product pages (" + (result != null ? result.size() : 0) + ")");
        return result;
    }

    @Override
    public String goToNextPage(Document document) throws Exception {
        String result = scraper.goToNextPage(document);
        statisticManager.addLinkAmountCounter(result != null && !result.isEmpty() ? 1 : 0);
        //logger.log(Level.FINEST, "scrape link to next page (" + (result != null ? result : "null") + ")");
        return result;
    }

    @Override
    public String goToNextPage(String url) throws Exception {
        String result = scraper.goToNextPage(url);
        statisticManager.addLinkAmountCounter(result != null && !result.isEmpty() ? 1 : 0);
        //logger.log(Level.FINEST, "scrape link to next page (" + (result != null ? result : "null") + ")");
        return result;
    }

    @Override
    public ConnectionSetting getDefaultConnectionSetting() {
        return scraper.getDefaultConnectionSetting();
    }
}
