package com.scraperservice.connection.setting;

import com.scraperservice.utils.WebDriverUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;

public class WaitDataEvent implements ConnectionEvent {
    private final String cssSelector;

    public WaitDataEvent(String cssSelector) {
        this.cssSelector = cssSelector;
    }

    @Override
    public void event(WebDriver webDriver, String url) {
        WebDriverUtil.wait(webDriver, 10, (driver -> {
            Document document = Jsoup.parse(driver.getPageSource());
            Element element = document.selectFirst(cssSelector);
            if(element == null)
                return true;
            else {
                if(element.text().trim().isEmpty())
                    return false;
                else
                    return true;
            }
        }));
    }
}
