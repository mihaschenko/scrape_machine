package com.scraperservice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TwoFilesMerge {
    public static void main(String[] args) throws Exception {
        Map<String, String> countryCodeMap = new HashMap<>();
        List<String> cityList = new ArrayList<>();
        try(BufferedReader countryCode = new BufferedReader(new FileReader("country-code-&-cities.txt"));
            BufferedReader cities = new BufferedReader(new FileReader("TB-cities.txt"))) {
            String str;
            while((str = countryCode.readLine()) != null) {
                String[] line = str.split(";");
                if(line.length == 2)
                    countryCodeMap.put(line[1].trim().toLowerCase(Locale.ROOT), line[0].trim().toLowerCase(Locale.ROOT));
            }
            while((str = cities.readLine()) != null)
                cityList.add(str.toLowerCase(Locale.ROOT));
        }
        List<String> allRequestList = new ArrayList<>();
        for(String city : cityList)
            allRequestList.add("https://ra.co/clubs/" + countryCodeMap.get(city) + "/" + city);

        Files.write(Paths.get("requestList.txt"), allRequestList);
    }
}
