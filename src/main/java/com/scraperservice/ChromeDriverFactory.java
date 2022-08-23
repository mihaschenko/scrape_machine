package com.scraperservice;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileReader;
import java.io.IOException;
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
    }

    public ChromeDriver getChromeDriver() {
        ChromeDriver chromeDriver;

        System.setProperty("webdriver.chrome.driver", pathToDriver);
        ChromeOptions chromeOptions = new ChromeOptions();
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
        chromeOptions.setExperimentalOption("useAutomationExtension", false);
        chromeOptions.addArguments("disable-infobars");
        chromeOptions.addArguments("window-size=1536,754");
        chromeOptions.addArguments("--enable-javascript");
        chromeOptions.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");

        /*if(isUseProxy) {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("com.scraperservice.proxy");
            ProxyProperty proxyProperty = context.getBean(ProxyProperty.class);

            Proxy proxy = new Proxy();
            proxy.setProxyType(Proxy.ProxyType.MANUAL);
            System.out.println(proxyProperty.getHost() + ":" + proxyProperty.getPort());
            proxy.setHttpProxy(proxyProperty.getHost() + ":" + proxyProperty.getPort());
            chromeOptions.setProxy(proxy);

            ProxyAuthenticator proxyAuthenticator = context.getBean(ProxyAuthenticator.class);
            Authenticator.setDefault(proxyAuthenticator);
            chromeOptions.addArguments("--proxy-server=" + proxyProperty.getHost() + ":" + proxyProperty.getPort());
        }*/

        chromeDriver = new ChromeDriver(chromeOptions);

        chromeDriver.executeScript("window.onblur = function() { window.onfocus() }");
        chromeDriver.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        if(implicitlyWaitSeconds > 0)
            chromeDriver.manage().timeouts().implicitlyWait(implicitlyWaitSeconds, TimeUnit.SECONDS);

        return chromeDriver;
    }
}