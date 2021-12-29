package com.scraperservice.queue;

import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueUnique extends ConcurrentLinkedQueue<String> implements AutoCloseable {
    private final UniqueValuesStorage uniqueValuesStorage;

    public ConcurrentLinkedQueueUnique() throws SQLException {
        uniqueValuesStorage = new UniqueValuesStorage();
    }

    @Override
    public boolean add(String value) {
        return uniqueValuesStorage.checkUniqueValue(value) && super.add(value);
    }

    @Override
    public boolean addAll(Collection<? extends String> values) {
        boolean result = false;
        for(String value : values) {
            if(add(value))
                result = true;
        }
        return result;
    }

    public int addUnique(String value) {
        return add(value) ? 0 : 1;
    }

    public int addAllUnique(Collection<? extends String> values) {
        int result = 0;
        for(String value : values) {
            if(!add(value))
                result++;
        }
        return result;
    }

    @Override
    public void close() {
        uniqueValuesStorage.close();
    }
}
