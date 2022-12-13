package com.scraperservice;

import com.scraperservice.connection.JsoupConnection;
import com.scraperservice.connection.SeleniumConnection;
import com.scraperservice.utils.ScrapeUtil;
import com.scraperservice.utils.WebDriverUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileReader;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

public class ProxyTest {
    private String currentIp;
    private final String IP_ADDRESS_CHECK_SITE_URL = "https://www.ipchicken.com";
    private final String IP_SELECTOR = "table p[align=\"center\"] > font > b"; // own test
    private boolean isUseProxy;
    private boolean isUseHeader;
    private AnnotationConfigApplicationContext applicationContext;

    @Before
    public void initDefaultIp() throws IOException {
        JsoupConnection connection = new JsoupConnection();
        Document document = connection.getPage(IP_ADDRESS_CHECK_SITE_URL);
        currentIp = ScrapeUtil.getOwnText(document, IP_SELECTOR).trim();
        System.out.println("CURRENT IP: " + currentIp);

        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/scraperApplication.properties"));
        isUseProxy = properties.getProperty("scraper.useProxy").equals("true");
        isUseHeader = properties.getProperty("scraper.useHeader").equals("true");
        System.out.println("IS USE PROXY: " + isUseProxy);
        System.out.println("IS USE HEADER: " + isUseHeader);

        applicationContext = new AnnotationConfigApplicationContext(SpringProxyTestConfig.class);
        setProxyAuthenticator();
    }

    private void setProxyAuthenticator() {
        Authenticator authenticator = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return (new PasswordAuthentication("U0000091426","qazRFVujm1472356890_+".toCharArray()));
            }
        };

        Authenticator.setDefault(authenticator);
    }

    @Test
    public void jsoupProxyTest() throws IOException {
        if(isUseProxy) {
            JsoupConnection connection = applicationContext.getBean(JsoupConnection.class);
            Document document = connection.getPage(IP_ADDRESS_CHECK_SITE_URL);
            compareIp(document);
        }
    }

    @Test
    public void seleniumProxyTest() throws IOException {
        if(isUseProxy) {
            SeleniumConnection connection = applicationContext.getBean(SeleniumConnection.class);
            connection.getPage(IP_ADDRESS_CHECK_SITE_URL);
            WebDriverUtil.waitElement(connection.getDriver(), IP_SELECTOR, 2);
            Document document = Jsoup.parse(connection.getDriver().getPageSource());
            connection.close();
            compareIp(document);
        }
    }

    private void compareIp(Document document) {
        String proxyIp = ScrapeUtil.getOwnText(document, IP_SELECTOR).trim();
        System.out.println("PROXY IP: " + proxyIp);
        Assert.assertNotEquals("", proxyIp);
        Assert.assertNotEquals(currentIp, proxyIp);
    }

    @After
    public void close() {
        applicationContext.close();
    }
}
