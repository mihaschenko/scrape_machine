package com.scraperservice.proxy;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Random;

@Data
@Component
@PropertySource("classpath:proxy.properties")
public class ProxyProperty {
    private String host;
    private int minPortRange;
    private int maxPortRange;

    public ProxyProperty(String host, int port) {
        this(host, port, port);
    }

    @Autowired
    public ProxyProperty(@Value("${proxy.host}") String host,
                         @Value("${proxy.port.min}") int minPortRange,
                         @Value("${proxy.port.max}") int maxPortRange) {
        this.host = host;
        this.minPortRange = minPortRange;
        this.maxPortRange = maxPortRange;
    }

    public void setPort(int port) {
        this.minPortRange = port;
        this.maxPortRange = port;
    }

    public int getPort() {
        if(minPortRange != maxPortRange && minPortRange < maxPortRange)
            return new Random().nextInt(minPortRange, maxPortRange+1);
        else
            return minPortRange;
    }

    public static void setAllProxyProperty(String host, String port) {
        setProxyProperty(host, port);
        setPropertyAuthDisabledSchemesToEmpty();
    }

    public static void setPort(String port) {
        System.setProperty("http.proxyPort", port);
        System.setProperty("https.proxyPort", port);
    }

    public static void setHttpProxyProperty(String host, String port) {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port);
    }

    public static void setHttpsProxyProperty(String host, String port) {
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port);
    }

    public static void setProxyProperty(String host, String port) {
        setHttpProxyProperty(host, port);
        setHttpsProxyProperty(host, port);
    }

    public static void setPropertyAuthDisabledSchemesToEmpty() {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
    }
}
