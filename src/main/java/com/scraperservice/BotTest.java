package com.scraperservice;

import com.scraperservice.connection.SeleniumConnection;
import com.scraperservice.connection.setting.ConnectionProperties;
import org.jsoup.nodes.Document;

import java.nio.file.Files;
import java.nio.file.Paths;

public class BotTest {
    public static void main(String[] args) throws Exception {
        try(SeleniumConnection connection = new SeleniumConnection()) {
            ConnectionProperties connectionProperties = new ConnectionProperties();
            connectionProperties.setRandomDelay(new ConnectionProperties.RandomDelay(10000, 15000));
            Document html = connection.getPage("https://bot.incolumitas.com", connectionProperties);
            Files.writeString(Paths.get("botTest.html"), html.outerHtml());
        }
    }
}
