package com.scraperservice.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLUtils {
    private String html;

    public HTMLUtils(Document document) {
        this.html = document.outerHtml();
    }

    public HTMLUtils(String html) {
        this.html = html;
    }

    public Document get() { return Jsoup.parse(html); }

    public String toString() {
        return html;
    }

    public HTMLUtils removeTagAndContent(String tagName) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select(tagName);
        if(elements.size() > 0)
            elements.remove();
        html = document.outerHtml();
        return this;
    }

    public HTMLUtils removeTag(String... tagNames) {
        for(String tagName : tagNames) {
            final Pattern pattern = Pattern.compile("<(/|)" + tagName + "( [^>]+?|)>", Pattern.MULTILINE | Pattern.DOTALL);
            final Matcher matcher = pattern.matcher(html);
            html = matcher.replaceAll("");
        }
        return this;
    }

    public HTMLUtils removeLinks() {
        removeTagAndContent("a");
        removeTag("img");
        removeTagAndContent("iframe");
        return this;
    }

    public HTMLUtils removeHTMLComments() {
        final Pattern pattern = Pattern.compile("<!--.+?-->", Pattern.MULTILINE | Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(html);
        html = matcher.replaceAll("");
        return this;
    }

    public HTMLUtils removeEmptyLines() {
        final Pattern pattern = Pattern.compile("(?<=\\n)(\\s+?|)\\n", Pattern.MULTILINE | Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(html);
        html = matcher.replaceAll("");
        return this;
    }

    public HTMLUtils removeHTMLAttributes() {
        final Pattern pattern = Pattern.compile(" [^<]+?(?=>)", Pattern.MULTILINE | Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(html);
        html = matcher.replaceAll("");
        return this;
    }

    public HTMLUtils removeHTMLAttributes(String... attributeNames) {
        for(String atName : attributeNames) {
            final Pattern pattern = Pattern.compile(" " + atName + "(=\"[^\"]*?\"|)", Pattern.MULTILINE | Pattern.DOTALL);
            final Matcher matcher = pattern.matcher(html);
            html = matcher.replaceAll("");
        }
        return this;
    }

    public HTMLUtils removeEmptyTags() {
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

    public HTMLUtils removeCommentsAttrAndEmptyLines() {
        removeHTMLComments();
        removeHTMLAttributes();
        removeEmptyTags();
        removeEmptyLines();
        return this;
    }

    public HTMLUtils removeCommentsAndEmptyLines() {
        removeHTMLComments();
        removeEmptyTags();
        removeEmptyLines();
        return this;
    }
}
