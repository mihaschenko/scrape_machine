package com.scraperservice.queue;

import com.scraperservice.helper.LogHelper;
import org.sqlite.SQLiteException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class ConcurrentLinkedQueueUnique implements AutoCloseable {
    private final UniqueValuesStorage uniqueValuesStorage;
    private final AtomicInteger size = new AtomicInteger(1);
    private final AtomicInteger currentPosition = new AtomicInteger(1);

    public ConcurrentLinkedQueueUnique() throws SQLException {
        uniqueValuesStorage = new UniqueValuesStorage();
    }

    public synchronized int size() {
        return size.get() - currentPosition.get();
    }

    public boolean addAll(Collection<? extends String> c) {
        boolean result = false;
        for(String str : c) {
            if(add(str))
                result = true;
        }
        return result;
    }

    public synchronized boolean add(String s) {
        boolean result = false;
        try{
            result = uniqueValuesStorage.add(size.get(), s);
        }
        catch (SQLException e) { /*LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);*/ }
        if(result)
            size.incrementAndGet();
        return result;
    }

    public synchronized String poll() {
        if(size.get() > currentPosition.get()) {
            try{
                return uniqueValuesStorage.get(currentPosition.getAndIncrement()).intern();
            }
            catch (SQLException e) { LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e); }
        }
        return null;
    }

    @Override
    public void close() {
        uniqueValuesStorage.close();
    }
}
