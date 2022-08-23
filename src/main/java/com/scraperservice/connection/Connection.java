package com.scraperservice.connection;

import com.scraperservice.captcha.CaptchaResult;
import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.scraper.page.PageData;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Connection implements AutoCloseable {
    @Deprecated
    public abstract Document getPage(String url, ConnectionProperties setting) throws IOException;
    public abstract void getPage(PageData pageData, ConnectionProperties connectionProperties) throws IOException;

    public Document getPage(String url) throws IOException {
        return getPage(url, new ConnectionProperties());
    }

    protected void delay(ConnectionProperties connectionProperties) {
        try{
            if(connectionProperties.getRandomDelay() != null)
                Thread.sleep(ThreadLocalRandom.current().nextLong(connectionProperties.getRandomDelay().from,
                        connectionProperties.getRandomDelay().to));
            else if(connectionProperties.getDelay() > 0)
                Thread.sleep(connectionProperties.getDelay());
        }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
