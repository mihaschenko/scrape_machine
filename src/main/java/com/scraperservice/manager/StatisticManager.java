package com.scraperservice.manager;

import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class StatisticManager {
    private static final StatisticManager statisticManager = new StatisticManager();

    public static StatisticManager getInstance() { return statisticManager; }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");

    private final AtomicInteger linkAmountCounter;
    private final AtomicInteger productSuccessCounter;
    private final AtomicInteger productFailCounter;
    private final AtomicInteger linkDuplicatesCounter;

    private final ConcurrentHashMap<PageType, Integer> pageTypeCounter;
    private final ConcurrentHashMap<String, Integer> emptyImportantData;
    private final ConcurrentHashMap<String, Integer> emptyNotImportantData;

    public int getLinkAmountCounter() {return linkAmountCounter.get();}
    public int getProductSuccessCounter() {return productSuccessCounter.get();}
    public int getProductFailCounter() {return productFailCounter.get();}
    public int getLinkDuplicatesCounter() {return linkDuplicatesCounter.get();}
    public Map<PageType, Integer> getPageTypeCounter() {return Collections.unmodifiableMap(pageTypeCounter);}
    public Map<String, Integer> getEmptyImportantData() {return Collections.unmodifiableMap(emptyImportantData);}
    public Map<String, Integer> getEmptyNotImportantData() {return Collections.unmodifiableMap(emptyNotImportantData);}

    private StatisticManager() {
        linkAmountCounter = new AtomicInteger(0);
        productSuccessCounter = new AtomicInteger(0);
        productFailCounter = new AtomicInteger(0);
        linkDuplicatesCounter = new AtomicInteger(0);

        pageTypeCounter = new ConcurrentHashMap<>();
        emptyImportantData = new ConcurrentHashMap<>();
        emptyNotImportantData = new ConcurrentHashMap<>();

        pageTypeCounter.put(PageType.PRODUCT_PAGE, 0);
        pageTypeCounter.put(PageType.CATEGORY_PAGE, 0);
        pageTypeCounter.put(PageType.CATEGORY_AND_PRODUCT_PAGE, 0);
        pageTypeCounter.put(PageType.UNDEFINED, 0);
    }

    public void addLinkAmountCounter(int i) {
        if(i >= 0)
            linkAmountCounter.addAndGet(i);
    }
    public void addProductSuccessCounter(int i) {
        if(i >= 0)
            productSuccessCounter.addAndGet(i);
    }
    public void addProductFailCounter(int i) {
        if(i >= 0)
            productFailCounter.addAndGet(i);
    }
    public void addLinkDuplicatesCounter(int i) {
        if(i >= 0)
            linkDuplicatesCounter.addAndGet(i);
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

    public void recordProductDataStatistic(Collection<DataArray> dataArraysList) {
        if(dataArraysList != null && dataArraysList.size() > 0) {
            for(DataArray dataArray : dataArraysList) {
                if(dataArray.checkAllNecessaryCells())
                    addProductSuccessCounter(1);
                else {
                    addProductFailCounter(1);
                    dataArray.getNamesOfNecessaryEmptyCells()
                            .forEach(name -> addEmptyNotImportantDataCounter(name, 1));
                }
                dataArray.getNamesOfNotNecessaryEmptyCells()
                        .forEach(name -> addEmptyNotImportantDataCounter(name, 1));
            }
        }
        else
            addProductFailCounter(1);
    }

    @Override
    public String toString() {
        Date date = new Date();

        StringBuilder result = new StringBuilder("STATISTIC (" + dateFormat.format(date) + "):").append("\n");
        int linkAmountCounter = this.linkAmountCounter.get();
        int productFailCounter = this.productFailCounter.get();
        int productSuccessCounter = this.productSuccessCounter.get();
        int linkDuplicatesCounter = this.linkDuplicatesCounter.get();
        result.append("Total links: ").append(linkAmountCounter).append("\n")
                .append("Success products: ").append(productSuccessCounter).append("\n")
                .append("Fail products: ").append(productFailCounter).append("\n");

        if(linkDuplicatesCounter > 0)
            result.append("\nDuplicates: ").append(linkDuplicatesCounter).append("\n");

        if(pageTypeCounter.size() > 0) {
            result.append("\nPage types:");
            for(Map.Entry<PageType, Integer> pageTypeEnter : pageTypeCounter.entrySet())
                result.append("\n\t").append(pageTypeEnter.getKey()).append(" - ").append(pageTypeEnter.getValue());
            result.append("\n");
        }

        if(emptyImportantData.size() > 0) {
            result.append("\nEmpty IMPORTANT fields (% of unsuccess):");
            for(Map.Entry<String, Integer> data : emptyImportantData.entrySet())
                result.append("\n\t").append(data.getKey()).append(" - ").append(data.getValue());
            result.append("\n");
        }
        if(emptyNotImportantData.size() > 0) {
            result.append("\nEmpty NOT IMPORTANT fields (% of success):");
            for(Map.Entry<String, Integer> data : emptyNotImportantData.entrySet())
                result.append("\n\t").append(data.getKey()).append(" - ").append(data.getValue());
            result.append("\n");
        }

        return result.toString();
    }
}
