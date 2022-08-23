package com.scraperservice;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.scraperservice.storage.csv.CSVReaderBuilder;
import com.scraperservice.storage.csv.CSVWriterBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RemoveDuplicates {
    private RemoveDuplicates() {}

    public static void main(String[] args) throws Exception {
        removeDuplicatesInCSVFile("PrimebuyScraper_09_47_06_08_2022.csv", "PrimebuyScraper.csv");
    }

    public static void removeDuplicatesInCSVFile(String fileNameWithData, String newFileName) throws IOException, CsvValidationException {
        try(CSVReader csvReader = CSVReaderBuilder.getInstance(new FileReader(fileNameWithData));
            CSVWriter csvWriter = CSVWriterBuilder.getInstance(new FileWriter(newFileName))) {
            String[] str;
            List<String> uniqueValues = new ArrayList<>();
            while((str = csvReader.readNext()) != null) {
                if(str.length > 0 && !uniqueValues.contains(str[0])) {
                    uniqueValues.add(str[0]);
                    csvWriter.writeNext(str);
                }
            }
        }
    }
}
