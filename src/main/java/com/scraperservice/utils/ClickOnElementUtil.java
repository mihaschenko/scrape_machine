package com.scraperservice.utils;

import com.scraperservice.helper.LogHelper;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.logging.Level;

public class ClickOnElementUtil {
    private final ChromeDriver driver;
    private final String cssSelector;
    private boolean isClickOnEverything;
    private int elemIndex;
    private boolean isUseJavascript;
    private long waitElementsSeconds;
    private boolean isCatchException;
    private boolean isLogException;
    private int attempts;

    private ClickOnElementUtil(ChromeDriver driver, String cssSelector) {
        if(driver == null)
            throw new NullPointerException("WebDriver driver = null");
        if(cssSelector == null || cssSelector.isEmpty())
            throw new NullPointerException("CSS is empty or null");

        this.driver = driver;
        this.cssSelector = cssSelector;
        this.isClickOnEverything = false;
        this.elemIndex = 0;
        this.isUseJavascript = true;
        this.waitElementsSeconds = 10;
        this.isCatchException = true;
        this.isLogException = false;
        this.attempts = 1;
    }

    public static ClickOnElementUtil prepare(ChromeDriver driver, String cssSelector) {
        return new ClickOnElementUtil(driver, cssSelector);
    }

    public ClickOnElementUtil setClickOnEverything(boolean clickOnEverything) {
        isClickOnEverything = clickOnEverything;
        return this;
    }

    public ClickOnElementUtil setElemIndex(int elemIndex) {
        this.elemIndex = elemIndex;
        return this;
    }

    public ClickOnElementUtil setUseJavascript(boolean useJavascript) {
        isUseJavascript = useJavascript;
        return this;
    }

    public ClickOnElementUtil setWaitElementsSeconds(long waitElementsSeconds) {
        this.waitElementsSeconds = waitElementsSeconds;
        return this;
    }

    public ClickOnElementUtil setCatchException(boolean catchException) {
        isCatchException = catchException;
        return this;
    }

    public ClickOnElementUtil setAttempts(int attempts) {
        this.attempts = attempts;
        return this;
    }

    public ClickOnElementUtil setLogException(boolean logException) {
        isLogException = logException;
        return this;
    }

    private void clickOnElement(int elemIndex) {
        waitForVisibilityOfElements();
        if(isUseJavascript)
            ((JavascriptExecutor) driver).executeScript(
                    "document.querySelectorAll('" + cssSelector + "')["+ elemIndex + "].click()");
        else {
            List<WebElement> elements = driver.findElementsByCssSelector(cssSelector);
            if(elemIndex < elements.size()) {
                WebElement element = elements.get(elemIndex);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
                Actions actions = new Actions(driver);
                actions.moveToElement(element).click(element).build().perform();
            }
            else
                throw new NoSuchElementException(
                        String.format("Element by such index does not exists (elemIndex=%d, elements.size=%d)",
                                elemIndex, elements.size()));
        }
        try{
            WebDriverUtil.waitPageComplete(driver, 1);
        }
        catch (Exception ignore) {}
    }

    private void waitForVisibilityOfElements() {
        try{
            new WebDriverWait(driver, waitElementsSeconds).until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(cssSelector)));
        }
        catch (Exception ignore) {}
    }

    public void click() {
        for(int i = 0; i < attempts; i++) {
            try {
                if(isClickOnEverything) {
                    while (checkIsElementExist())
                        clickOnElement(0);
                }
                else
                    clickOnElement(elemIndex);
                break;
            }
            catch (Exception e) {
                if(i >= attempts-1) {
                    if(isLogException)
                        LogHelper.getLogger().log(Level.WARNING, e.getMessage(), e);
                    if(!isCatchException)
                        throw new RuntimeException(e);
                }
            }
        }
    }

    private boolean checkIsElementExist() {
        return Jsoup.parse(driver.getPageSource()).selectFirst(cssSelector) != null;
    }
}
