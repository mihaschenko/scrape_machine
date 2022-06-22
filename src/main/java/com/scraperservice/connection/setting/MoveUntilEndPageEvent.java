package com.scraperservice.connection.setting;

import com.scraperservice.utils.WebDriverUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

public class MoveUntilEndPageEvent implements ConnectionEvent {
    public final String cssSelector;

    public MoveUntilEndPageEvent(String cssSelector) {
        this.cssSelector = cssSelector;
    }

    @Override
    public void event(WebDriver webDriver, String url) {
        try{
            Document document = Jsoup.parse(webDriver.getPageSource());
            if(document.selectFirst(cssSelector) != null)
                WebDriverUtil.moveDownUntilEndOfPage(webDriver, cssSelector);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
