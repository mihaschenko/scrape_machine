package com.scraperservice.captcha;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class CaptchaServer {
    protected static final String H_CAPTCHA_SELECTOR = "iframe[src*=\"hcaptcha.com\"]";
    protected static final String H_CAPTCHA_RESULT_SELECTOR = "[name=\"h-captcha-response\"]";
    private final static String RESULT_REGEX = "OK\\|.+";
    private final Map<CaptchaType, String> captchaTypes = new HashMap<>();

    public Map<CaptchaType, String> getCaptchaTypes() {
        return captchaTypes;
    }

    public void addCaptchaType(CaptchaType captchaType, String cssSelector) {
        captchaTypes.put(captchaType, cssSelector);
    }

    public void clearAllCaptchaTypes() {
        captchaTypes.clear();
    }

    public CaptchaResult solve(String url, Document document, Map<String, String> cookies) {
        CaptchaResult captchaResult = null;

        if(document.selectFirst(H_CAPTCHA_SELECTOR) != null)
            captchaResult = hCaptcha(document, url);
        else {
            String imageCaptchaCssSelector = captchaTypes.get(CaptchaType.CAPTCHA_IMAGE);
            if(imageCaptchaCssSelector != null && document.selectFirst(imageCaptchaCssSelector) != null)
                captchaResult = imageCaptcha(document, cookies, imageCaptchaCssSelector);
        }

        if(captchaResult == null)
            captchaResult = new CaptchaResult(null, CaptchaStatus.CAPTCHA_NOT_FOUND);

        return captchaResult;
    }

    public boolean isPageHaveCaptcha(Document document) {
        if(document.selectFirst(H_CAPTCHA_SELECTOR) != null)
            return true;
        else {
            for(Map.Entry<CaptchaType, String> captchaTypeStringEntry : captchaTypes.entrySet()) {
                if(document.selectFirst(captchaTypeStringEntry.getValue()) != null)
                    return true;
            }
        }
        return false;
    }

    protected String sendCaptcha(Connection connection) throws InterruptedException, IOException {
        String result = connection.post().text().trim();
        if(result.matches(RESULT_REGEX)) {
            result = result.replaceFirst("OK\\|", "");
            Thread.sleep(20000);
            return result;
        }
        else
            throw new IllegalStateException(result);
    }

    protected CaptchaResult getCaptchaResult(Connection connection) throws InterruptedException, IOException {
        for(int attempts = 0; attempts < 10; attempts++) {
            String captchaResult = connection.get().text().trim();
            if(captchaResult.matches(RESULT_REGEX))
                return new CaptchaResult(captchaResult.replaceFirst("OK\\|", ""),
                        CaptchaStatus.OK);
            else if(!captchaResult.equals("CAPCHA_NOT_READY"))
                return new CaptchaResult(null, CaptchaStatus.ERROR, captchaResult);
            Thread.sleep(5000);
        }
        return new CaptchaResult(null, CaptchaStatus.TIME_OUT);
    }

    abstract CaptchaResult imageCaptcha(Document document, Map<String, String> cookies, String captchaImageCssSelector);
    abstract CaptchaResult hCaptcha(Document document, String url);
}
