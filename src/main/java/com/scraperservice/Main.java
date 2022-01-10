package com.scraperservice;

import com.scraperservice.manager.ScrapeManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        if(args.length == 15) {
            Thread managerThread = new Thread(ScrapeManager.getInstance());
            managerThread.start();
        }
        Files.writeString(Paths.get("test.txt"), Arrays.toString(args));
    }
}
