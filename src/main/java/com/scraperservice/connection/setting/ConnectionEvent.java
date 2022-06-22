package com.scraperservice.connection.setting;

import org.openqa.selenium.WebDriver;

/**
 * Interface have to implement something event on the page. The event will start after the page is loaded
 */
public interface ConnectionEvent {
    void event(WebDriver webDriver, String url);
}
