package com.scraperservice.storage.csv;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;

import java.io.Writer;

/**
 * Возвращает экземпляр класса SimpleCSVWriter с помощью метода getInstance()
 * @author Mihaschenko V.
 */
public class CSVWriterBuilder {
    public static CSVWriter getInstance(Writer writer) {
        return getInstance(writer, ';', '"', "\n");
    }

    public static CSVWriter getInstance(Writer writer, char separator) {
        return getInstance(writer, separator, '"', "\n");
    }

    public static CSVWriter getInstance(Writer writer, char separator, char quotechar, String lineEnd) {
        return (CSVWriter) new com.opencsv.CSVWriterBuilder(writer)
                .withSeparator(separator)
                .withQuoteChar(quotechar)
                .withEscapeChar(ICSVWriter.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(lineEnd)
                .build();
    }

    private CSVWriterBuilder() {}
}
