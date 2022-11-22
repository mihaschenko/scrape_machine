package com.scraperservice.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    public static String replaceAll(String regex, String text, int group) {
        final String subst = "$" + group;

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(subst);
    }

    public static String findText(String regex, String text) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if(matcher.find())
            return matcher.group();
        return "";
    }

    public static String findText(String regex, String text, int group) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if(matcher.find())
            return matcher.group(group);
        return "";
    }

    public static List<String> findTexts(String regex, String text) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        List<String> result = new ArrayList<>();
        while(matcher.find())
            result.add(matcher.group());
        return result;
    }

    public static List<String> findTexts(String regex, String text, int group) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        List<String> result = new ArrayList<>();
        while(matcher.find())
            result.add(matcher.group(group));
        return result;
    }
}
