package com.scraperservice.utils;

import java.util.Date;

public class TimestampUtil {
    public static long getTimestampInMilliseconds() {
        return new Date().getTime();
    }

    public static long getTimestampInSeconds() {
        return getTimestampInMilliseconds() / 1000;
    }

    private TimestampUtil() {}
}
