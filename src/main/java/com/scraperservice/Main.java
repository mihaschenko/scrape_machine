package com.scraperservice;

import com.scraperservice.manager.ScrapeManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        createDirectories();

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ScraperContext.class);
        ScrapeManager scrapeManager = context.getBean(ScrapeManager.class);

        Thread managerThread = new Thread(scrapeManager);
        managerThread.start();
        managerThread.join();
        context.close();
    }

    private static void createDirectories() throws IOException {
        if(Files.notExists(Paths.get("log")))
            Files.createDirectory(Paths.get("log"));
        if(Files.notExists(Paths.get("temp")))
            Files.createDirectory(Paths.get("temp"));
        if(Files.notExists(Paths.get("results")))
            Files.createDirectory(Paths.get("results"));
    }
}