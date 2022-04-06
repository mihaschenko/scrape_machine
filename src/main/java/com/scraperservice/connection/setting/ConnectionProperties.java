package com.scraperservice.connection.setting;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use for custom connection. Some settings only work when connecting via jsoup/selenium
 */
@Data
public class ConnectionProperties {
    private Map<String, String> cookie;
    private Method method;
    private Map<String, String> data;
    private List<String> waitForIt;
    private List<ConnectionEvent> events;
    private int delay;

    public ConnectionProperties() {
        method = Method.GET;
        cookie = new HashMap<>();
        data = new HashMap<>();
        waitForIt = new ArrayList<>();
        events = new ArrayList<>();
        delay = 0;
    }

    public ConnectionProperties(ConnectionProperties connectionProperties) {
        if(connectionProperties == null)
            throw new NullPointerException("connectionProperties = null");
        method = connectionProperties.method;
        cookie = connectionProperties.cookie;
        data = connectionProperties.data;
        waitForIt = connectionProperties.waitForIt;
        events = connectionProperties.events;
        delay = connectionProperties.delay;
    }

    public enum Method {
        GET,
        POST
    }
}
