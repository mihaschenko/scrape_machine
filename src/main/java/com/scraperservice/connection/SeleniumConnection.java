package com.scraperservice.connection;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.helper.LogHelper;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.utils.ScrapeUtil;
import com.scraperservice.utils.WebDriverUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

@Component
@Scope("prototype")
public class SeleniumConnection extends Connection<WebDriver> {
    private final ChromeDriverFactory driverFactory;
    private ChromeDriver driver;

    public ChromeDriver getDriver() {
        return driver;
    }
    public ChromeDriverFactory getDriverFactory() {
        return driverFactory;
    }

    @Deprecated
    public SeleniumConnection() {
        this(null);
    }

    @Autowired
    public SeleniumConnection(ChromeDriverFactory chromeDriverFactory) {
        this.driverFactory = chromeDriverFactory;
    }

    @PostConstruct
    private void init() {
        if(driver == null)
            driver = driverFactory.getChromeDriver();
    }

    @Override
    public synchronized Document getPage(String url, ConnectionProperties setting) {
        PageData pageData = new PageData();
        pageData.setUrl(url);
        getPage(pageData, setting);
        return pageData.getHtml();
    }

    @Override
    public void getPage(PageData pageData, ConnectionProperties connectionProperties) {
        init();
        before(driver, pageData, connectionProperties);

        driver.navigate().to(pageData.getUrl());
        solveCaptcha(driver, connectionProperties, pageData.getUrl());

        after(driver, pageData, connectionProperties);

        Document document = Jsoup.parse(driver.getPageSource());
        document.setBaseUri(ScrapeUtil.getBaseSiteUrl(driver.getCurrentUrl()));
        pageData.setHtml(document);
    }

    @Override
    public void before(WebDriver driver, PageData pageData, ConnectionProperties connectionProperties) {
        if(connectionProperties.getCookie() != null && connectionProperties.getCookie().size() > 0) {
            //if(isNewCookie(driver.manage().getCookies(), connectionProperties.getCookie())) {
                driver.navigate().to(pageData.getUrl());
                for(Map.Entry<String, String> c : connectionProperties.getCookie().entrySet())
                    driver.manage().addCookie(new Cookie(c.getKey(), c.getValue()));
                delay(connectionProperties);
            //}
        }
    }

    private boolean isNewCookie(Set<Cookie> browserCookies, Map<String, String> propertiesCookie) {
        int findSameCookieCounter = 0;
        for(Cookie cookie : browserCookies) {
            String name = cookie.getName();
            String value = cookie.getValue();
            if(propertiesCookie.containsKey(name) && propertiesCookie.get(name).equals(value))
                findSameCookieCounter++;
        }
        return findSameCookieCounter != propertiesCookie.size();
    }

    @Override
    public void after(WebDriver driver, PageData pageData, ConnectionProperties connectionProperties) {
        try {
            WebDriverUtil.waitJQuery(driver, 5);
        }
        catch (Exception ignore) {}
        try {
            WebDriverUtil.waitPageStateComplete(driver, 10);
        }
        catch (JavascriptException e) {
            LogHelper.getLogger().log(Level.SEVERE, "exception while SeleniumConnection", e);
        }

        waitForElements(connectionProperties);

        if(connectionProperties.getEvents().size() > 0) {
            connectionProperties.getEvents().forEach(connectionEvent -> {
                connectionEvent.event(driver, pageData.getUrl());
                waitForElements(connectionProperties);
            });
        }

        delay(connectionProperties);
    }


    private void waitForElements(ConnectionProperties setting) {
        try{
            if(setting.getWaitForIt() != null && setting.getWaitForIt().size() > 0)
                WebDriverUtil.waitElement(driver, String.join(", ", setting.getWaitForIt()), 10);
        }
        catch (Exception e) {
            LogHelper.getLogger().log(Level.WARNING, "selenium wait exception", e);
        }
    }

    private boolean solveCaptcha(ChromeDriver driver, ConnectionProperties setting, String url) {
        if(setting.getCaptchaSolver() != null) {
            if(setting.getCaptchaSolver().solveCaptcha(driver, url));
                //driver.navigate().refresh();
        }
        return false;
    }

    @Override
    public void close() {
        if(driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
