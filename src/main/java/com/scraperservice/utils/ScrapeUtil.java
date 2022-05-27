package com.scraperservice.utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    private ScrapeUtil() {}
}
