package com.scraperservice.connection.setting;

import com.scraperservice.utils.ClickOnElementUtil;
import com.scraperservice.utils.WebDriverUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

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
            ClickOnElementUtil.prepare((ChromeDriver) webDriver, cssSelector).setUseJavascript(isUseJavascript)
                    .click();
        }
        try{
            Thread.sleep(delay);
        }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
