package com.scraperservice.connection;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.scraper.helper.SSLHelper;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupConnection extends Connection {
    @Override
    public Document getPage(String url, ConnectionProperties setting) throws IOException {
        org.jsoup.Connection connection = SSLHelper.getConnection(url);

        connection = connection.userAgent(RandomUserAgent.getRandomUserAgent())
                .header("Accept-Language", "en-US")
                .header("Accept-Encoding", "gzip,deflate,sdch")
                .ignoreContentType(true)
                .ignoreHttpErrors(true);

        if(setting.getCookie() != null && setting.getCookie().size() > 0)
            connection = connection.cookies(setting.getCookie());
        if(setting.getData() != null && setting.getData().size() > 0)
            connection = connection.data(setting.getData());

        connection.timeout(0);

        Document result = setting.getMethod() == ConnectionProperties.Method.GET ? connection.get() : connection.post();
        if(setting.getDelay() > 0) {
            try{ Thread.sleep(setting.getDelay()); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        return result;
    }

    @Override
    public void close() {}
}
