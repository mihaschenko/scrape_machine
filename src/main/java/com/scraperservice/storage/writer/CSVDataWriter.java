package com.scraperservice.storage.writer;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.csv.CSVReaderBuilder;
import com.scraperservice.storage.csv.CSVWriterBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CSVDataWriter implements ScraperDataWriter {
    private final int CSV_LINE_MAX;
    private final String FILE_SUFFIX;

    private CSVWriter writer;
    private int csvWriteCounter = 0;
    private final List<File> tempFiles = new ArrayList<>();
    private File finalFile = null;

    public CSVDataWriter(String fileSuffix) throws IOException {
        this(fileSuffix, 10000);
    }

    public CSVDataWriter(String fileSuffix, int tempFileRecordLimit) throws IOException {
        if(fileSuffix == null)
            throw new NullPointerException("String fileSuffix is null");
        if(tempFileRecordLimit <= 0)
            throw new IllegalArgumentException("int tempFileRecordLimit is below or equals zero");
        FILE_SUFFIX = fileSuffix;
        CSV_LINE_MAX = tempFileRecordLimit;

        updateTempFile();
    }

    private File createNewTempFile() throws IOException {
        return Files.createTempFile(Paths.get("temp"), FILE_SUFFIX, ".csv").toFile();
    }

    private void updateTempFile() throws IOException {
        File tempFile = createNewTempFile();
        tempFiles.add(tempFile);
        writer = CSVWriterBuilder.getInstance(new FileWriter(tempFile, StandardCharsets.UTF_8));
    }

    @Override
    public synchronized boolean writeData(List<DataArray> dataArray) throws IOException {
        if(dataArray == null)
            return false;
        if(csvWriteCounter == 0)
            writer.writeNext(dataArray.get(0).getTitleArray());
        for(DataArray da : dataArray) {
            writer.writeNext(da.toValueArray());
            csvWriteCounter++;
        }

        if(csvWriteCounter >= CSV_LINE_MAX) {
            writer.close();

            updateTempFile();
            csvWriteCounter = 0;
        }
        writer.flush();
        return true;
    }

    public File getFinalFile() {
        return finalFile;
    }

    @Override
    public synchronized void close() throws Exception {
        writer.close();
        mergeAllTempFiles();
    }

    private void mergeAllTempFiles() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH_mm_dd_MM_yyyy");
        File finalFile = new File("results/" + FILE_SUFFIX + "_" + dateFormat.format(new Date()) + ".csv");
        this.finalFile = finalFile;
        boolean isHeadExist = false;
        try(CSVWriter csvWriter = CSVWriterBuilder.getInstance(
                new FileWriter(finalFile))) {
            for(File file : tempFiles) {
                try(CSVReader csvReader = CSVReaderBuilder.getInstance(new FileReader(file), ';', isHeadExist)) {
                    isHeadExist = true;
                    String[] line;
                    while((line = csvReader.readNext()) != null)
                        csvWriter.writeNext(line);
                    file.deleteOnExit();
                }
            }
        }
    }
}
