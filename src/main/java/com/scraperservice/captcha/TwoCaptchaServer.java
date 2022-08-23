package com.scraperservice.captcha;

import com.scraperservice.utils.RegexUtil;
import com.scraperservice.utils.ScrapeUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.*;

public class TwoCaptchaServer extends CaptchaServer {
    private final String apiKey;

    public TwoCaptchaServer() {
        try{
            Properties properties = new Properties();
            properties.load(new FileReader("src/main/resources/captcha.properties"));
            apiKey = properties.getProperty("captcha.key");
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    @Override
    public CaptchaResult imageCaptcha(Document document, Map<String, String> cookies, String captchaImageCssSelector) {
        try{
            Connection connection = Jsoup.connect("http://2captcha.com/in.php");
            Map<String, String> data = new HashMap<>();
            data.put("key", apiKey);
            data.put("method", "base64");
            data.put("body", decodeImageIntoBase64(document, cookies));
            connection.data(data);
            String result = sendCaptcha(connection);

            connection = Jsoup.connect("http://2captcha.com/res.php");
            data = new HashMap<>();
            data.put("key", apiKey);
            data.put("action", "get");
            data.put("id", result);
            connection.data(data);
            return getCaptchaResult(connection);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    private String decodeImageIntoBase64(Document document, Map<String, String> cookies) throws IOException {
        String imageLocation = ScrapeUtil.getAttribute(document, getCaptchaTypes().get(CaptchaType.CAPTCHA_IMAGE), "abs:src");
        Connection.Response resultImageResponse = Jsoup.connect(imageLocation).cookies(cookies).ignoreContentType(true).execute();
        return new String(Base64.getEncoder().encode(resultImageResponse.bodyAsBytes()));
    }

    @Override
    public CaptchaResult hCaptcha(Document document, String url) {
        try{
            String key = RegexUtil.findText("(?<=sitekey=)[^&]+",
                    ScrapeUtil.getAttribute(document, H_CAPTCHA_SELECTOR, "src"));
            Connection connection = Jsoup.connect("http://2captcha.com/in.php");
            Map<String, String> data = new HashMap<>();
            data.put("key", apiKey);
            data.put("method", "hcaptcha");
            data.put("sitekey", key);
            data.put("pageurl", url);
            connection.data(data);
            String result = sendCaptcha(connection);

            connection = Jsoup.connect("http://2captcha.com/res.php");
            data = new HashMap<>();
            data.put("key", apiKey);
            data.put("action", "get");
            data.put("id", result);
            connection.data(data);
            return getCaptchaResult(connection);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
