package com.scraperservice.connection;

import com.scraperservice.connection.HeaderPool;
import com.scraperservice.connection.ProxyPool;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@PropertySource("classpath:scraperApplication.properties")
public class ChromeDriverFactory {
    private final String pathToDriver;
    private final boolean headless;
    private final int implicitlyWaitSeconds;
    private final boolean isUseProxy;
    private final boolean isUseHeader;
    private final ProxyPool proxies;
    private final HeaderPool headerPool;

    private ChromeDriverFactory(@Value("${webDriver.pathToDriver}") String pathToDriver,
                                @Value("${webDriver.headless}") boolean headless,
                                @Value("${scraper.useProxy}") boolean isUseProxy,
                                @Value("${scraper.useHeader}") boolean isUseHeader,
                                @Value("${webDriver.implicitlyWaitSeconds}") int implicitlyWaitSeconds,
                                ProxyPool proxies, HeaderPool headerPool) {
        this.pathToDriver = pathToDriver;
        this.headless = headless;
        this.isUseProxy = isUseProxy;
        this.isUseHeader = isUseHeader;
        this.implicitlyWaitSeconds = implicitlyWaitSeconds;
        this.proxies = proxies;
        this.headerPool = headerPool;
    }

    public ChromeDriver getChromeDriver() {
        ChromeDriver chromeDriver;

        BrowserMobProxy proxy = null;
        if(isUseHeader && headerPool.isHeadExist()) {
            proxy = new BrowserMobProxyServer();
            proxy.addHeaders(headerPool.getHead());
            proxy.start(0);
        }

        System.setProperty("webdriver.chrome.driver", pathToDriver);
        ChromeOptions chromeOptions = new ChromeOptions();
        if(proxy != null)
            chromeOptions.setProxy(ClientUtil.createSeleniumProxy(proxy));
        chromeOptions.setBinary("");
        if(headless)
            chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox'");
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--single-process");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        //chromeOptions.addArguments("--incognito");
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
        chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        chromeOptions.addArguments("disable-infobars");
        chromeOptions.addArguments("window-size=1536,754");
        chromeOptions.addArguments("--enable-javascript");
        if(isUseProxy && proxies.isProxyExist())
            chromeOptions.addArguments("--proxy-server=" + proxies.getProxy().toString());

        // disable images
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2);
        chromeOptions.setExperimentalOption("prefs", prefs);

        chromeDriver = new ChromeDriver(chromeOptions);

        chromeDriver.executeScript("window.onblur = function() { window.onfocus() }");
        chromeDriver.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        if(implicitlyWaitSeconds > 0)
            chromeDriver.manage().timeouts().implicitlyWait(implicitlyWaitSeconds, TimeUnit.SECONDS);

        return chromeDriver;
    }
}