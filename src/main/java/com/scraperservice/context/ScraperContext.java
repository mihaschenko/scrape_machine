package com.scraperservice.context;

import com.scraperservice.ScraperLogProxy;
import com.scraperservice.ScraperSetting;
import com.scraperservice.connection.Connection;
import com.scraperservice.connection.JsoupConnection;
import com.scraperservice.connection.SeleniumConnection;
import com.scraperservice.connection.pool.ConnectionPool;
import com.scraperservice.manager.DataSaveManager;
import com.scraperservice.queue.ConcurrentLinkedQueueUnique;
import com.scraperservice.scraper.Scraper;
import com.scraperservice.storage.writer.CSVDataWriter;
import org.springframework.context.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan(value = "com.scraperservice")
public class ScraperContext {
    @Bean
    @Scope("singleton")
    public DataSaveManager dataSaveManager(ScraperSetting scraperSetting) throws IOException {
        DataSaveManager dataSaveManager = new DataSaveManager();
        dataSaveManager.addDataWriter(new CSVDataWriter(scraperSetting.getScraper().getClass().getSimpleName()));
        return dataSaveManager;
    }

    @Bean
    @Scope("singleton")
    public Scraper scraper(ScraperSetting scraperSetting) {
        return new ScraperLogProxy(scraperSetting.getScraper());
    }

    @Bean
    @Scope("singleton")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(10);
    }

    @Bean
    @Scope("singleton")
    public ConnectionPool connectionPool(ScraperSetting scraperSetting)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        int connectionPoolSize;
        Class<? extends Connection> connectionClass = scraperSetting.getConnectionClass();
        if(connectionClass.isAssignableFrom(SeleniumConnection.class))
            connectionPoolSize = 3;
        else if(connectionClass.isAssignableFrom(JsoupConnection.class))
            connectionPoolSize = 10;
        else
            connectionPoolSize = 5;
        return new ConnectionPool(connectionPoolSize, scraperSetting.getConnectionClass(), new Object[0]);
    }

    @Bean
    @Scope("singleton")
    @Primary
    public ConcurrentLinkedQueueUnique concurrentLinkedQueueUnique(ScraperSetting scraperSetting) throws SQLException {
        ConcurrentLinkedQueueUnique concurrentLinkedQueueUnique = new ConcurrentLinkedQueueUnique();
        if(scraperSetting.getStartLinks() != null)
            concurrentLinkedQueueUnique.addAll(scraperSetting.getStartLinks());
        return concurrentLinkedQueueUnique;
    }
}
