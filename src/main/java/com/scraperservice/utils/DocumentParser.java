package com.scraperservice.utils;

import com.scraperservice.storage.DataArray;
import org.jsoup.nodes.Document;

import java.util.Set;

@FunctionalInterface
public interface DocumentParser {
    Set<DataArray> parseData(String url, Document document);
}
