package com.scraperservice.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class WebDriverUtil {
    /**
     * Ожидает появления элемента на странице.
     * @param driver Selenium WebDriver. Содержит HTML
     * @param cssSelector css селектор
     * @param seconds максимальное время ожидания в секундах
     */
    public static void waitElement(WebDriver driver, String cssSelector, int seconds) {
        waitElements(driver, cssSelector, seconds, 1);
    }

    /**
     * Ожидает появления элемента/элементов на странице.
     * @param driver Selenium WebDriver. Содержит HTML
     * @param cssSelector css селектор
     * @param seconds максимальное время ожидания в секундах
     * @param amount количество ожидаемых элементов
     * @throws IllegalArgumentException int amount < 0
     */
    public static boolean waitElements(WebDriver driver, String cssSelector, int seconds, int amount) {
        if(amount < 0)
            throw new IllegalArgumentException("int amount < 0");
        try{
            new WebDriverWait(driver, seconds).until(
                    webDriver -> {
                        List<WebElement> elements = webDriver.findElements(By.cssSelector(cssSelector));
                        return elements.size() >= amount;
                    });
        }
        catch (Exception ignore) { return false; }
        return true;
    }

    /**
     * Происходит нажатие на элемент с помощью экземпляра класса
     * Actions библиотеки Selenium
     * @param driver Selenium WebDriver
     * @param css селектор
     * @return количество найденных и нажатых элементов
     * @throws NullPointerException один из аргументов равен null
     */
    public static int clickOnElement(WebDriver driver, String css) {
        return clickOnSeveralElements(driver, css, null, true, false);
    }

    /**
     * Происходит нажатие на элемент с помощью экземпляры класса
     * Actions библиотеки Selenium
     * @param driver Selenium WebDriver
     * @param css селектор
     * @return количество найденных и нажатых элементов
     * @throws NullPointerException один из аргументов равен null
     */
    public static int clickOnElements(WebDriver driver, String css) {
        return clickOnSeveralElements(driver, css, null, false, false);
    }

    /**
     * Происходит нажатие на элемент с помощью экземпляры класса
     * Actions библиотеки Selenium
     * @param driver Selenium WebDriver
     * @param element Selenium WebElement
     * @return количество найденных и нажатых элементов
     * @throws NullPointerException один из аргументов равен null
     */
    public static int clickOnElement(WebDriver driver, WebElement element) {
        return clickOnSeveralElements(driver, null, Collections.singletonList(element), true, false);
    }

    /**
     * Происходит нажатие на элементы с помощью экземпляры класса
     * Actions библиотеки Selenium
     * @param driver Selenium WebDriver
     * @param elements коллекция Selenium WebElement
     * @return количество найденных и нажатых элементов
     * @throws NullPointerException один из аргументов равен null
     */
    public static int clickOnElements(WebDriver driver, List<WebElement> elements) {
        return clickOnSeveralElements(driver, null, elements, false, false);
    }

    /**
     * Происходит нажатие на элемент с помощью javascript
     * @param driver Selenium WebDriver
     * @param css селектор
     * @return количество найденных и нажатых элементов
     * @throws NullPointerException один из аргументов равен null
     */
    public static int clickOnElementJavaScript(WebDriver driver, String css) {
        return clickOnSeveralElements(driver, css, null, true, true);
    }

    /**
     * Происходит нажатие на элементы с помощью javascript
     * @param driver Selenium WebDriver
     * @param css селектор
     * @return количество найденных и нажатых элементов
     * @throws NullPointerException один из аргументов равен null
     */
    public static int clickOnElementsJavaScript(WebDriver driver, String css) {
        return clickOnSeveralElements(driver, css, null, false, true);
    }

    /**
     * Происходит нажатие на элемент с помощью javascript
     * @param driver Selenium WebDriver
     * @param element Selenium WebElement
     * @return количество найденных и нажатых элементов
     * @throws NullPointerException один из аргументов равен null
     */
    public static int clickOnElementJavaScript(WebDriver driver, WebElement element) {
        return clickOnSeveralElements(driver, null, Collections.singletonList(element), true, true);
    }

    /**
     * Происходит нажатие на элементы с помощью javascript
     * @param driver Selenium WebDriver
     * @param elements коллекция Selenium WebElement
     * @return количество найденных и нажатых элементов
     * @throws NullPointerException один из аргументов равен null
     */
    public static int clickOnElementsJavaScript(WebDriver driver, List<WebElement> elements) {
        return clickOnSeveralElements(driver, null, elements, false, true);
    }

    /**
     * Происходит нажатие на элемент/элементы с помощью JavaScript или класса Actions
     * библиотеки Selenium
     * @param driver Selenium WebDriver
     * @param css css селектор целевых/целевого элемента для нажатия. Может быть null, если
     *            переменная elementsList не равна null. Если оба значения
     *            не равны null, нажатия будут произведены на список экземпляров WebElement
     *            переменной elementsList
     * @param elementsList список экземпляров WebElement на которые будут
     *                     произведены нажатия. Может быть null, если
     *                     переменная css не равна null. Если оба значения
     *                     не равны null, нажатия будут произведены на список экземпляров WebElement
     *                     переменной elementsList
     * @param oneElement true - если требуется нажать только на один элемент. Эсли элементов будет несколько,
     *                   нажатие будет произведено только на первый элемент
     * @param useJavascript true - нажатие вызывается методами JavaScript. В ином случае,
     *                      будут использоваться методы класса Actions библиотеки
     *                      Selenium
     * @return количество нажатых элементов
     */
    private static int clickOnSeveralElements(WebDriver driver, String css, List<WebElement> elementsList,
                                              boolean oneElement, boolean useJavascript) {
        if(driver == null)
            throw new NullPointerException("WebDriver driver = null");
        if(css == null && elementsList == null)
            throw new NullPointerException("String css/List<WebElement> elements/WebElement element = null");
        int result = 0;

        List<WebElement> elements;
        if(elementsList != null)
            elements = elementsList;
        else
            elements = driver.findElements(By.cssSelector(css));

        if (elements != null && elements.size() > 0){
            Actions actions = null;
            if(!useJavascript)
                actions = new Actions(driver);
            if(oneElement) {
                WebElement element = elements.get(0);
                if(useJavascript) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView()", element);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                }
                else
                    actions.moveToElement(element).click(element).build().perform();
                result++;
            }
            else {
                for(WebElement element : elements) {
                    if(useJavascript) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView()", element);
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                    }
                    else
                        actions.moveToElement(element).click(element).build().perform();
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * Прокрутка страницы до заданного элемента
     * @param driver Selenium WebDriver
     * @param css селектор
     */
    public static void moveToElement(WebDriver driver, String css) {
        List<WebElement> elements = driver.findElements(By.cssSelector(css));
        if(elements != null && elements.size() > 0)
            moveToElement(driver, elements.get(0));
    }

    /**
     * Прокрутка страницы до заданного элемента
     * @param driver Selenium WebDriver
     * @param element Selenium WebElement
     */
    public static void moveToElement(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView()", element);
    }

    /**
     * Ожидает окончание работы JQuery 30 секунд
     * @param driver Selenium WebDriver
     */
    public static void waitJQuery(WebDriver driver) {
        waitJQuery(driver, 30);
    }

    /**
     * Ожидает окончание работы JQuery указанное количество секунд
     * @param driver Selenium WebDriver
     * @param seconds время ожидания в секундах
     */
    public static void waitJQuery(WebDriver driver, int seconds) {
        new WebDriverWait(driver, seconds).until(
                webDriver -> (Boolean) (((JavascriptExecutor) webDriver).executeScript("return jQuery.active == 0")));
    }

    public static void wait(WebDriver driver, int seconds, Function<? super WebDriver, Boolean> function) {
        new WebDriverWait(driver, seconds).until(function);
    }

    /**
     * Прокутка страницы до окончания её пагинации. Полезно, если товары на странице подгружаются
     * при прокрутке к концу списка
     * @param driver Selenium WebDriver
     * @param cssSelector css селектор продукта. Прокрутка будет идти до последнего
     *                    найденного продукта на странице
     * @return Jsoup Document после завершения прокрутки
     * @throws InterruptedException может быть выкинуто во время ожидания загрузки новых товаров
     */
    public static Document moveDownUntilEndOfPage(WebDriver driver, String cssSelector) throws InterruptedException {
        int productAmountLast = -1;
        int productAmountCurrent;
        int attempts = 0;
        while(attempts <= 3) {
            List<WebElement> products = driver.findElements(By.cssSelector(cssSelector));
            if(products != null && products.size() > 0) {
                productAmountCurrent = products.size();
                if(productAmountCurrent == productAmountLast)
                    attempts++;
                else
                    attempts--;

                productAmountLast = products.size();
                moveToElement(driver, products.get(products.size()-1));
            }
            else {
                attempts++;
                moveDownToEndOfPage(driver);
            }
            Thread.sleep(1500);
        }
        return Jsoup.parse(driver.getPageSource());
    }

    /**
     * Прокутка страницы до окончания её пагинации. Аналог метода moveDownUntilEndOfPage(), но
     * если объекты подгружаются при нажатии на кнопку. Вместо селектора на продукт требуется
     * селектор на кнопку для загрузки новых продуктов
     * @param driver Selenium WebDriver
     * @param cssSelector css селектор кнопки, которая загружает новые продукты на страницу
     * @param clickWithJavascript true - нажатие будет произведено с помощью Javascript. В ином случает,
     *                            будет использован инструмент Selenium
     * @return Jsoup Document после завершения прокрутки
     * @throws InterruptedException может быть выкинуто во время ожидания загрузки новых товаров
     */
    public static Document moveDownUntilEndOfPageClick(WebDriver driver, String cssSelector, boolean clickWithJavascript) throws InterruptedException {
        int attempts = 0;
        while(attempts <= 3) {
            List<WebElement> products = driver.findElements(By.cssSelector(cssSelector));
            if(products != null && products.size() > 0) {
                if (clickWithJavascript)
                    clickOnElementJavaScript(driver, cssSelector);
                else
                    clickOnElement(driver, cssSelector);
            }
            else {
                attempts++;
                moveDownToEndOfPage(driver);
            }
            Thread.sleep(1500);
        }
        return Jsoup.parse(driver.getPageSource());
    }

    private static void moveDownToEndOfPage(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Делает и сохраняет скриншот просматриваемого участка страницы
     * @param driver Selenium WebDriver
     * @param fileName полное имя файла, в который будет созранено изображение (Пример: "image.jpg")
     * @param formatName формат файла (Пример: "jpg")
     */
    public static void screenshot(WebDriver driver, String fileName, String formatName) throws IOException {
        File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        ImageIO.write(ImageIO.read(file), formatName, new File(fileName));
    }

    public static void waitPageStateComplete(WebDriver driver, int seconds) {
        new WebDriverWait(driver, seconds).until(
                werDriver -> ((JavascriptExecutor) werDriver).executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Закрывает в браузере все окна, кроме первого
     * @param driver Selenium WebDriver
     */
    public static void closeExcessWindow(WebDriver driver) {
        if(driver.getWindowHandles().size() > 1) {
            List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
            windowHandles.remove(0);
            for(String windowName : windowHandles)
                driver.switchTo().window(windowName).close();
        }
    }

    public static void clickOnElementUsingMatrix(WebDriver driver, int[][] matrix, WebElement[][] variants) {
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix[i].length; j++) {
                if(matrix[i][j] == 1)
                    clickOnElementJavaScript(driver, variants[i][j]);
            }
        }
    }

    private WebDriverUtil() {}
}
