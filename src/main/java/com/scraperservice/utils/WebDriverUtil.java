package com.scraperservice.utils;

import com.scraperservice.connection.SeleniumConnection;
import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.helper.LogHelper;
import com.scraperservice.scraper.page.PageData;
import com.scraperservice.storage.DataArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;

public class WebDriverUtil {
    public static Set<DataArray> walkAllVariantsAndReturnData(DocumentParser parser,
                                                              SeleniumConnection connection,
                                                              ConnectionProperties connectionProperties,
                                                              String selectCssSelector,
                                                              String optionCssSelector,
                                                              boolean isUseJavascript) {
        Set<DataArray> result = new HashSet<>();
        WebDriver driver = connection.getDriver();
        List<WebElement> selects = driver.findElements(By.cssSelector(selectCssSelector));
        int[] variants = new int[selects.size()];
        int[] lastVariants = new int[selects.size()];
        Arrays.fill(variants, 0);
        Arrays.fill(lastVariants, -1);
        if(variants.length == 0)
            return result;
        boolean isLastVariant = false;
        PageData pageData = new PageData(driver.getCurrentUrl());

        do {
            //выбираются варианты
            for(int i = 0; i < selects.size(); i++) {
                if(lastVariants[i] == variants[i])
                    continue;
                //выбирается группа
                clickOnDynamicElement(driver, selectCssSelector, i);

                List<WebElement> options = driver.findElements(By.cssSelector(optionCssSelector));
                // счётчик сбрасывается, если он больше количества кнопок
                if(variants[i] >= options.size()) {
                    variants[i] = 0;
                    if(i+1 < variants.length)
                        variants[i+1] = variants[i+1]+1;
                    else {
                        isLastVariant = true;
                        break;
                    }
                }
                lastVariants[i] = variants[i];

                //выбирается значение
                clickOnDynamicElement(driver, optionCssSelector, variants[i]);
            }

            // проходят процедуры ожидания элементов и вызов всех необходимых событий
            connection.after(driver, pageData, connectionProperties);

            // парсинг полученной страницы
            result.addAll(parser.parseData(driver.getCurrentUrl(), Jsoup.parse(driver.getPageSource())));

            //итерируется индекс кнопок на первой странице
            variants[0] = variants[0]+1;
        }
        while(!isLastVariant);

        return result;
    }

    @Deprecated
    public static void clickOnDynamicElement(WebDriver driver, String css, int elemIndex) {
        if(driver == null)
            throw new NullPointerException("WebDriver driver = null");
        if(css == null || css.isEmpty())
            throw new NullPointerException("CSS is empty");
        if(elemIndex < 0)
            throw new IllegalArgumentException("elem index can not be less than 0");


        try{
            new WebDriverWait(driver, 10).until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(css)));
        }
        catch (Exception ignore) {}
        for(int i = 0; i < 10; i++) {
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "document.querySelectorAll('" + css + "')["+ elemIndex + "].click()");
                break;
            }
            catch (Exception e) {
                if(i == 9)
                    LogHelper.getLogger().log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    @Deprecated
    public static int clickOnElement(WebDriver driver, WebElement webElement, boolean isUseJavascript) {
        if(isUseJavascript)
            return clickOnElementJavaScript(driver, webElement);
        else
            return clickOnElement(driver, webElement);
    }

    /**
     * Браузер нажимает по очереди по всем кнопкам на первой странице и всех идущих за ней
     * и парсит финальные страницы без кнопок
     * @param parser используется для парсинга конечных страниц
     * @param connection используется для перехода к странице без нажатие на кнопку
     * @param connectionProperties параметры подключения к странице
     * @param cssSelectors CSS селекторы кнопок. Селекторы разделять через ','. Селекторы будут использоваться на одной
     *                     странице в порядке очереди. Как только по селектору будет найден элемент, последующие селекторы
     *                     игнорируются. Если после нажатия на элемент требуется нажатие на ещё какую-нибудь кнопку,
     *                     например отправки формы, селектор кнопки должен идти сразу после селектора на элемент и
     *                     знака '#'. Пример: div > button, div > button.option#[type="submit"]
     * @param isUseJavascript указывает, использовать ли javascript для нажатие на кнопку
     * @return коллекция полученных данных ввиде DataArray
     */
    public static Set<DataArray> clickOnButtonsAndReturnDocuments(DocumentParser parser,
                                                                  SeleniumConnection connection,
                                                                  ConnectionProperties connectionProperties,
                                                                  String cssSelectors,
                                                                  boolean isUseJavascript) {
        Set<DataArray> result = new HashSet<>();
        WebDriver driver = connection.getDriver();
        List<Integer> variants = new ArrayList<>();
        final String firstPage = driver.getCurrentUrl();
        boolean isLastPage = false;
        List<ElementAndSubmitButton> selectors = parseSelectors(cssSelectors);
        //selectors.forEach(sl -> System.out.println(sl.elementSelector + " ### " + sl.submitButtonSelector));

        while (!isLastPage) {
            for(int i = 0;; i++) {
                int selectorIndex = -1;
                Document document = Jsoup.parse(driver.getPageSource());
                for(int j = 0; j < selectors.size(); j++) {
                    ElementAndSubmitButton s = selectors.get(j);
                    if(document.select(s.elementSelector).size() > 0
                        && (s.submitButtonSelector == null
                            || document.select(s.submitButtonSelector).size() > 0)) {
                        selectorIndex = j;
                        break;
                    }
                }

                if(selectorIndex >= 0) {
                    List<WebElement> buttons = driver.findElements(By.cssSelector(selectors.get(selectorIndex).elementSelector));
                    // индексы кнопок добавляются в коллекцию, если они ещё не встречались
                    if(i >= variants.size())
                        variants.add(0);
                    // счётчик сбрасывается, если он больше количества кнопок
                    if(variants.get(i) >= buttons.size()) {
                        variants.set(i, 0);
                        if(i+1 < variants.size())
                            variants.set(i+1, variants.get(i+1)+1);
                        else {
                            isLastPage = true;
                            break;
                        }
                    }
                    // нажимается выбранная кнопка
                    if(isUseJavascript)
                        clickOnElementJavaScript(driver, buttons.get(variants.get(i)));
                    else
                        clickOnElement(driver, buttons.get(variants.get(i)));

                    //нажимается кнопка отправки формы
                    if(selectors.get(selectorIndex).submitButtonSelector != null) {
                        if(isUseJavascript)
                            clickOnElementJavaScript(driver, driver.findElement(By.cssSelector(selectors.get(selectorIndex).submitButtonSelector)));
                        else
                            clickOnElement(driver, driver.findElement(By.cssSelector(selectors.get(selectorIndex).submitButtonSelector)));
                    }

                    if(connectionProperties.getWaitForIt().size() > 0)
                        waitElement(driver, connectionProperties.getWaitForIt().get(0), 10);
                    if(connectionProperties.getEvents().size() > 0)
                        connectionProperties.getEvents().forEach(connectionEvent -> connectionEvent.event(driver, driver.getCurrentUrl()));
                }
                else {
                    result.addAll(parser.parseData(driver.getCurrentUrl(), document));
                    break;
                }
            }
            //возвращаемся на первоначальную страницу
            connection.getPage(firstPage, connectionProperties);

            //итерируется индекс кнопок на первой странице
            variants.set(0, variants.get(0)+1);
        }

        return result;
    }

    private static List<ElementAndSubmitButton> parseSelectors(String cssSelectors) {
        List<ElementAndSubmitButton> result = new ArrayList<>();
        for(String selector : cssSelectors.split(",")) {
            String[] selectors = selector.split("#");
            if(selectors.length == 1)
                result.add(new ElementAndSubmitButton(selectors[0].trim(), null));
            else if(selectors.length == 2)
                result.add(new ElementAndSubmitButton(selectors[0].trim(), selectors[1].trim()));
        }
        return result;
    }

    private static class ElementAndSubmitButton {
        public final String elementSelector;
        public final String submitButtonSelector;

        public ElementAndSubmitButton(String elementSelector, String submitButtonSelector) {
            this.elementSelector = elementSelector;
            this.submitButtonSelector = submitButtonSelector;
        }
    }

    /**
     * Браузер нажимает по очереди по всем кнопкам на первой странице и всех идущих за ней
     * и парсит финальные страницы без кнопок
     * @param parser используется для парсинга конечных страниц
     * @param connection используется для перехода к странице без нажатие на кнопку
     * @param waitForTheseElements CSS селекторы элементов, которые браузер должен ожидать на странице. Не обязательно
     * @param cssSelectors CSS селекторы кнопок. Селекторы разделять через ','. Селекторы будут использоваться на одной
     *                     странице в порядке очереди. Как только по селектору будет найден элемент, последующие селекторы
     *                     игнорируются. Если после нажатия на элемент требуется нажатие на ещё какую-нибудь кнопку,
     *                     например отправки формы, селектор кнопки должен идти сразу после селектора на элемент и
     *                     знака '#'. Пример: div > button, div > button.option#[type="submit"]
     * @param isUseJavascript указывает, использовать ли javascript для нажатие на кнопку
     * @return коллекция полученных данных ввиде DataArray
     */
    public static Set<DataArray> clickOnButtonsAndReturnDocuments(DocumentParser parser,
                                                                  SeleniumConnection connection,
                                                                  String waitForTheseElements,
                                                                  String cssSelectors,
                                                                  boolean isUseJavascript) {
        ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setWaitForIt(Collections.singletonList(waitForTheseElements));
        return clickOnButtonsAndReturnDocuments(parser, connection, connectionProperties, cssSelectors, isUseJavascript);
    }

    /**
     * Браузер нажимает по очереди по всем кнопкам на первой странице и возвращает коллекцию ссылок
     * на которые эти кнопки ведут
     * @param connection используется для возращения к первоначальной странице с кнопками
     * @param connectionProperties параметры подключения к странице
     * @param cssSelector CSS селектор кнопки
     * @param isUseJavascript указывает, использовать ли javascript для нажатие на кнопку
     * @return коллекция уникальных ссылок
     */
    public static Set<String> clickOnButtonsAndReturnLinks(SeleniumConnection connection, ConnectionProperties connectionProperties,
                                                           String cssSelector, boolean isUseJavascript) {
        Set<String> result = new HashSet<>();

        WebDriver driver = connection.getDriver();
        final String firstPage = driver.getCurrentUrl();
        for(int i = 0;; i++) {
            List<WebElement> buttons = driver.findElements(By.cssSelector(cssSelector));
            if(i >= buttons.size())
                break;

            if(isUseJavascript)
                clickOnElementJavaScript(driver, buttons.get(i));
            else
                clickOnElement(driver, buttons.get(i));
            if(connectionProperties.getWaitForIt().size() > 0)
                waitElement(driver, connectionProperties.getWaitForIt().get(0), 5);

            result.add(driver.getCurrentUrl());
            connection.getPage(firstPage, connectionProperties);
        }

        return result;
    }

    /**
     * Браузер нажимает по очереди по всем кнопкам на первой странице и возвращает коллекцию ссылок
     * на которые эти кнопки ведут
     * @param connection используется для возращения к первоначальной странице с кнопками
     * @param waitForTheseElements CSS селекторы элементов, которые браузер должен ожидать на странице. Не обязательно
     * @param cssSelector CSS селектор кнопки
     * @param isUseJavascript указывает, использовать ли javascript для нажатие на кнопку
     * @return коллекция уникальных ссылок
     */
    public static Set<String> clickOnButtonsAndReturnLinks(SeleniumConnection connection, String waitForTheseElements,
                                                           String cssSelector, boolean isUseJavascript) {
        ConnectionProperties connectionProperties = new ConnectionProperties();
        connectionProperties.setWaitForIt(Collections.singletonList(waitForTheseElements));
        return clickOnButtonsAndReturnLinks(connection, connectionProperties, cssSelector, isUseJavascript);
    }

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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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

            for(WebElement element : elements) {
                try{
                    if(css != null && !css.isEmpty())
                        new WebDriverWait(driver, 10).until(
                            ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(css)));
                }
                catch (Exception ignore) {}

                if(useJavascript)
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                else {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
                    actions.moveToElement(element).click(element).build().perform();
                }
                result++;
                if(oneElement)
                    break;
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

    public static void waitPageComplete(WebDriver driver, int seconds) {
        new WebDriverWait(driver, seconds).until(
                webDriver -> (Boolean) (((JavascriptExecutor) webDriver).executeScript("return jQuery.active == 0"))
                        && ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
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

    public static void moveDownToEndOfPage(WebDriver driver) {
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

    public static Map<String, String> webDriverCookieToMap(Set<Cookie> cookieSet) {
        Map<String, String> result = new HashMap<>();
        cookieSet.forEach(cookie -> result.put(cookie.getName(), cookie.getValue()));
        return result;
    }

    private WebDriverUtil() {}
}
