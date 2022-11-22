package com.scraperservice.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLUtil {
    private String html;

    public HTMLUtil(Document document) {
        this.html = document.outerHtml();
    }

    public HTMLUtil(String html) {
        this.html = html;
    }

    public Document get() { return Jsoup.parse(html); }

    public String toString() {
        return html;
    }

    public HTMLUtil removeTagAndContent(String tagName) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select(tagName);
        if(elements.size() > 0)
            elements.remove();
        html = document.body().html();
        return this;
    }

    public HTMLUtil removeTag(String... tagNames) {
        for(String tagName : tagNames) {
            final Pattern pattern = Pattern.compile("<(/|)" + tagName + "( [^>]+?|)>", Pattern.MULTILINE | Pattern.DOTALL);
            final Matcher matcher = pattern.matcher(html);
            html = matcher.replaceAll("");
        }
        return this;
    }

    public HTMLUtil removeAllTags() {
        final Pattern pattern = Pattern.compile("<(/|)[^>]+>", Pattern.MULTILINE | Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(html);
        html = matcher.replaceAll("");
        return this;
    }

    public HTMLUtil removeLinks() {
        removeTagAndContent("a");
        removeTag("img");
        removeTagAndContent("iframe");
        return this;
    }

    public HTMLUtil removeHTMLComments() {
        final Pattern pattern = Pattern.compile("<!--.*?-->", Pattern.MULTILINE | Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(html);
        html = matcher.replaceAll("");
        return this;
    }

    public HTMLUtil removeEmptyLines() {
        final Pattern pattern = Pattern.compile("(?<=\\n)(\\s+?|)\\n", Pattern.MULTILINE | Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(html);
        html = matcher.replaceAll("");
        return this;
    }

    public HTMLUtil removeHTMLAttributes() {
        final Pattern pattern = Pattern.compile(" [^<]+?(?=>)", Pattern.MULTILINE | Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(html);
        html = matcher.replaceAll("");
        return this;
    }

    public HTMLUtil removeHTMLAttributes(String... attributeNames) {
        for(String atName : attributeNames) {
            final Pattern pattern = Pattern.compile(" " + atName + "(=\"[^\"]*?\"|)", Pattern.MULTILINE | Pattern.DOTALL);
            final Matcher matcher = pattern.matcher(html);
            html = matcher.replaceAll("");
        }
        return this;
    }

    public HTMLUtil removeEmptyTags() {
        boolean compareFound = true;
        Pattern pattern = Pattern.compile("<(?<tag>(?!td)[a-z]+?)( [^>]+?|)>\\s*?</(\\k<tag>)>", Pattern.MULTILINE | Pattern.DOTALL);
        while (compareFound) {
            compareFound = false;
            Matcher matcher = pattern.matcher(html);
            if(matcher.find()) {
                compareFound = true;
                html = matcher.replaceAll("");
            }
        }
        return this;
    }

    public HTMLUtil removeCommentsAttrAndEmptyLines() {
        removeHTMLComments();
        removeHTMLAttributes();
        removeEmptyTags();
        removeEmptyLines();
        return this;
    }

    public HTMLUtil removeCommentsAndEmptyLines() {
        removeHTMLComments();
        removeEmptyTags();
        removeEmptyLines();
        return this;
    }
}
