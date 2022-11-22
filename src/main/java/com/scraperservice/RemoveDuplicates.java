package com.scraperservice;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.scraperservice.storage.csv.CSVReaderBuilder;
import com.scraperservice.storage.csv.CSVWriterBuilder;
import com.scraperservice.utils.RegexUtil;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveDuplicates {
    private RemoveDuplicates() {}

    public static void main(String[] args) throws Exception {
        removeDuplicatesInCSVFile("09112022_PlumbersStockScraper_17_29.csv", "10112022_PlumbersStockScraper.csv");
    }

    public static void removeDuplicatesInCSVFile(String fileNameWithData, String newFileName) throws IOException, CsvValidationException {
        String[] str = null;
        try(CSVReader csvReader = CSVReaderBuilder.getInstance(new FileReader(fileNameWithData));
            CSVWriter csvWriter = CSVWriterBuilder.getInstance(new FileWriter(newFileName))) {
            boolean isFirstRow = true;
            while((str = csvReader.readNext()) != null) {
                List<String> row = Arrays.stream(str).collect(Collectors.toList());
                if(isFirstRow) {
                    isFirstRow = false;
                    row.add(3, "mpn#");
                }
                else {
                    row.add(3, RegexUtil.findText("\"Mpn #\": \"([^\"]+)", row.get(7), 1).trim());
                }
                str = row.toArray(new String[0]);
                csvWriter.writeNext(str);
            }
        }
        catch (Exception e) {
            System.out.println(Arrays.toString(str));
            throw e;
        }
    }
}
