package com.scraperservice;

import com.scraperservice.context.ManualScraperContext;
import com.scraperservice.manager.ScrapeManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class Main {
    public static ScrapeManager scrapeManager;

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ManualScraperContext.class);
        scrapeManager = context.getBean(ScrapeManager.class);
        Thread managerThread = new Thread(scrapeManager);
        managerThread.start();
    }
}