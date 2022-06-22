package com.scraperservice.queue;

import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentLinkedQueueUnique implements AutoCloseable {
    private final UniqueValuesStorage uniqueValuesStorage;
    private final AtomicInteger size = new AtomicInteger(0);
    private final AtomicInteger currentPosition = new AtomicInteger(0);

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
            catch (SQLException e) { /*LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);*/ }
        }
        return null;
    }

    @Override
    public void close() {
        uniqueValuesStorage.close();
    }
}
