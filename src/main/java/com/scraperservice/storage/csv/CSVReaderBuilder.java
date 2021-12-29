package com.scraperservice.storage.csv;

import com.opencsv.*;

import java.io.Reader;

/**
 * Возвращает экземпляр класса CSVReader с помощью метода getInstance()
 * @author Mihaschenko V.
 */
public class CSVReaderBuilder {
    public static CSVReader getInstance(Reader reader) {
        return getInstance(reader, ';', false);
    }

    public static CSVReader getInstance(Reader reader, char separator) {
        return getInstance(reader, separator, false);
    }

    public static CSVReader getInstance(Reader reader, boolean skipHeader) {
        return getInstance(reader, ';', skipHeader);
    }

    public static CSVReader getInstance(Reader reader, char separator, boolean skipHeader) {
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(separator)
                .build();

        return new com.opencsv.CSVReaderBuilder(reader)
                .withCSVParser(parser)
                .withSkipLines(skipHeader ? 1 : 0)
                .build();
    }

    private CSVReaderBuilder() {}
}
