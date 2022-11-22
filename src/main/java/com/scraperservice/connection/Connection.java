package com.scraperservice.connection;

import com.scraperservice.connection.setting.ConnectionProperties;
import com.scraperservice.scraper.page.PageData;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Connection<T> implements AutoCloseable {
    private static final ConnectionProperties CONNECTION_PROPERTIES = new ConnectionProperties();

    /**
     * Метод проводит подключение к странице и возвращает html документ
     * @param url ссылка
     * @param setting параметры подключения
     * @return html документ
     */
    public abstract Document getPage(String url, ConnectionProperties setting) throws IOException;

    /**
     * Метод проводит подключение к странице и сохраняет html документ в экземпляр класса PageData
     * @param pageData хранит информацию о странице. В том числе и ссылку
     * @param setting параметры подключения
     */
    public abstract void getPage(PageData pageData, ConnectionProperties setting) throws IOException;

    /**
     * Метод проводит подключение к странице и возвращает html документ. Параметры подключения устанавливаются
     * по умолчанию
     * @param url ссылка
     * @return html документ
     */
    public Document getPage(String url) throws IOException {
        return getPage(url, CONNECTION_PROPERTIES);
    }

    /**
     * Метод проводит необходимые действия перед подключением к странице. Не обязателен
     * @param t экземпляр класса, отвечающий за подключение или работы со страницей
     * @param pageData информация о странице. В том числе и ссылка
     * @param connectionProperties параметры подключения
     */
    public abstract void before(T t, PageData pageData, ConnectionProperties connectionProperties);

    /**
     * Метод проводит необходимые действия после подключением к странице. Не обязателен
     * @param t экземпляр класса, отвечающий за подключение или работы со страницей
     * @param pageData информация о странице. В том числе и ссылка
     * @param connectionProperties параметры подключения
     */
    public abstract void after(T t, PageData pageData, ConnectionProperties connectionProperties);

    protected void delay(ConnectionProperties connectionProperties) {
        try{
            if(connectionProperties.getRandomDelay() != null)
                Thread.sleep(ThreadLocalRandom.current().nextLong(connectionProperties.getRandomDelay().from,
                        connectionProperties.getRandomDelay().to));
            else if(connectionProperties.getDelay() > 0)
                Thread.sleep(connectionProperties.getDelay());
        }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
