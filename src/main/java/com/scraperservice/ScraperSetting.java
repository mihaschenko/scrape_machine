package com.scraperservice;

import com.scraperservice.connection.Connection;
import com.scraperservice.connection.JsoupConnection;
import com.scraperservice.helper.ConsoleHelper;
import com.scraperservice.scraper.Scraper;
import lombok.Data;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Data
@Component
public class ScraperSetting {
    private static final String CONNECTION_CLASSES_PACKAGE = "com.scraperservice.connection";
    private static final String SCRAPER_CLASSES_PACKAGE = "com.scraperservice.scraper";

    protected Scraper scraper;
    protected Class<? extends Connection> connectionClass;
    protected List<String> startLinks;

    public ScraperSetting() {}
    public ScraperSetting(Scraper scraper) {
        this(scraper, JsoupConnection.class, Collections.emptyList(), false);
    }
    public ScraperSetting(Scraper scraper, Class<? extends Connection> connectionClass) {
        this(scraper, connectionClass, Collections.emptyList(), false);
    }
    public ScraperSetting(Scraper scraper, Class<? extends Connection> connectionClass, List<String> startLinks) {
        this(scraper, connectionClass, startLinks, false);
    }
    public ScraperSetting(Scraper scraper, Class<? extends Connection> connectionClass, List<String> startLinks, boolean isUseProxy) {
        this.scraper = scraper;
        this.connectionClass = connectionClass;
        this.startLinks = startLinks;
    }

    @PostConstruct
    public void init() throws Exception {
        setScraper();
        setConnection();
        setStartLinks();
    }

    private void setScraper()
            throws ClassNotFoundException, IOException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException, InterruptedException {
        Reflections reflections = new Reflections(SCRAPER_CLASSES_PACKAGE);

        List<Class<? extends Scraper>> allClasses =
                new ArrayList<>(reflections.getSubTypesOf(Scraper.class));
        allClasses.sort(Comparator.comparing(Class::getSimpleName));
        if(allClasses.size() > 0) {
            System.out.println("I. CHOOSE A SCRAPER");
            Class<? extends Scraper> clazz = getUserChoice(allClasses);
            scraper = clazz.getConstructor().newInstance();
        }
        else
            throw new ClassNotFoundException("There are not Scraper classes");
    }

    private void setConnection() throws IOException, ClassNotFoundException, InterruptedException {
        Reflections reflections = new Reflections(CONNECTION_CLASSES_PACKAGE);

        List<Class<? extends Connection>> allClasses =
                new ArrayList<>(reflections.getSubTypesOf(Connection.class));
        if(allClasses.size() > 0) {
            System.out.println("II. CHOOSE A CONNECTION");
            connectionClass = getUserChoice(allClasses);
        }
        else
            throw new ClassNotFoundException("There are not Connection classes");
    }

    private <T> Class<? extends T> getUserChoice(List<Class<? extends T>> allClasses) throws IOException, InterruptedException {
        while(true) {
            System.out.println("Enter an option or 'exit':");
            for(int i = 0; i < allClasses.size(); i++) {
                Class<? extends T> clazz = allClasses.get(i);
                System.out.println("\t" + i + ". " + clazz.getSimpleName());
            }
            String userChoice = ConsoleHelper.readLine().trim();
            if(userChoice.equals("exit"))
                throw new InterruptedException("User called exit command");
            if(userChoice.matches("[0-9]+")) {
                int classIndex = Integer.parseInt(userChoice);
                if(classIndex >= 0 && classIndex < allClasses.size()) {
                    Class<? extends T> result = allClasses.get(classIndex);
                    System.out.println("You chose: " + result.getSimpleName());
                    return result;
                }
                else
                    System.out.println("There is no such item. Try again!");
            }
            else
                System.out.println("It isn't a digit. Try again!");
        }
    }

    private void setStartLinks() throws IOException {
        String result;
        System.out.println("III. CHOOSE START LINKS");
        System.out.println("""
                Initial links options. There are the following options:
                \t* Hit "Enter" to use default links from config or class file
                \t* Provide links separated by semicolon
                \t* Type 'file::<pathToFile>' - file with list of links""");
        result = ConsoleHelper.readLine().trim();
        if(result.startsWith("file::"))
            startLinks = Files.readAllLines(Paths.get(result.replaceFirst("file::", "")));
        else if(!result.isEmpty())
            startLinks = Arrays.stream(result.trim().split(";")).toList();
        else
            startLinks = Collections.emptyList();
    }
}