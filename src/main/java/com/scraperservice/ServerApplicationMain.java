package com.scraperservice;

import com.scraperservice.context.ScraperContext;
import com.scraperservice.manager.ScrapeManager;
import com.scraperservice.scraper.ScraperService;
import com.web.application.entity.Run;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerApplicationMain {
    public static void main(String[] args) throws Exception {
        if(args.length == 1) {
            Run run = getRun(args[0]);
            if(run != null) {
                AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
                context.registerBean(ScraperSetting.class, new ScraperService(run));
                context.register(ScraperContext.class);
                context.refresh();
                Main.scrapeManager = context.getBean(ScrapeManager.class);
                Thread managerThread = new Thread(Main.scrapeManager);
                managerThread.start();
            }
        }
        else
            System.out.println("args.length = 0");
    }

    private static Run getRun(String fileName) throws IOException, ClassNotFoundException {
        final File file = new File(fileName);
        Run run = null;
        if(file.isFile()) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
                run = (Run) objectInputStream.readObject();
            } finally {
                file.delete();
            }
        }
        return run;
    }
}
