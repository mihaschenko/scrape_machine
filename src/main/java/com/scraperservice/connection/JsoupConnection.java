package com.scraperservice.connection;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.helper.SSLHelper;
import com.scraperservice.scraper.page.PageData;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope("prototype")
@PropertySource("classpath:scraperApplication.properties")
public class JsoupConnection extends Connection<Document> {
    private final HeaderPool headerPool;
    private final ProxyPool proxyPool;
    private final boolean isUseProxy;
    private final boolean isUseHeader;

    public JsoupConnection() {
        this(null, false, null, false);
    }

    @Autowired
    public JsoupConnection(HeaderPool headerPool,
                           @Value("${scraper.useProxy}") boolean isUseProxy,
                           ProxyPool proxyPool,
                           @Value("${scraper.useHeader}") boolean isUseHeader) {
        this.headerPool = headerPool;
        this.proxyPool = proxyPool;
        this.isUseProxy = isUseProxy;
        this.isUseHeader = isUseHeader;
    }

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
        //org.jsoup.Connection.Response response = connection.execute();

        //connection = createConnection(pageData, connectionProperties)
        //        .cookies(response.cookies());
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

        connection.userAgent(RandomUserAgent.getRandomUserAgent())
                .header("Accept-Language", "en-US")
                .header("Accept-Encoding", "gzip,deflate,sdch")
                .ignoreContentType(true)
                .ignoreHttpErrors(true);

        if(isUseHeader && headerPool.isHeadExist())
            connection.headers(headerPool.getHead());
        if(isUseProxy && proxyPool.isProxyExist()) {
            Proxy proxy = proxyPool.getProxy();
            connection.proxy(proxy.host, proxy.port);
        }

        if(connectionProperties.getCookie() != null && connectionProperties.getCookie().size() > 0)
            connection.cookies(connectionProperties.getCookie());
        if(connectionProperties.getData() != null && connectionProperties.getData().size() > 0)
            connection.data(connectionProperties.getData());

        connection.timeout(0);

        return connection;
    }

    @Override
    public void close() {}
}
