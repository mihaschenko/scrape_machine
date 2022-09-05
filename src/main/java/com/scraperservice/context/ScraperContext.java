package com.scraperservice.context;

import com.scraperservice.ScraperLogProxy;
import com.scraperservice.ScraperSetting;
import com.scraperservice.UniqueValuesStorage;
import com.scraperservice.connection.Connection;
import com.scraperservice.connection.JsoupConnection;
import com.scraperservice.connection.SeleniumConnection;
import com.scraperservice.connection.ConnectionPool;
import com.scraperservice.manager.DataSaveManager;
import com.scraperservice.scraper.Scraper;
import com.scraperservice.storage.writer.CSVDataWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.*;

@Configuration
@ComponentScan(value = "com.scraperservice")
@PropertySources({@PropertySource("classpath:scraperApplication.properties")})
public class ScraperContext {
    @Bean
    public DataSaveManager dataSaveManager(ScraperSetting scraperSetting) throws IOException {
        DataSaveManager dataSaveManager = new DataSaveManager();
        dataSaveManager.addDataWriter(new CSVDataWriter(scraperSetting.getScraper().getClass().getSimpleName()));
        return dataSaveManager;
    }

    @Bean
    public Scraper scraper(ScraperSetting scraperSetting, ConnectionPool connectionPool) {
        scraperSetting.getScraper().setConnectionPool(connectionPool);
        return new ScraperLogProxy(scraperSetting.getScraper());
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
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        int connectionPoolSize;
        Class<? extends Connection> connectionClass = scraperSetting.getConnectionClass();
        if(connectionClass.isAssignableFrom(SeleniumConnection.class))
            connectionPoolSize = SeleniumPoolSize;
        else if(connectionClass.isAssignableFrom(JsoupConnection.class))
            connectionPoolSize = jsoupPoolSize;
        else
            throw new RuntimeException();
        return new ConnectionPool(connectionPoolSize, scraperSetting.getConnectionClass());
    }

    @Bean
    @Primary
    public UniqueValuesStorage uniqueValuesStorage(ScraperSetting scraperSetting) throws SQLException {
        UniqueValuesStorage uniqueValuesStorage = new UniqueValuesStorage();
        if(scraperSetting.getStartLinks() != null)
            uniqueValuesStorage.addAll(scraperSetting.getStartLinks());
        return uniqueValuesStorage;
    }

    @Bean
    public ScraperSetting scraperSetting() throws Exception {
        ScraperSetting scraperSetting = new ScraperSetting();
        scraperSetting.init();
        return scraperSetting;
    }
}
