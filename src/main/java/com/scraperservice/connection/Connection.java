package com.scraperservice.connection;

import com.scraperservice.connection.setting.ConnectionProperties;
import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class Connection implements AutoCloseable {
    public abstract Document getPage(String url, ConnectionProperties setting) throws IOException;

    public Document getPage(String url) throws IOException {
        return getPage(url, new ConnectionProperties());
    }
}
