package com.scraperservice.captcha;

public class CaptchaResult {
    public final String key;
    public final CaptchaStatus status;
    public final String statusMessage;

    public CaptchaResult(String key) {
        this(key, CaptchaStatus.OK, null);
    }

    public CaptchaResult(String key, CaptchaStatus status) {
        this(key, status, null);
    }

    public CaptchaResult(String key, CaptchaStatus status, String statusMessage) {
        this.key = key;
        this.status = status;
        this.statusMessage = statusMessage;
    }

    @Override
    public String toString() {
        return "CaptchaResult{" +
                "key='" + key + '\'' +
                ", status=" + status +
                ", statusMessage='" + statusMessage + '\'' +
                '}';
    }
}
