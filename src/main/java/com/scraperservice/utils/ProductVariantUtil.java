package com.scraperservice.utils;

import lombok.Data;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductVariantUtil {
    private ProductVariantUtil() {}

    /**
     * @param valueCssSelector is keyCssSelector's child
     */
    public static List<KeyAndValue> scrapeVariantKeyAndValue(Document document, String keyCssSelector, String keyAttribute,
                                                               String valueCssSelector, String valueAttribute) {
        List<KeyAndValue> result = new ArrayList<>();
        Elements parents = document.select(keyCssSelector);
        if(parents.size() > 0) {
            for(Element parent : parents) {
                String key = parent.attr(keyAttribute);
                List<String> values = ScrapeUtil.getAttributes(parent, valueCssSelector, valueAttribute);
                if(!key.isEmpty() && values.size() > 0)
                    result.add(new KeyAndValue(key, values));
            }
        }
        return result;
    }

    public static List<Map<String, String>> createRequestsParameters(List<KeyAndValue> params) {
        List<Map<String, String>> result = new ArrayList<>();

        int[] matrixTemplate = new int[params.size()];
        for(int i = 0; i < params.size(); i++)
            matrixTemplate[i] = params.get(i).values.size();
        int[][] matrix = MatrixUtil.createVariantMatrix(matrixTemplate);
        do{
            Map<String, String> variant = new HashMap<>();
            for(int i = 0; i < matrix.length; i++) {
                int cellIndex = MatrixUtil.getCellIndexInRow(matrix, i);
                KeyAndValue keyAndValue = params.get(i);
                variant.put(keyAndValue.key, keyAndValue.values.get(cellIndex));
            }
            result.add(variant);
        }
        while (MatrixUtil.iterateMatrix(matrix));
        return result;
    }

    public static List<String> createGetRequests(String url, List<Map<String, String>> parameters) {
        List<String> result = new ArrayList<>();
        parameters.forEach(parameterMap -> {
            List<String> keyAndValue = new ArrayList<>();
            parameterMap.forEach((key, value) -> keyAndValue.add(String.join("=", key, value)));
            result.add(url + "?" + String.join("&", keyAndValue));
        });
        return result;
    }

    @Data
    public static class KeyAndValue {
        private String key;
        private List<String> values;

        public KeyAndValue(String key, List<String> values) {
            this.key = key;
            this.values = values;
        }
    }
}