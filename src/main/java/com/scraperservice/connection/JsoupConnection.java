package com.scraperservice.connection;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.helper.SSLHelper;
import com.scraperservice.scraper.page.PageData;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupConnection extends Connection<Document> {
    @Override
    public Document getPage(String url, ConnectionProperties setting) throws IOException {
        PageData pageData = new PageData();
        pageData.setUrl(url);
        getPage(pageData, setting);
        return pageData.getHtml();
    }

    @Override
    public void getPage(PageData pageData, ConnectionProperties connectionProperties) throws IOException {
        org.jsoup.Connection connection = createConnection(pageData, connectionProperties);
        org.jsoup.Connection.Response response = connection.execute();

        connection = createConnection(pageData, connectionProperties)
                .cookies(response.cookies());
        Document result = connectionProperties.getMethod() == ConnectionProperties.Method.GET ?
                connection.get() : connection.post();

        delay(connectionProperties);
        pageData.setHtml(result);
    }

    @Override
    public void before(Document document, PageData pageData, ConnectionProperties connectionProperties) {}

    @Override
    public void after(Document document, PageData pageData, ConnectionProperties connectionProperties) {}

    private org.jsoup.Connection createConnection(PageData pageData, ConnectionProperties connectionProperties) {
        org.jsoup.Connection connection = SSLHelper.getConnection(pageData.getUrl());

        connection = connection.userAgent(RandomUserAgent.getRandomUserAgent())
                .header("Accept-Language", "en-US")
                .header("Accept-Encoding", "gzip,deflate,sdch")
                .ignoreContentType(true)
                .ignoreHttpErrors(true);

        if(connectionProperties.getCookie() != null && connectionProperties.getCookie().size() > 0)
            connection = connection.cookies(connectionProperties.getCookie());
        if(connectionProperties.getData() != null && connectionProperties.getData().size() > 0)
            connection = connection.data(connectionProperties.getData());

        connection.timeout(0);

        return connectionProperties.getMethod() == ConnectionProperties.Method.GET ?
                connection.method(org.jsoup.Connection.Method.GET) : connection.method(org.jsoup.Connection.Method.POST);
    }

    @Override
    public void close() {}
}
