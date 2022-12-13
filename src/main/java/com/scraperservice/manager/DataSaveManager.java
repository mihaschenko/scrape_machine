package com.scraperservice.manager;

import com.scraperservice.helper.LogHelper;
import com.scraperservice.storage.DataArray;
import com.scraperservice.storage.writer.ScraperDataWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@Component
public class DataSaveManager implements Closeable {
    private final Set<ScraperDataWriter> dataWriters = new HashSet<>();

    @Autowired
    @Qualifier("csvDataWriter")
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
