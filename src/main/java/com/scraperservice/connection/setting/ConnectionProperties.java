package com.scraperservice.connection.setting;

import com.scraperservice.captcha.CaptchaSolver;
import lombok.*;

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
    private RandomDelay randomDelay;
    private CaptchaSolver captchaSolver;

    public ConnectionProperties() {
        method = Method.GET;
        cookie = new HashMap<>();
        data = new HashMap<>();
        waitForIt = new ArrayList<>();
        events = new ArrayList<>();
        delay = 0;
        randomDelay = null;
        captchaSolver = null;
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
        randomDelay = connectionProperties.randomDelay;
        captchaSolver = connectionProperties.captchaSolver;
    }

    public static class RandomDelay {
        public final int from;
        public final int to;

        public RandomDelay(int from, int to) {
            if(to < from || from < 0)
                throw new IllegalArgumentException(String.format("from = %d, to = %d", from, to));
            this.from = from;
            this.to = to;
        }
    }

    public enum Method {
        GET,
        POST
    }
}
