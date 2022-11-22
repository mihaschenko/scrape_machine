package com.scraperservice.utils;

public class Converter {
    private Converter() {}

    public static String lbsToKg(String lbs) {
        String digitStr = RegexUtil.findText("[0-9,.]+", lbs);
        if(!digitStr.isBlank()) {
            double digit = Double.parseDouble(digitStr);
            return String.format("%.2f kg", lbsToKg(digit));
        }
        return "";
    }

    public static double lbsToKg(double lbs) {
        return lbs * 0.453592;
    }
}
