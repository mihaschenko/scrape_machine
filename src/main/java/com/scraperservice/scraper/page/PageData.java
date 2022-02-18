package com.scraperservice.scraper.page;

import org.jsoup.nodes.Document;

public class PageData {
    public PageData() {}
    public PageData(String url) { this.url = url; }

    public String url;
    public PageType pageType;
    public Document html;
}
