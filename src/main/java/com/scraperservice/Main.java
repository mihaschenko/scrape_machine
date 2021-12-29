package com.scraperservice;

import com.scraperservice.manager.ScrapeManager;

public class Main {
    public static void main(String[] args) {
        Thread managerThread = new Thread(ScrapeManager.getInstance());
        managerThread.start();
    }
}
