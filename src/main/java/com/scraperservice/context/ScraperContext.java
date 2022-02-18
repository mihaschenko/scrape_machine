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
import com.scraperservice.storage.writer.RemoteServerDataWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan(value = "com.scraperservice")
@PropertySources({@PropertySource("classpath:scraperApplication.properties"),
    @PropertySource("classpath:remoteServerStorage.properties")})
public class ScraperContext {
    @Bean
    @Scope("singleton")
    public DataSaveManager dataSaveManager(ScraperSetting scraperSetting,
                                           @Value("${remote.storage.url}") String url,
                                           @Value("${remote.storage.key}") String key) throws IOException {
        DataSaveManager dataSaveManager = new DataSaveManager();
        dataSaveManager.addDataWriter(new CSVDataWriter(scraperSetting.getScraper().getClass().getSimpleName()));
        if(scraperSetting.isSaveRemoteServer())
            dataSaveManager.addDataWriter(new RemoteServerDataWriter(url, key));
        return dataSaveManager;
    }

    @Bean
    @Scope("singleton")
    public Scraper scraper(ScraperSetting scraperSetting) {
        return new ScraperLogProxy(scraperSetting.getScraper());
    }

    @Bean
    @Scope("singleton")
    public ExecutorService executorService(@Value("${scraper.manager.task.pool}") int taskPoolSize) {
        return Executors.newFixedThreadPool(taskPoolSize);
    }

    @Bean
    @Scope("singleton")
    public ConnectionPool connectionPool(ScraperSetting scraperSetting,
                                         @Value("${scraper.manager.connection.jsoup.pool}") int jsoupPoolSize,
                                         @Value("${scraper.manager.connection.selenium.pool}") int SeleniumPoolSize)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        int connectionPoolSize;
        Class<? extends Connection> connectionClass = scraperSetting.getConnectionClass();
        if(connectionClass.isAssignableFrom(SeleniumConnection.class))
            connectionPoolSize = SeleniumPoolSize;
        else if(connectionClass.isAssignableFrom(JsoupConnection.class))
            connectionPoolSize = jsoupPoolSize;
        else
            throw new RuntimeException();
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

    @Bean
    @Scope("singleton")
    public ScraperSetting scraperSetting() throws Exception {
        ScraperSetting scraperSetting = new ScraperSetting();
        scraperSetting.choice();
        return scraperSetting;
    }
}
