package com.scraperservice.helper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogHelper {
    private static final Logger logger;

    public static Logger getLogger() {return logger;}

    static {
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream("src/main/resources/logConfig.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger = Logger.getLogger(Class.class.getName());
        logger.setLevel(Level.ALL);
    }

    private LogHelper() {}
}
