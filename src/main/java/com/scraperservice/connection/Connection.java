package com.scraperservice.connection;

import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class Connection implements AutoCloseable {
    public abstract Document getPage(String url, ConnectionSetting setting) throws IOException;

    public Document getPage(String url) throws IOException {
        return getPage(url, new ConnectionSetting());
    }
}
