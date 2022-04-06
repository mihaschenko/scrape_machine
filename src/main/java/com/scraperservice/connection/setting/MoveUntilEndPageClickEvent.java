package com.scraperservice.connection.setting;

import com.scraperservice.utils.WebDriverUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

public class MoveUntilEndPageClickEvent implements ConnectionEvent {
    public final String cssSelector;
    public final boolean isUseJavascript;

    public MoveUntilEndPageClickEvent(String cssSelector, boolean isUseJavascript) {
        this.cssSelector = cssSelector;
        this.isUseJavascript = isUseJavascript;
    }

    @Override
    public void event(WebDriver webDriver) {
        try{
            Document document = Jsoup.parse(webDriver.getPageSource());
            if(document.selectFirst(cssSelector) != null)
                WebDriverUtil.moveDownUntilEndOfPageClick(webDriver, cssSelector, isUseJavascript);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
