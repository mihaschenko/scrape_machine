# Scrape_machine
(EN)

The program collects data from the site. The main category of sites where there are product pages (store data). The program will start with one or a group of pages and search through the links for pages with the required data. For the data to be collected, a small instruction class needs to be written. The class should be created in the com.scraperservice.scraper package. The class must be inherited from the Scraper class and its name must end with the word Scraper (example: LinkedinScraper.java). The class implementation only affects html handling and data retrieval

The *ScraperExample* class is an example for writing an instruction (its name does not match the spelling rule of the name). The main classes that will be useful when collecting data:

 - ScrapeUtil - general methods for getting text/attributes/pieces of code from an html page
 - HTMLUtil - methods for working with html. Removing a number of tags, attributes, etc.
 - ScrapeLinkUtil - methods for working with group of links in tags 'a' and 'img'.
 - TableUtil - saving the tables to json.
When launching, the program will ask to go through 3 points:

Select an instruction. The instruction will be found if it matches all the rules
Specify the initial link/references where the scraper will start from. You can leave it blank. Then the reference will be taken from the instruction class
Choose what to use for queries:
*Jsoup*. For simple get or post requests without JS running
*Selenium*. Using browser (Chrome) in headless mode for requests. For Selenium to work you need a driver and Chrome browser installed. You can download the driver from here https://chromedriver.chromium.org/downloads. After that, create a folder driver in the root of the project and move the driver file there naming it 'chromedriver.exe'.
At the moment, results are saved in a СSV file. The results will be saved in the *results* folder.

--------
(RU)

Программа собирает данные с сайта. Основная категория сайтов, 
где есть страницы продуктов (хранят данные). Программа начнёт работу с одной или группы страниц
и будет по ссылкам искать страницы с требуемыми данными. Чтобы данные собирались, 
требуется написание небольшого класса-инструкции. Класс следует 
создать в пакете com.scraperservice.scraper. Класс должен наследоваться 
от класса Scraper и его имя должно оканчиваться на слово Scraper (пример: LinkedinScraper.java). Реализация класса только затрагивает 
работу с html и получения данных

Примером для написания инструкции служит класс ScraperExample (его имя не соответствует правилу написания имени).
Основные классы, которые будут полезны при сборе данных:
- ScrapeUtil - общие методы для получения текста/аттрибутов/кусков кода из html страницы
- HTMLUtil - методы для работы с html. Удаления ряда тегов, аттрибутов и т.д.
- ScrapeLinkUtil - методы для работы с группой ссылок в тегах 'a' и 'img'
- TableUtil - сохранение таблиц в json

При запуске, программа попросит пройти 3 пункта:
1. Выбрать инструкцию. Инструкция будет найдена, если она соответствует всем правилам
2. Указать начальную ссылку/ссылки, откуда скрейпер начнёт работать. Можно оставить пустым. Тогда ссылка будет взята с класса-инструкции
3. Выбор того, что использовать для запросов:
   - Jsoup. Для простых get или post запросов без работы JS
   - Selenium. Использования браузера (Chrome) в headless режиме для запросов. Для работы Selenium понадобится драйвер и установленный браузер Chrome.
Драйвер скачать можно отсюда https://chromedriver.chromium.org/downloads. После, создайте папку driver в корне проекта и перенесите файл драйвера туда назвав его 'chromedriver.exe'.

На данный момент, результаты сохраняются в csv файл. Результат будет сохранён в папку results
