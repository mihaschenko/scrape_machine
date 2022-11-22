package com.scraperservice.captcha;

import org.openqa.selenium.chrome.ChromeDriver;

public interface CaptchaSolver {
    boolean solveCaptcha(ChromeDriver driver, String url);
}
