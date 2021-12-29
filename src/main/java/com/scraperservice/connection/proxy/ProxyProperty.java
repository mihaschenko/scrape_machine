package com.scraperservice.connection.proxy;

public class ProxyProperty {
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
