package com.scraperservice.utils;

public class RandomStringHelper {
    public static String getRandomStringOnlyLetters(int size) {
        if(size <= 0)
            throw new IllegalArgumentException("int size <= 0");
        char[] charArray = new char[size];
        for(int i = 0; i < charArray.length; i++) {
            double lowerOrUpperCase = Math.random();
            double randomResult;
            if(lowerOrUpperCase >= 0.5)
                randomResult = (Math.random() * ('z' - 'a')) + 'a';
            else
                randomResult = (Math.random() * ('Z' - 'A')) + 'A';
            charArray[i] = (char) Math.round(randomResult);
        }
        return new String(charArray);
    }

    private RandomStringHelper() {}
}
