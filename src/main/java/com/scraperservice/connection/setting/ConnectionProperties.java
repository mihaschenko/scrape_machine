package com.scraperservice.connection.setting;

import com.scraperservice.proxy.ProxyProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConnectionProperties {
    private static final ConnectionProperties DEFAULT_CONNECTION_PROPERTIES = new ConnectionProperties();

    public static ConnectionProperties getDEFAULT_CONNECTION_PROPERTIES() { return DEFAULT_CONNECTION_PROPERTIES; }

    private Map<String, String> cookie;
    private Method method;
    private Map<String, String> data;
    private boolean useDefaultPreparation;
    private boolean getJson;
    private List<String> waitForIt;
    private List<ConnectionEvent> events;
    private int delay = 0;
    private boolean useProxy;
    private ProxyProperty proxyProperty;

    public ConnectionProperties() {
        method = Method.GET;
        useDefaultPreparation = true;
        getJson = false;
        cookie = new HashMap<>();
        data = new HashMap<>();
        waitForIt = new ArrayList<>();
        events = new ArrayList<>();
        useProxy = false;
        proxyProperty = null;
    }

    public enum Method {
        GET,
        POST
    }
}
