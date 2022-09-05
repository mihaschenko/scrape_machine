package com.scraperservice.manager;

import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class StatisticManager {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");

    private final AtomicInteger totalUniqueLinks;
    private final AtomicInteger totalSuccessUniqueLinks;
    private final AtomicInteger totalFailUniqueLinks;
    private final AtomicInteger duplicateLinksCounter;
    private final AtomicInteger productsCounter;
    private final AtomicInteger productsSuccessCounter;
    private final AtomicInteger productsFailCounter;

    private final ConcurrentHashMap<PageType, Integer> pageTypeCounter;
    private final ConcurrentHashMap<String, Integer> emptyImportantData;
    private final ConcurrentHashMap<String, Integer> emptyNotImportantData;

    public int getTotalUniqueLinks() {return totalUniqueLinks.get();}
    public int getTotalSuccessUniqueLinks() {return totalSuccessUniqueLinks.get();}
    public int getTotalFailUniqueLinks() {return totalFailUniqueLinks.get();}
    public int getDuplicateLinksCounter() {return duplicateLinksCounter.get();}
    public int getProductsCounter() {return productsCounter.get();}
    public int getProductsSuccessCounter() {return productsSuccessCounter.get();}
    public int getProductsFailCounter() {return productsFailCounter.get();}

    public Map<PageType, Integer> getPageTypeCounter() {return Collections.unmodifiableMap(pageTypeCounter);}
    public Map<String, Integer> getEmptyImportantData() {return Collections.unmodifiableMap(emptyImportantData);}
    public Map<String, Integer> getEmptyNotImportantData() {return Collections.unmodifiableMap(emptyNotImportantData);}

    private StatisticManager() {
        totalUniqueLinks = new AtomicInteger(0);
        totalSuccessUniqueLinks = new AtomicInteger(0);
        totalFailUniqueLinks = new AtomicInteger(0);
        duplicateLinksCounter = new AtomicInteger(0);
        productsCounter = new AtomicInteger(0);
        productsSuccessCounter = new AtomicInteger(0);
        productsFailCounter = new AtomicInteger(0);

        pageTypeCounter = new ConcurrentHashMap<>();
        emptyImportantData = new ConcurrentHashMap<>();
        emptyNotImportantData = new ConcurrentHashMap<>();

        pageTypeCounter.put(PageType.PRODUCT_PAGE, 0);
        pageTypeCounter.put(PageType.CATEGORY_PAGE, 0);
        pageTypeCounter.put(PageType.CATEGORY_AND_PRODUCT_PAGE, 0);
        pageTypeCounter.put(PageType.UNDEFINED, 0);
    }

    public void addTotalUniqueLinks(int i) {
        if(i >= 0)
            totalUniqueLinks.addAndGet(i);
    }
    public void addTotalSuccessUniqueLinks(int i) {
        if(i >= 0)
            totalSuccessUniqueLinks.addAndGet(i);
    }
    public void addTotalFailUniqueLinks(int i) {
        if(i >= 0)
            totalFailUniqueLinks.addAndGet(i);
    }
    public void addDuplicateLinksCounter(int i) {
        if(i >= 0)
            duplicateLinksCounter.addAndGet(i);
    }
    public void addProductsCounter(int i) {
        if(i >= 0)
            productsCounter.addAndGet(i);
    }
    public void addProductsSuccessCounter(int i) {
        if(i >= 0)
            productsSuccessCounter.addAndGet(i);
    }
    public void addProductsFailCounter(int i) {
        if(i >= 0)
            productsFailCounter.addAndGet(i);
    }
    public void addPageTypeCounter(PageType pageType, int amount) {
        pageTypeCounter.compute(pageType, (k, v) -> v == null ? amount : v+amount);
    }
    public void addEmptyImportantDataCounter(String dataName, int amount) {
        emptyImportantData.compute(dataName, (k, v) -> v == null ? amount : v+amount);
    }
    public void addEmptyNotImportantDataCounter(String dataName, int amount) {
        emptyNotImportantData.compute(dataName, (k, v) -> v == null ? amount : v+amount);
    }

    public void writeDataArrayIntoStatisticDown(Collection<DataArray> dataArraysList) {
        if(dataArraysList != null && dataArraysList.size() > 0) {
            for(DataArray dataArray : dataArraysList) {
                addProductsCounter(1);
                if(dataArray.checkAllNecessaryCells())
                    addProductsSuccessCounter(1);
                else {
                    addProductsFailCounter(1);
                    dataArray.getNamesOfNecessaryEmptyCells()
                            .forEach(name -> addEmptyNotImportantDataCounter(name, 1));
                }
                dataArray.getNamesOfNotNecessaryEmptyCells()
                        .forEach(name -> addEmptyNotImportantDataCounter(name, 1));
            }
        }
    }

    @Override
    public String toString() {
        Date date = new Date();

        StringBuilder result = new StringBuilder("STATISTIC (" + dateFormat.format(date) + "):").append("\n");
        int totalUniqueLinks = this.totalUniqueLinks.get();
        int totalSuccessUniqueLinks = this.totalSuccessUniqueLinks.get();
        int totalFailUniqueLinks = this.totalFailUniqueLinks.get();
        int duplicateLinksCounter = this.duplicateLinksCounter.get();
        int productsCounter = this.productsCounter.get();
        int productsSuccessCounter = this.productsSuccessCounter.get();
        int productsFailCounter = this.productsFailCounter.get();

        result.append("Total unique links: ").append(totalUniqueLinks).append("\n")
                .append("Success unique links: ").append(totalSuccessUniqueLinks).append("\n")
                .append("Fail unique links: ").append(totalFailUniqueLinks).append("\n")
                .append("Duplicates: ").append(duplicateLinksCounter).append("\n")
                .append("Products counter: ").append(productsCounter).append("\n")
                .append("Products success counter: ").append(productsSuccessCounter).append("\n")
                .append("Products fail counter: ").append(productsFailCounter).append("\n");

        if(pageTypeCounter.size() > 0) {
            result.append("\nPage types:");
            for(Map.Entry<PageType, Integer> pageTypeEnter : pageTypeCounter.entrySet())
                result.append("\n\t").append(pageTypeEnter.getKey()).append(" - ").append(pageTypeEnter.getValue());
            result.append("\n");
        }

        if(emptyImportantData.size() > 0) {
            result.append("\nEmpty IMPORTANT fields:");
            for(Map.Entry<String, Integer> data : emptyImportantData.entrySet())
                result.append("\n\t").append(data.getKey()).append(" - ").append(data.getValue());
            result.append("\n");
        }
        if(emptyNotImportantData.size() > 0) {
            result.append("\nEmpty NOT IMPORTANT fields:");
            for(Map.Entry<String, Integer> data : emptyNotImportantData.entrySet())
                result.append("\n\t").append(data.getKey()).append(" - ").append(data.getValue());
            result.append("\n");
        }

        return result.toString();
    }
}
