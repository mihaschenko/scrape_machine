package com.scraperservice.captcha;

public interface CaptchaServer {
    String solve(String key);
}
