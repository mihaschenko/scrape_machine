package com.scraperservice.connection;

public class Proxy {
    public final String host;
    public final int port;

    public Proxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
