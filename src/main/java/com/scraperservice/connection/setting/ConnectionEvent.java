package com.scraperservice.connection.setting;

import org.openqa.selenium.WebDriver;

public interface ConnectionEvent {
    void event(WebDriver webDriver);
}
