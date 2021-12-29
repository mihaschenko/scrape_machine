package com.scraperservice.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionSetting {
    private static final ConnectionSetting defaultConnectionSetting = new ConnectionSetting();

    public static ConnectionSetting getDefaultConnectionSetting() { return defaultConnectionSetting; }

    private Map<String, String> cookie;
    private Method method;
    private Map<String, String> data;
    private boolean useDefaultPreparation;
    private boolean getJson;
    private List<String> waitForIt;

    public ConnectionSetting() {
        method = Method.GET;
        useDefaultPreparation = true;
        getJson = false;
        cookie = new HashMap<>();
        data = new HashMap<>();
        waitForIt = new ArrayList<>();
    }

    public Map<String, String> getCookie() {return cookie;}
    public void setCookie(Map<String, String> cookie) {this.cookie = cookie;}
    public Method getMethod() {return method;}
    public void setMethod(Method method) {this.method = method;}
    public Map<String, String> getData() {return data;}
    public void setData(Map<String, String> data) {this.data = data;}
    public boolean isUseDefaultPreparation() {return useDefaultPreparation;}
    public void setUseDefaultPreparation(boolean useDefaultPreparation) {this.useDefaultPreparation = useDefaultPreparation;}
    public boolean isGetJson() {return getJson;}
    public void setGetJson(boolean getJson) {this.getJson = getJson;}
    public List<String> getWaitForIt() {return waitForIt;}
    public void setWaitForIt(String waitForIt) {
        this.waitForIt.clear();
        this.waitForIt.add(waitForIt);
    }
    public void setWaitForItList(List<String> waitForIt) {this.waitForIt = waitForIt;}

    public enum Method {
        GET,
        POST
    }
}
