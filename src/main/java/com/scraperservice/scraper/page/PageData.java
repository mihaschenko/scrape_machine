package com.scraperservice.scraper.page;

import lombok.Data;
import org.jsoup.nodes.Document;

import java.util.Map;

@Data
public class PageData {
    public PageData() {}
    public PageData(String url) { this(url, null, null); }
    public PageData(String url, PageType pageType) { this(url, pageType, null); }
    public PageData(PageData pageData) { this(pageData.url, pageData.pageType, pageData.html); }
    public PageData(String url, PageType pageType, Document html) {
        this.url = url;
        this.pageType = pageType;
        this.html = html;
    }

    private String url;
    private PageType pageType;
    private Document html;
    private int categoryLinks = 0;
    private int subcategoryLinks = 0;
    private int productLinks = 0;
    private boolean hasLinkToNextPage = false;
    private int products = 0;
}
