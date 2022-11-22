package com.scraperservice.connection.setting;

import com.scraperservice.utils.ClickOnElementUtil;
import com.scraperservice.utils.WebDriverUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class MoveUntilEndPageClickEvent implements ConnectionEvent {
    public final String cssSelector;
    public final boolean isUseJavascript;

    public MoveUntilEndPageClickEvent(String cssSelector, boolean isUseJavascript) {
        this.cssSelector = cssSelector;
        this.isUseJavascript = isUseJavascript;
    }

    @Override
    public void event(WebDriver webDriver, String url) {
        Document document = Jsoup.parse(webDriver.getPageSource());
        if(document.selectFirst(cssSelector) != null) {
            ClickOnElementUtil.prepare((ChromeDriver) webDriver, cssSelector)
                    .setClickOnEverything(true).setUseJavascript(isUseJavascript).click();
        }
        WebDriverUtil.moveDownToEndOfPage(webDriver);
    }
}
