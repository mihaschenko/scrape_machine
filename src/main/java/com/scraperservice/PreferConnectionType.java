package com.scraperservice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface PreferConnectionType {
    ConnectionType value();
    boolean isUseProxy() default false;

    enum ConnectionType {
        SeleniumConnection,
        JsoupConnection
    }
}
