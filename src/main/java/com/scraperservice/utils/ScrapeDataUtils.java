package com.scraperservice.utils;

import org.jsoup.nodes.Document;

public class ScrapeDataUtils {
    public static String getNextPageLink(Document document, String cssSelector, String baseSiteUrl) {
        return ScrapeUtils.joinBaseUrlAndLink(baseSiteUrl,
                ScrapeUtils.getAttribute(document, cssSelector, "href"));
    }

    private ScrapeDataUtils() {}
}
