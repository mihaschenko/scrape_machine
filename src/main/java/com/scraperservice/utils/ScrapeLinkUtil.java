package com.scraperservice.utils;

import org.jsoup.nodes.Element;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ScrapeLinkUtil {
    private static final String CACHE_REGEX = "(?<=\\/cache\\/)[^\\/]+";

    public static String scrapeLink(Element document, String cssSelector, String baseSiteUrl) {
        return scrapeAttributeWithLink(document, cssSelector, baseSiteUrl, "href");
    }

    public static Collection<String> scrapeLinks(Element document, String cssSelector, String baseSiteUrl) {
        return scrapeAttributesWithLink(document, cssSelector, baseSiteUrl, "href");
    }

    public static String scrapeImage(Element document, String cssSelector, String baseSiteUrl) {
        return scrapeAttributeWithLink(document, cssSelector, baseSiteUrl, "src");
    }

    public static Collection<String> scrapeImages(Element document, String cssSelector, String baseSiteUrl) {
        return scrapeAttributesWithLink(document, cssSelector, baseSiteUrl, "src");
    }

    public static Collection<String> scrapeImagesWithCache(List<String> imagesWithReplaceableCache, List<String> imagesWithReplacementCache) {
        if(imagesWithReplaceableCache.size() == 0)
            return imagesWithReplacementCache;
        else if(imagesWithReplacementCache.size() > 0)
            return changeLinkCache(imagesWithReplaceableCache, parseCache(imagesWithReplacementCache.get(0)));
        else
            return Collections.emptySet();
    }

    private static Collection<String> scrapeAttributesWithLink(Element document, String cssSelector, String baseSiteUrl, String attribute) {
        return ScrapeUtil.getAttributes(document, cssSelector, attribute)
                .stream().distinct().map(link -> ScrapeUtil.joinBaseUrlAndLink(baseSiteUrl, link))
                .collect(Collectors.toSet());
    }

    private static String scrapeAttributeWithLink(Element document, String cssSelector, String baseSiteUrl, String attribute) {
        return ScrapeUtil.joinBaseUrlAndLink(baseSiteUrl,
                ScrapeUtil.getAttribute(document, cssSelector, attribute));
    }

    public static String parseCache(String link) {
        return RegexUtil.findText(CACHE_REGEX, link);
    }

    public static String changeLinkCache(String link, String cache) {
        return changeLinkCache(Collections.singletonList(link), cache).get(0);
    }

    public static List<String> changeLinkCache(List<String> links, String cache) {
        if(cache.isEmpty())
            return links;
        return links.stream().map(link -> link.replaceFirst(CACHE_REGEX, cache))
                .collect(Collectors.toList());
    }

    private ScrapeLinkUtil() {}
}
