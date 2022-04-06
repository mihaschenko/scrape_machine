package com.scraperservice.storage.writer;

import com.scraperservice.storage.DataArray;

import java.io.IOException;
import java.util.List;

public interface ScraperDataWriter extends AutoCloseable {
    boolean writeData(List<DataArray> dataArrayList) throws IOException;
}
