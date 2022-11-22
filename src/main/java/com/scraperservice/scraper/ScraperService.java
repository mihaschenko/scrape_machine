package com.scraperservice.scraper;

import com.scraperservice.scraper.page.PageData;
import com.scraperservice.scraper.page.PageType;
import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.DataCell;
import com.scraperservice.utils.RegexUtil;
import com.scraperservice.utils.ScrapeUtil;
import com.scraperservice.utils.TableUtil;

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
    private String baseSiteUrl;
    private final List<String> startPages = new ArrayList<>();
    private String categorySelector;
    private String subcategorySelector;
    private String productSelector;
    private String nextPageSelector;
    private String nextPageGET;
    private String isProductSelector;
    private final List<ProductDataInfo> productDataInfoList = new ArrayList<>();

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
    public List<DataArray> scrapeData(PageData pageData) {
        Document document = pageData.getHtml();
        DataArray dataArray = new DataArray(ScrapeUtil.joinBaseUrlAndLink(baseSiteUrl, pageData.getUrl()));
        for (ProductDataInfo pdi : productDataInfoList) {
            String data = String.join("\n", scrapeData(document, pdi));
            dataArray.add(new DataCell(pdi.name, data, pdi.important));
        }
        return Collections.singletonList(dataArray);
    }

    @Override
    public PageType getPageType(PageData pageData) {
        Document document = pageData.getHtml();
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
    public List<String> getStartLinks() {
        return startPages.size() == 0 ? null : startPages;
    }

    @Override
    public List<String> scrapeCategories(PageData pageData) {
        Document document = pageData.getHtml();
        return ScrapeUtil.getAttributes(document, categorySelector, "href")
                .stream().map(link -> ScrapeUtil.joinBaseUrlAndLink(baseSiteUrl, link))
                .toList();
    }

    @Override
    public List<String> scrapeSubCategories(PageData pageData) {
        Document document = pageData.getHtml();
        return ScrapeUtil.getAttributes(document, subcategorySelector, "href")
                .stream().map(link -> ScrapeUtil.joinBaseUrlAndLink(baseSiteUrl, link))
                .toList();
    }

    @Override
    public List<String> scrapeLinksToProductPages(PageData pageData) {
        Document document = pageData.getHtml();
        return ScrapeUtil.getAttributes(document, productSelector, "href")
                .stream().map(link -> ScrapeUtil.joinBaseUrlAndLink(baseSiteUrl, link))
                .toList();
    }

    @Override
    public String goToNextPage(PageData pageData) {
        if(nextPageGET != null && !nextPageGET.isEmpty()) {
            String categoryPage = pageData.getUrl();
            String pageCounter = RegexUtil.findText("(?<=" + nextPageGET + "=)[0-9]+", categoryPage);
            if(!pageCounter.isEmpty()) {
                int page = Integer.parseInt(pageCounter);
                page++;
                categoryPage = categoryPage.replaceFirst("(?<=" + nextPageGET + "=)[0-9]+", Integer.toString(page));
                return categoryPage;
            }
        }
        Document document = pageData.getHtml();
        return ScrapeUtil.joinBaseUrlAndLink(baseSiteUrl,
                ScrapeUtil.getAttribute(document, nextPageSelector, "href"));
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
                    result.addAll(ScrapeUtil.getTexts(document, selector));
                else
                    result.add(ScrapeUtil.getText(document, selector));
            }
            else if(dataType == DataType.attribute) {
                if(dataTypeValue.equals("src") || dataTypeValue.equals("href"))
                    dataTypeValue = "abs:" + dataTypeValue;
                if (multiply)
                    result.addAll(ScrapeUtil.getAttributes(document, selector, dataTypeValue));
                else
                    result.add(ScrapeUtil.getAttribute(document, selector, dataTypeValue));
            }
            else if(dataType == DataType.innerHTML) {
                if (multiply)
                    result.addAll(ScrapeUtil.getInnerHTMLs(document, selector));
                else
                    result.add(ScrapeUtil.getInnerHTML(document, selector));
            }
            else if(dataType == DataType.outerHTML) {
                if (multiply)
                    result.addAll(ScrapeUtil.getOuterHTMLs(document, selector));
                else
                    result.add(ScrapeUtil.getOuterHTML(document, selector));
            }
            else if(dataType == DataType.tableToJson) {
                String tableVariant = productDataInfo.tableVariant;
                Elements tables = document.select(selector);
                if(tables.size() > 0) {
                    if(tableVariant.equals("t")) {
                        if(multiply)
                            result.addAll(Arrays.asList(TableUtil.topHeadTables(tables)));
                        else
                            result.add(TableUtil.topHeadTable(tables.get(0)));
                    }
                    else if(tableVariant.equals("l")) {
                        if(multiply)
                            result.addAll(Arrays.asList(TableUtil.leftHeadTables(tables)));
                        else
                            result.add(TableUtil.leftHeadTable(tables.get(0)));
                    }
                    else if(tableVariant.equals("Tl")) {
                        if(multiply)
                            result.addAll(Arrays.asList(TableUtil.topLeftHeadTables(tables, true)));
                        else
                            result.add(TableUtil.topLeftHeadTable(tables.get(0), true));
                    }
                    else if(tableVariant.equals("tL")) {
                        if(multiply)
                            result.addAll(Arrays.asList(TableUtil.topLeftHeadTables(tables, false)));
                        else
                            result.add(TableUtil.topLeftHeadTable(tables.get(0), false));
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
