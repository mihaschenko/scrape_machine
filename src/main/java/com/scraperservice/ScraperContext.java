package com.scraperservice;

import com.scraperservice.connection.Connection;
import com.scraperservice.connection.ConnectionPool;
import com.scraperservice.connection.JsoupConnection;
import com.scraperservice.connection.SeleniumConnection;
import com.scraperservice.manager.StatisticManager;
import com.scraperservice.scraper.Scraper;
import com.scraperservice.storage.writer.CSVDataWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.util.concurrent.*;

@Configuration
@ComponentScan(value = "com.scraperservice")
@PropertySource("classpath:scraperApplication.properties")
public class ScraperContext {
    private final ApplicationContext applicationContext;

    public ScraperContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean("csvDataWriter")
    public CSVDataWriter csvDataWriter(ScraperSetting scraperSetting) throws IOException {
        return new CSVDataWriter(scraperSetting.getScraper().getClass().getSimpleName());
    }

    @Bean
    public Scraper scraper(ScraperSetting scraperSetting, ConnectionPool connectionPool, StatisticManager statisticManager) {
        scraperSetting.getScraper().setConnectionPool(connectionPool);
        return new ScraperProxy(scraperSetting.getScraper(), statisticManager);
    }

    @Bean("blockingQueue")
    public BlockingQueue<Runnable> blockingQueue() {
        return new LinkedBlockingDeque<>(20);
    }

    @Bean
    public ExecutorService executorService(@Value("${scraper.manager.task.pool}") int taskPoolSize,
                                           @Qualifier("blockingQueue") BlockingQueue<Runnable> runnableBlockingQueue) {
        return new ThreadPoolExecutor(taskPoolSize, taskPoolSize, 0L, TimeUnit.MILLISECONDS, runnableBlockingQueue);
    }



    @Bean
    public ConnectionPool connectionPool(ScraperSetting scraperSetting,
                                         @Value("${scraper.manager.connection.jsoup.pool}") int jsoupPoolSize,
                                         @Value("${scraper.manager.connection.selenium.pool}") int SeleniumPoolSize)
            throws InterruptedException {
        int connectionPoolSize;
        Class<? extends Connection> connectionClass = scraperSetting.getConnectionClass();
        if(connectionClass.isAssignableFrom(SeleniumConnection.class))
            connectionPoolSize = SeleniumPoolSize;
        else if(connectionClass.isAssignableFrom(JsoupConnection.class))
            connectionPoolSize = jsoupPoolSize;
        else
            connectionPoolSize = 1;

        if(connectionPoolSize <= 0)
            connectionPoolSize = 1;

        ArrayBlockingQueue<Connection<?>> pool = new ArrayBlockingQueue<>(connectionPoolSize);
        for(int i = 0; i < connectionPoolSize; i++) {
            pool.put(applicationContext.getBean(scraperSetting.getConnectionClass()));
        }

        return new ConnectionPool(pool);
    }
}
