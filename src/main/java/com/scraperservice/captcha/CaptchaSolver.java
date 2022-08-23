package com.scraperservice.captcha;

import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Map;

public interface CaptchaSolver {
    Document solve(ChromeDriver driver, String url, CaptchaResult captchaResult, Map<String, String> cookies);
}