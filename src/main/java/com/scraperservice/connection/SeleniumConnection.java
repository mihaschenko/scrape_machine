package com.scraperservice.connection;

import com.scraperservice.ChromeDriverFactory;
import com.scraperservice.captcha.CaptchaResult;
import com.scraperservice.captcha.CaptchaStatus;
import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.helper.LogHelper;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.utils.RegexUtil;
import com.scraperservice.utils.ScrapeUtil;
import com.scraperservice.utils.WebDriverUtil;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Data
public class SeleniumConnection extends Connection {
    private final ChromeDriverFactory driverFactory;
    private ChromeDriver driver;

    public SeleniumConnection() {
        this.driverFactory = ChromeDriverFactory.getInstance();
        init();
    }

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

        driver.manage().deleteAllCookies();
        if(connectionProperties.getCookie() != null && connectionProperties.getCookie().size() > 0) {
            driver.navigate().to(RegexUtil.findText("http(s|):\\/\\/[^\\/]+", pageData.getUrl()));
            for(Map.Entry<String, String> c : connectionProperties.getCookie().entrySet())
                driver.manage().addCookie(new Cookie(c.getKey(), c.getValue()));
            delay(connectionProperties);
        }

        driver.navigate().to(pageData.getUrl());

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

        Map<String, String> cookies = new HashMap<>();
        driver.manage().getCookies().forEach(cookie -> cookies.put(cookie.getName(), cookie.getValue()));
        pageData.setCookies(cookies);

        waitForElements(connectionProperties);
        if(solveCaptcha(driver, pageData.getUrl(), connectionProperties, cookies))
            waitForElements(connectionProperties);

        if(connectionProperties.getEvents().size() > 0)
            connectionProperties.getEvents().forEach(connectionEvent -> connectionEvent.event(driver, pageData.getUrl()));

        delay(connectionProperties);

        Document document = Jsoup.parse(driver.getPageSource());
        document.setBaseUri(ScrapeUtil.getBaseSiteUrl(driver.getCurrentUrl()));
        pageData.setHtml(document);
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

    private boolean solveCaptcha(ChromeDriver driver, String url, ConnectionProperties setting, Map<String, String> cookies) {
        if(setting.getCaptchaServer() != null) {
            CaptchaResult captchaResult = setting.getCaptchaServer().solve(
                    driver.getCurrentUrl(), ScrapeUtil.getDocument(driver), cookies);
            if(setting.getCaptchaSolver() != null)
                setting.getCaptchaSolver().solve(driver, url, captchaResult, cookies);
            return captchaResult.status == CaptchaStatus.OK;
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
