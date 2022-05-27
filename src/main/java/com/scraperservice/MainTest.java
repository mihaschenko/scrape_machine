package com.scraperservice;

import java.util.ArrayList;
import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(15);
        list.add(10);
        list.add(7);
        multiplyBy10(list);
    }

    private static void multiplyBy10(List<Integer> list) {
        list.forEach(v -> System.out.println(v*10));
    }
}
