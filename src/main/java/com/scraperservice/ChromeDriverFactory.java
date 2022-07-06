package com.scraperservice;

import com.scraperservice.proxy.ProxyAuthenticator;
import com.scraperservice.proxy.ProxyProperty;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileReader;
import java.io.IOException;
import java.net.Authenticator;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class ChromeDriverFactory {
    private static final ChromeDriverFactory chromeDriverFactory;

    static {
        try {
            chromeDriverFactory = new ChromeDriverFactory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final String pathToDriver;
    //private final String pathToBrowser;
    private final boolean headless;
    private final int implicitlyWaitSeconds;
    private final boolean isUseProxy;

    public static ChromeDriverFactory getInstance() {
        return chromeDriverFactory;
    }

    private ChromeDriverFactory() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/webDriverConfig.properties"));
        pathToDriver = properties.getProperty("webDriver.pathToDriver");
        //pathToBrowser = properties.getProperty("webDriver.pathToBrowser");
        headless = properties.getProperty("webDriver.headless").equals("true");
        implicitlyWaitSeconds = Integer.parseInt(properties.getProperty("webDriver.implicitlyWaitSeconds"));
        isUseProxy = properties.getProperty("webDriver.proxy").equals("true");
    }

    public ChromeDriver getChromeDriver() {
        ChromeDriver chromeDriver;

        System.setProperty("webdriver.chrome.driver", pathToDriver);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary("");
        if(headless)
            chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--enable-javascript");
        chromeOptions.addArguments("lang=en");
        chromeOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("disable-infobars"); // disabling infobars
        chromeOptions.addArguments("--disable-extensions"); // disabling extensions
        chromeOptions.addArguments("--disable-gpu"); // applicable to windows os only
        chromeOptions.addArguments("--no-sandbox"); // Bypass OS security model
        chromeOptions.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        chromeOptions.addArguments("--incognito");
        chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        chromeOptions.setExperimentalOption("useAutomationExtension", false);
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
        chromeOptions.addArguments("--disable-infobars");

        if(isUseProxy) {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.scraperservice.proxy");
            ProxyProperty proxyProperty = context.getBean(ProxyProperty.class);

            /*Proxy proxy = new Proxy();
            proxy.setProxyType(Proxy.ProxyType.MANUAL);
            System.out.println(proxyProperty.getHost() + ":" + proxyProperty.getPort());
            proxy.setHttpProxy(proxyProperty.getHost() + ":" + proxyProperty.getPort());
            chromeOptions.setProxy(proxy);*/

            ProxyAuthenticator proxyAuthenticator = context.getBean(ProxyAuthenticator.class);
            Authenticator.setDefault(proxyAuthenticator);
            chromeOptions.addArguments("--proxy-server=" + proxyProperty.getHost() + ":" + proxyProperty.getPort());
        }

        chromeDriver = new ChromeDriver(chromeOptions);

        chromeDriver.executeScript("window.onblur = function() { window.onfocus() }");
        chromeDriver.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        if(implicitlyWaitSeconds > 0)
            chromeDriver.manage().timeouts().implicitlyWait(implicitlyWaitSeconds, TimeUnit.SECONDS);

        return chromeDriver;
    }
}