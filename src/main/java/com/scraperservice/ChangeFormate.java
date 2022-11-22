package com.scraperservice;

import com.scraperservice.utils.HTMLUtil;
import com.scraperservice.utils.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ChangeFormate {
    public static void main(String[] args) throws Exception {
        final String pathName = "commandcenterconcrete";

        AtomicInteger totalFiles = new AtomicInteger(0);
        AtomicInteger successFiles = new AtomicInteger(0);
        Set<String> fileNames = new HashSet<>();
        AtomicInteger counter = new AtomicInteger(1);

        Files.walk(Paths.get(pathName))
                .filter(file -> {
                    totalFiles.incrementAndGet();
                    try {
                        //System.out.println(Files.isRegularFile(file) + " " + file.endsWith(".html") + " " + Files.size(file) + " " + file.toFile().getName());
                        return Files.isRegularFile(file) && file.toFile().getName().endsWith(".html") && Files.size(file) > 1024;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .forEach(file -> {
                    successFiles.incrementAndGet();
                    try {
                        Document document = Jsoup.parse(String.join("\n", Files.readAllLines(file)));
                        document.select("meta").stream().filter(element -> {
                                    String content = element.attr("content");
                                    if(element.attr("name").equals("viewport")
                                            || !element.attr("og:image:type").isEmpty()
                                            || element.attr("property").contains("_time"))
                                        return true;
                                    if(content.matches("[0-9]+") || content.matches("http.+"))
                                        return true;
                                    return false;
                                })
                                .forEach(Element::remove);

                        String html = document.outerHtml();
                        html = RegexUtil.replaceAll("<meta.+?content=\"([^\"]+)\".*?>", html, 1);
                        html = RegexUtil.replaceAll("<img.+?name=\"([^\"]+)\".*?>", html, 1);
                        html = new HTMLUtil(html).removeTagAndContent("style").removeTagAndContent("script")
                                .removeAllTags().removeEmptyLines().toString();

                        String fileName = file.toFile().getName();
                        if(fileNames.contains(fileName))
                            fileName = fileName.replaceFirst("\\.html", "_" + counter.getAndIncrement() + ".txt");
                        else {
                            fileNames.add(fileName);
                            fileName = fileName.replaceFirst("\\.html", ".txt");
                        }

                        Files.writeString(Paths.get("commandcenterconcrete_txt/" + fileName), html);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        System.out.println(totalFiles.get() + " " + successFiles.get());
    }
}
