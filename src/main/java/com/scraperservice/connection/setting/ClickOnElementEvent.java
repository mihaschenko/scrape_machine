package com.scraperservice.connection.setting;

import com.scraperservice.utils.WebDriverUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

public class ClickOnElementEvent implements ConnectionEvent {
    public final String cssSelector;
    public final boolean isUseJavascript;
    public final int delay;

    public ClickOnElementEvent(String cssSelector, boolean isUseJavascript) {
        this(cssSelector, isUseJavascript, 0);
    }
    public ClickOnElementEvent(String cssSelector, boolean isUseJavascript, int delay) {
        this.cssSelector = cssSelector;
        this.isUseJavascript = isUseJavascript;
        this.delay = delay;
    }

    @Override
    public void event(WebDriver webDriver, String url) {
        Document document = Jsoup.parse(webDriver.getPageSource());
        if(document.selectFirst(cssSelector) != null) {
            if(isUseJavascript)
                WebDriverUtil.clickOnElementJavaScript(webDriver, cssSelector);
            else
                WebDriverUtil.clickOnElement(webDriver, cssSelector);
        }
        try{
            Thread.sleep(delay);
        }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
