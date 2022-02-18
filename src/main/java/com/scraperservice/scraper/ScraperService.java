package com.scraperservice.scraper;

import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.DataCell;
import com.scraperservice.utils.RegexUtils;
import com.scraperservice.utils.ScrapeUtils;
import com.scraperservice.utils.TableUtils;
import com.web.application.entity.Config;
import com.web.application.entity.Run;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mihaschenko V.
 * @see com.scraperservice.scraper.Scraper
 */
public class ScraperService extends Scraper {
    private final String baseSiteUrl;
    private final List<String> startPages = new ArrayList<>();
    private final String categorySelector;
    private final String subcategorySelector;
    private final String productSelector;
    private final String nextPageSelector;
    private final String nextPageGET;
    private String isProductSelector;
    private final List<ProductDataInfo> productDataInfoList = new ArrayList<>();

    public ScraperService(Run run) {
        if(run == null)
            throw new NullPointerException("Run run = null");
        Config config = run.getConfig();

        baseSiteUrl = config.getBaseUrl();
        //startPages.addAll();
        categorySelector = config.getCategorySelector();
        subcategorySelector = config.getSubcategorySelector();
        productSelector = config.getProductSelector();
        nextPageSelector = config.getNextPageSelector();
        nextPageGET = config.getNextPageGet();
        //isProductSelector =
        scrapeProductDataInfo(new JSONArray(config.getProductDataSelectors()));
    }

    private void scrapeProductDataInfo(JSONArray json) {
        for(int i = 1;;i++) {
            JSONObject productDataUnit = json.optJSONObject(i);
            if(productDataUnit != null) {
                ProductDataInfo productDataInfo = new ProductDataInfo();
                productDataInfo.name = productDataUnit.optString("name").trim();
                JSONArray selectors = productDataUnit.optJSONArray("value");
                productDataInfo.selector = selectors != null ? selectors.toList().stream().map(obj -> ((String) obj).trim())
                        .collect(Collectors.toList())
                        : Collections.emptyList();
                productDataInfo.tableVariant = productDataUnit.optString("table-variant").trim();
                productDataInfo.dataType = productDataInfo.tableVariant.equals("n")
                        ? DataType.valueOf(productDataUnit.optString("datatype").trim()) : DataType.tableToJson;
                productDataInfo.dataTypeValue = productDataUnit.optString("attribute").trim();
                productDataInfo.multiply = productDataUnit.optString("multiply").trim().equals("1");
                productDataInfo.important = productDataUnit.optString("required").trim().equals("1");
                productDataInfo.regex = productDataUnit.optString("regex").trim();
                productDataInfoList.add(productDataInfo);
            }
            else
                break;
        }
    }

    @Override
    public List<DataArray> scrapeData(Document document, String productUrl) throws Exception {
        DataArray dataArray = new DataArray(ScrapeUtils.joinBaseUrlAndLink(baseSiteUrl, productUrl));
        for (ProductDataInfo pdi : productDataInfoList) {
            String data = String.join("\n", scrapeData(document, pdi));
            dataArray.add(new DataCell(pdi.name, data, pdi.important));
        }
        return Collections.singletonList(dataArray);
    }

    @Override
    public PageType getPageType(Document document) throws Exception {
        String notProductSelector = String.join(",", categorySelector, subcategorySelector, productSelector);
        boolean isProductPage = document.selectFirst(isProductSelector) != null;
        boolean isNotProductPage = document.selectFirst(notProductSelector) != null;
        if(isProductPage && isNotProductPage)
            return PageType.CATEGORY_AND_PRODUCT_PAGE;
        else if(isProductPage)
            return PageType.PRODUCT_PAGE;
        else if(isNotProductPage)
            return PageType.CATEGORY_PAGE;
        else
            return PageType.UNDEFINED;
    }

    @Override
    public List<String> getStartLinks() throws Exception {
        return startPages.size() == 0 ? null : startPages;
    }

    @Override
    public List<String> scrapeCategories(Document document) throws Exception {
        return ScrapeUtils.getAttributes(document, categorySelector, "href")
                .stream().map(link -> ScrapeUtils.joinBaseUrlAndLink(baseSiteUrl, link))
                .toList();
    }

    @Override
    public List<String> scrapeSubCategories(Document document) throws Exception {
        return ScrapeUtils.getAttributes(document, subcategorySelector, "href")
                .stream().map(link -> ScrapeUtils.joinBaseUrlAndLink(baseSiteUrl, link))
                .toList();
    }

    @Override
    public List<String> scrapeLinksToProductPages(Document document) throws Exception {
        return ScrapeUtils.getAttributes(document, productSelector, "href")
                .stream().map(link -> ScrapeUtils.joinBaseUrlAndLink(baseSiteUrl, link))
                .toList();
    }

    @Override
    public String goToNextPage(Document document) throws Exception {
        return ScrapeUtils.joinBaseUrlAndLink(baseSiteUrl,
                ScrapeUtils.getAttribute(document, nextPageSelector, "href"));
    }

    @Override
    public String goToNextPage(String categoryPage) {
        if(nextPageGET != null && !nextPageGET.isEmpty()) {
            String pageCounter = RegexUtils.findText("(?<=" + nextPageGET + "=)[0-9]+", categoryPage);
            if(!pageCounter.isEmpty()) {
                int page = Integer.parseInt(pageCounter);
                page++;
                categoryPage = categoryPage.replaceFirst("(?<=" + nextPageGET + "=)[0-9]+", Integer.toString(page));
                return categoryPage;
            }
        }
        return null;
    }

    private List<String> scrapeData(Document document, ProductDataInfo productDataInfo) {
        List<String> selectors = productDataInfo.selector;
        DataType dataType = productDataInfo.dataType;
        String dataTypeValue = productDataInfo.dataTypeValue;
        boolean multiply = productDataInfo.multiply;

        List<String> result = new ArrayList<>();
        for(String selector : selectors) {
            if(dataType == DataType.text) {
                if (multiply)
                    result.addAll(ScrapeUtils.getTexts(document, selector));
                else
                    result.add(ScrapeUtils.getText(document, selector));
            }
            else if(dataType == DataType.attribute) {
                if(dataTypeValue.equals("src") || dataTypeValue.equals("href"))
                    dataTypeValue = "abs:" + dataTypeValue;
                if (multiply)
                    result.addAll(ScrapeUtils.getAttributes(document, selector, dataTypeValue));
                else
                    result.add(ScrapeUtils.getAttribute(document, selector, dataTypeValue));
            }
            else if(dataType == DataType.innerHTML) {
                if (multiply)
                    result.addAll(ScrapeUtils.getInnerHTMLs(document, selector));
                else
                    result.add(ScrapeUtils.getInnerHTML(document, selector));
            }
            else if(dataType == DataType.outerHTML) {
                if (multiply)
                    result.addAll(ScrapeUtils.getOuterHTMLs(document, selector));
                else
                    result.add(ScrapeUtils.getOuterHTML(document, selector));
            }
            else if(dataType == DataType.tableToJson) {
                String tableVariant = productDataInfo.tableVariant;
                Elements tables = document.select(selector);
                if(tables.size() > 0) {
                    if(tableVariant.equals("t")) {
                        if(multiply)
                            result.addAll(Arrays.asList(TableUtils.topHeadTables(tables)));
                        else
                            result.add(TableUtils.topHeadTable(tables.get(0)));
                    }
                    else if(tableVariant.equals("l")) {
                        if(multiply)
                            result.addAll(Arrays.asList(TableUtils.leftHeadTables(tables)));
                        else
                            result.add(TableUtils.leftHeadTable(tables.get(0)));
                    }
                    else if(tableVariant.equals("Tl")) {
                        if(multiply)
                            result.addAll(Arrays.asList(TableUtils.topLeftHeadTables(tables, true)));
                        else
                            result.add(TableUtils.topLeftHeadTable(tables.get(0), true));
                    }
                    else if(tableVariant.equals("tL")) {
                        if(multiply)
                            result.addAll(Arrays.asList(TableUtils.topLeftHeadTables(tables, false)));
                        else
                            result.add(TableUtils.topLeftHeadTable(tables.get(0), false));
                    }
                    /*else if(tableVariant.equals("Tl_P")) {
                        //
                    }
                    else if(tableVariant.equals("tL_P")) {
                        //
                    }*/
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "ScraperService{" +
                "baseSiteUrl='" + baseSiteUrl + '\'' +
                ", startPages=" + startPages +
                ", categorySelector='" + categorySelector + '\'' +
                ", subcategorySelector='" + subcategorySelector + '\'' +
                ", productSelector='" + productSelector + '\'' +
                ", nextPageSelector='" + nextPageSelector + '\'' +
                ", nextPageGET='" + nextPageGET + '\'' +
                ", isProductSelector='" + isProductSelector + '\'' +
                ", productDataInfoList=" + productDataInfoList +
                '}';
    }

    private static class ProductDataInfo {
        public String name;
        public List<String> selector;
        public DataType dataType;
        public String dataTypeValue;
        public boolean multiply ;
        public boolean important;
        public String regex = "";
        public String tableVariant = "";
    }

    /**
     * Перечисление типов данных. Указывает скрейперу, какую информацию
     * сохранить
     */
    private enum DataType {
        text,
        innerHTML,
        outerHTML,
        attribute,
        tableToJson
    }
}