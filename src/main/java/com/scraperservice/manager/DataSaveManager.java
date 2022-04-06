package com.scraperservice.manager;

import com.scraperservice.scraper.helper.LogHelper;
import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.writer.ScraperDataWriter;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class DataSaveManager implements Closeable {
    private final Set<ScraperDataWriter> dataWriters = new HashSet<>();

    public void addDataWriter(ScraperDataWriter dataWriter) {
        dataWriters.add(dataWriter);
    }

    public void save(List<DataArray> dataArray) throws IOException {
        for(ScraperDataWriter dataWriter : dataWriters)
            dataWriter.writeData(dataArray);
    }

    @Override
    public void close() {
        for(ScraperDataWriter dataWriter : dataWriters) {
            try{ dataWriter.close(); }
            catch (Exception e) {
                LogHelper.getLogger().log(Level.WARNING, "", e);}
        }
    }
}
