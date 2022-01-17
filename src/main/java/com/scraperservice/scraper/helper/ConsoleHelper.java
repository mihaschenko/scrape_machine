package com.scraperservice.scraper.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private final static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static String readLine() throws IOException {
        return bufferedReader.readLine();
    }
}
