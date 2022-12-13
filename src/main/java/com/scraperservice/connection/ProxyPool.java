package com.scraperservice.connection;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ProxyPool {
    private final List<Proxy> proxyList;
    private int counter = 0;

    public ProxyPool() throws IOException {
        proxyList = Collections.unmodifiableList(
                Files.readAllLines(Paths.get("src/main/resources/proxyList.txt"))
                        .stream()
                        .map(line -> line.split(":"))
                        .filter(lineParts -> lineParts.length >= 2 && lineParts[lineParts.length-1].matches("[0-9]+"))
                        .map(lineParts -> {
                            String host = String.join(":", Arrays.copyOfRange(lineParts, 0, lineParts.length-1));
                            int port = Integer.parseInt(lineParts[lineParts.length-1]);
                            return new Proxy(host, port);
                        }).toList());
    }

    public boolean isProxyExist() {
        return proxyList.size() > 0;
    }

    public synchronized Proxy getProxy() {
        if(proxyList.size() == 0)
            return null;
        else if(counter >= proxyList.size())
            counter = 0;
        return proxyList.get(counter++);
    }
}
