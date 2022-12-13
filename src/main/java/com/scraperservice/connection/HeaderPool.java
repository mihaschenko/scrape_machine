package com.scraperservice.connection;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Component
public class HeaderPool {
    private final Map<String, String> header;

    public HeaderPool() throws IOException {
        Map<String, String> headMap = new HashMap<>();
        Files.readAllLines(Paths.get("src/main/resources/header.txt"))
                .forEach(line -> {
                    String[] lineParts = line.split(" ");
                    if(lineParts.length > 1)
                        headMap.put(lineParts[0], String.join(" ",
                                Arrays.copyOfRange(lineParts, 1, lineParts.length)));
                });
        header = Collections.unmodifiableMap(headMap);
    }

    public boolean isHeadExist() {
        return header.size() > 0;
    }

    public Map<String, String> getHead() {
        return header;
    }
}
