package com.scraperservice.utils;

import com.scraperservice.scraper.page.PageType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Вспомогательный класс для получения HTML страницы сайта или для получения данных с HTML страницы.
 * Методы предназначены для работы с Jsoup и SeleniumChromeDriver.
 * @author Mihaschenko V.
 */
public class ScrapeUtil {
    public static String iterateUrlGetParameter(String url, String parameterName, int addToIndex) {
        final String regex = "(?<=" + parameterName + "=)[0-9]+";
        String indexStr = RegexUtil.findText(regex, url);
        if(!indexStr.isBlank()) {
            int index = Integer.parseInt(indexStr);
            index += addToIndex;
            return url.replaceFirst(regex, Integer.toString(index));
        }
        return url;
    }

    /**
     * @param document Jsoup Document. Содержит HTML
     * @param selector css селектор
     * @return текст по заданному css селектору или пустую строку
     */
    public static String getText(Element document, String selector) {
        Elements elements = document.select(selector);
        if(elements.size() > 0)
            return elements.get(0).text().trim();
        else
            return "";
    }

    /**
     * @param document Jsoup Document. Содержит HTML
     * @param selector css селектор
     * @return коллекция с текстом всех элементов найденных по заданному css селектору
     */
    public static List<String> getTexts(Element document, String selector) {
        Elements elements = document.select(selector);
        return new ArrayList<>(elements.eachText());
    }

    public static String getOwnText(Element document, String selector) {
        Elements elements = document.select(selector);
        if(elements.size() > 0)
            return elements.get(0).ownText().trim();
        else
            return "";
    }

    public static List<String> getOwnTexts(Element document, String selector) {
        Elements elements = document.select(selector);
        List<String> result = new ArrayList<>();
        for(Element element : elements)
            result.add(element.ownText().trim());
        return result;
    }

    public static void removeElement(Document document, String cssSelector) {
        if(cssSelector == null)
            return;
        Element element = document.selectFirst(cssSelector);
        if(element != null)
            element.remove();
    }

    public static void removeElements(Document document, String cssSelector) {
        if(cssSelector == null)
            return;
        document.select(cssSelector).remove();
    }

    /**
     * @param document Jsoup Document. Содержит HTML
     * @param selector css селектор
     * @param attribute название аттрибута
     * @return значение аттрибута по заданному css селектору или пустую строку
     */
    public static String getAttribute(Element document, String selector, String attribute) {
        Elements elements = document.select(selector);
        if(elements.size() > 0)
            return elements.get(0).attr(attribute).trim();
        else
            return "";
    }

    /**
     * @param document Jsoup Document. Содержит HTML
     * @param selector css селектор
     * @param attribute название аттрибута
     * @return коллекция со значением аттрибута всех элементов найденных по заданному css селектору
     */
    public static List<String> getAttributes(Element document, String selector, String attribute) {
        Elements elements = document.select(selector);
        if(elements.size() > 0)
            return elements.eachAttr(attribute);
        else
            return new ArrayList<>();
    }

    /**
     * @param document Jsoup Document. Содержит HTML
     * @param selector css селектор
     * @return внутренний HTML элемента по заданному css селектору или пустую строку
     */
    public static String getInnerHTML(Element document, String selector) {
        Elements elements = document.select(selector);
        if(elements.size() > 0)
            return elements.get(0).html();
        else
            return "";
    }

    /**
     * @param document Jsoup Document. Содержит HTML
     * @param selector css селектор
     * @return коллекция с внутренним HTML всех элементов найдённых по указанному css селектору
     */
    public static List<String> getInnerHTMLs(Element document, String selector) {
        Elements elements = document.select(selector);
        if(elements.size() > 0) {
            List<String> result = new ArrayList<>();
            for(Element element : elements)
                result.add(element.html());
            return result;
        }
        else
            return new ArrayList<>();
    }

    /**
     * @param document Jsoup Document. Содержит HTML
     * @param selector css селектор
     * @return внешний HTML элемента по заданному css селектору или пустую строку
     */
    public static String getOuterHTML(Element document, String selector) {
        Elements elements = document.select(selector);
        if(elements.size() > 0)
            return elements.get(0).outerHtml();
        else
            return "";
    }

    /**
     * @param document Jsoup Document. Содержит HTML
     * @param selector css селектор
     * @return коллекция с внешним HTML всех элементов найдённых по указанному css селектору
     */
    public static List<String> getOuterHTMLs(Element document, String selector) {
        Elements elements = document.select(selector);
        if(elements.size() > 0) {
            List<String> result = new ArrayList<>();
            for(Element element : elements)
                result.add(element.outerHtml());
            return result;
        }
        else
            return new ArrayList<>();
    }

    /**
     * Объединяет базовую ссылку с относительной. Если в переменной link полная ссылка,
     * объединение не произойдёт
     * @param baseUrl базовая ссылка (https://example.com)
     * @param link относительная ссылка
     * @return полная ссылка
     */
    public static String joinBaseUrlAndLink(String baseUrl, String link) {
        link = link.trim();
        if(!link.startsWith("http") && !link.isEmpty() && !baseUrl.isEmpty()) {
            if(!link.startsWith("/") && !baseUrl.endsWith("/"))
                link = baseUrl + "/" + link;
            else if(link.startsWith("/") && baseUrl.endsWith("/")) {
                link = link.replaceFirst("/", "");
                link = baseUrl + link;
            }
            else
                link = baseUrl + link;
        }
        return link;
    }

    public static List<String> joinBaseUrlAndLinkList(String baseUrl, List<String> links) {
        return links.stream().map(link -> joinBaseUrlAndLink(baseUrl, link)).collect(Collectors.toList());
    }

    public static String iterateLinkAttribute(String link, String attributeName, int i) {
        final String regex = String.format("(?<=%s)[0-9]+", attributeName);
        String pageStr = RegexUtil.findText(regex, link);
        if(!pageStr.isEmpty()) {
            int page = Integer.parseInt(pageStr);
            page+=i;
            return link.replaceFirst(regex, Integer.toString(page));
        }
        return link;
    }

    public static Map<String, Element> parseTabList(Element document, String tabListHeadSelector, String tabListBodySelector) {
        Map<String, Element> result = new HashMap<>();
        List<String> head = getTexts(document, tabListHeadSelector);
        if(head.size() > 0) {
            Elements body = document.select(tabListBodySelector);
            if(head.size() == body.size()) {
                for(int i = 0; i < head.size(); i++)
                    result.put(head.get(i).trim(), body.get(i));
            }
        }

        return result;
    }

    public static String iteratePageLink(String url, String parameterName) {
        if(url == null || url.isEmpty())
            throw new IllegalArgumentException("url is null or empty = " + url);
        if(parameterName == null || parameterName.isEmpty())
            throw new IllegalArgumentException("parameterName is null or empty = " + parameterName);
        if(url.contains(parameterName)) {
            final String regex = "(?<=" + parameterName + "=)[0-9]+";
            String parameterValueStr = RegexUtil.findText(regex, url);
            if(!parameterValueStr.isEmpty()) {
                int parameterValue = Integer.parseInt(parameterValueStr);
                parameterValue++;
                return url.replaceFirst(regex, Integer.toString(parameterValue));
            }
            else
                throw new IllegalArgumentException("parameter must to contains figures");
        }
        else if(url.contains("?"))
            return url + "&" + parameterName + "=2";
        else
            return url + "?" + parameterName + "=2";
    }

    protected static PageType initPageType(Document document,
                                           String categoryCssSelector, String productPageCssSelector) {
        if(document.selectFirst(productPageCssSelector) != null)
            return PageType.PRODUCT_PAGE;
        else if(document.selectFirst(categoryCssSelector) != null)
            return PageType.CATEGORY_PAGE;
        else
            return PageType.UNDEFINED;
    }

    public static String getBaseSiteUrl(String url) {
        return RegexUtil.findText("http(s|):\\/\\/[^\\/]+", url);
    }

    public static Document getDocument(ChromeDriver driver) {
        Document document = Jsoup.parse(driver.getPageSource());
        document.setBaseUri(ScrapeUtil.getBaseSiteUrl(driver.getCurrentUrl()));
        return document;
    }

    private ScrapeUtil() {}
}
