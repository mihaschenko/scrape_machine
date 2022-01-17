package com.scraperservice.connection;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.scraper.helper.SSLHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupConnection extends Connection {
    @Override
    public Document getPage(String url, ConnectionProperties setting) throws IOException {
        if(setting == null)
            throw new NullPointerException("ConnectionSetting setting = null");
        org.jsoup.Connection connection = SSLHelper.getConnection(url);

        if(setting.isUseDefaultPreparation()) {
            connection = connection.userAgent(RandomUserAgent.getRandomUserAgent())
                    .header("Accept-Language", "en-US")
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true);
        }
        if(setting.getCookie() != null && setting.getCookie().size() > 0)
            connection = connection.cookies(setting.getCookie());
        if(setting.getData() != null && setting.getData().size() > 0)
            connection = connection.data(setting.getData());

        if(setting.isGetJson()) {
            connection.method(org.jsoup.Connection.Method.POST);
            return Jsoup.parse(connection.execute().body());
        }

        if(setting.isUseProxy() && setting.getProxyProperty() != null)
            connection.proxy(setting.getProxyProperty().getHost(), setting.getProxyProperty().getPort());

        connection.timeout(0);

        return setting.getMethod() == ConnectionProperties.Method.GET ? connection.get() : connection.post();
    }

    @Override
    public void close() {}
}
