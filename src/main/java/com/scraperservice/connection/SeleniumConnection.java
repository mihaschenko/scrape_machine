package com.scraperservice.connection;

import com.scraperservice.ChromeDriverFactory;
import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.scraper.helper.LogHelper;
import com.scraperservice.utils.WebDriverUtil;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Map;
import java.util.logging.Level;

@Data
public class SeleniumConnection extends Connection {
    private final ChromeDriverFactory driverFactory;
    private ChromeDriver driver;

    public SeleniumConnection() {
        this.driverFactory = ChromeDriverFactory.getInstance();
    }

    @Override
    public synchronized Document getPage(String url, ConnectionProperties setting) {
        if(driver == null)
            driver = driverFactory.getChromeDriver();

        driver.navigate().to(url);

        if(setting.getCookie() != null && setting.getCookie().size() > 0) {
            for(Map.Entry<String, String> c : setting.getCookie().entrySet())
                driver.manage().addCookie(new Cookie(c.getKey(), c.getValue()));
            driver.navigate().refresh(); // CHECK THIS STEP
        }

        try{
            WebDriverUtil.waitPageStateComplete(driver, 10);
            if(setting.getWaitForIt() != null && setting.getWaitForIt().size() > 0)
                WebDriverUtil.waitElement(driver, String.join(", ", setting.getWaitForIt()), 30);
        }
        catch (JavascriptException e) {
            LogHelper.getLogger().log(Level.SEVERE, "exception while SeleniumConnection", e);
        }
        catch (Exception e) {
            LogHelper.getLogger().log(Level.WARNING, "selenium wait exception", e);
        }

        try {
            WebDriverUtil.waitJQuery(driver, 5);
        }
        catch (Exception ignore) {}

        if(setting.getEvents().size() > 0)
            setting.getEvents().forEach(connectionEvent -> connectionEvent.event(driver));

        if(setting.getDelay() > 0) {
            try {
                Thread.sleep(setting.getDelay());
            }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        return Jsoup.parse(driver.getPageSource());
    }

    @Override
    public void close() {
        if(driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
