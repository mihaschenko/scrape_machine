package com.scraperservice.connection;

import com.scraperservice.ChromeDriverFactory;
import com.scraperservice.connection.setting.ConnectionSetting;
import com.scraperservice.helper.LogHelper;
import com.scraperservice.utils.WebDriverUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Map;
import java.util.logging.Level;

public class SeleniumConnection extends Connection {
    private final ChromeDriverFactory driverFactory;
    private ChromeDriver driver;

    public SeleniumConnection() {
        this.driverFactory = ChromeDriverFactory.getInstance();
    }

    public ChromeDriver getDriver() { return driver; }

    @Override
    public synchronized Document getPage(String url, ConnectionSetting setting) {
        if(driver == null)
            driver = driverFactory.getChromeDriver();
        if(setting == null)
            throw new NullPointerException("ConnectionSetting setting = null");

        if(setting.getCookie() != null && setting.getCookie().size() > 0) {
            for(Map.Entry<String, String> c : setting.getCookie().entrySet()) {
                if(driver.manage().getCookieNamed(c.getKey()) == null
                        || !driver.manage().getCookieNamed(c.getKey()).getValue().equals(c.getValue()))
                    driver.manage().addCookie(new Cookie(c.getKey(), c.getValue()));
            }
        }

        driver.navigate().to(url);

        try{
            WebDriverUtils.waitPageStateComplete(driver, 120);
            WebDriverUtils.waitJQuery(driver, 120);
            if(setting.getWaitForIt() != null && setting.getWaitForIt().size() > 0) {
                for(String waitForIt : setting.getWaitForIt())
                    WebDriverUtils.waitElement(driver, waitForIt, 20);
            }
        }
        catch (JavascriptException e) {
            LogHelper.getLogger().log(Level.SEVERE, "exception while SeleniumConnection", e);
        }
        catch (Exception e) {
            LogHelper.getLogger().log(Level.WARNING, "selenium wait exception");
        }

        if(setting.getEvents().size() > 0)
            setting.getEvents().forEach(connectionEvent -> connectionEvent.event(driver));

        return Jsoup.parse(driver.getPageSource());
    }

    @Override
    public void close() throws Exception {
        if(driver != null)
            driver.quit();
    }
}
