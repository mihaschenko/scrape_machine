package com.scraperservice.captcha;

import com.scraperservice.connection.SeleniumConnection;
import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.helper.LogHelper;
import com.scraperservice.utils.ClickOnElementUtil;
import com.scraperservice.utils.ScrapeUtil;
import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.ReCaptcha;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.logging.Level;

public class ReCaptchaV2Solver implements CaptchaSolver {
    private final TwoCaptcha twoCaptcha;
    private static final String RECAPTCHA_SELECTOR = "div[data-sitekey]";
    private static final String MAIN_FRAME_SELECTOR = "body > #main-iframe[src]";
    private static final String RECAPTCHA_BUTTON_SELECTOR = "div.recaptcha-checkbox-checkmark";

    public ReCaptchaV2Solver(String apiKey) {
        twoCaptcha = new TwoCaptcha(apiKey);
    }

    @Override
    public boolean solveCaptcha(ChromeDriver driver, String url) {
        System.out.println("enter method solveCaptcha()");
        Document document = Jsoup.parse(driver.getPageSource());
        if(document.selectFirst(MAIN_FRAME_SELECTOR) != null) {
            System.out.println("enter main frame");
            driver.switchTo().frame(driver.findElement(By.cssSelector(MAIN_FRAME_SELECTOR)));
            document = Jsoup.parse(driver.getPageSource());
        }
        if(document.selectFirst(RECAPTCHA_SELECTOR) != null) {
            System.out.println("start solving captcha");
            ReCaptcha captcha = new ReCaptcha();
            captcha.setSiteKey(ScrapeUtil.getAttribute(document, RECAPTCHA_SELECTOR, "data-sitekey"));
            captcha.setUrl(url);
            captcha.setInvisible(true);
            captcha.setAction("verify");

            try {
                String captchaId = twoCaptcha.send(captcha);

                Thread.sleep(20 * 1000);
                String code = null;
                for (int i = 0; i < 5; i++) {
                    code = twoCaptcha.getResult(captchaId);
                    if(code != null && !code.isBlank())
                        break;
                    Thread.sleep(5 * 1000);
                }
                System.out.println("captcha code = " + code);
                if(code != null) {
                    driver.executeScript("document.getElementById('g-recaptcha-response').innerHTML = '" +
                            code + "'");

                    Thread.sleep(500);
                    driver.switchTo().frame(driver.findElement(By.cssSelector("iframe[title=\"reCAPTCHA\"]")));
                    ClickOnElementUtil.prepare(driver, RECAPTCHA_BUTTON_SELECTOR).click();
                }
                System.out.println("finish solving captcha");
                return true;
            }
            catch (Exception e) {
                LogHelper.getLogger().log(Level.WARNING, e.getMessage(), e);
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        try(SeleniumConnection connection = new SeleniumConnection()) {
            ConnectionProperties connectionProperties = new ConnectionProperties();
            connectionProperties.setCaptchaSolver(new ReCaptchaV2Solver("af5d81704f126d06d0bcea55ec56b688"));
            connection.getPage("https://www.google.com/recaptcha/api2/demo", connectionProperties);
        }
    }
}
