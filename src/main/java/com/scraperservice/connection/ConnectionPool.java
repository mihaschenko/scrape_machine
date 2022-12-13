package com.scraperservice.connection;

import com.scraperservice.helper.LogHelper;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

public class ConnectionPool implements ObjectPool<Connection<?>>, AutoCloseable {
    private final ArrayBlockingQueue<Connection<?>> pool;

    public ConnectionPool(ArrayBlockingQueue<Connection<?>> pool) {
        this.pool = pool;
    }

    @Override
    public Connection<?> acquire() throws InterruptedException {
        return pool.take();
    }

    @Override
    public void release(Connection<?> object) throws InterruptedException {
        pool.put(object);
    }

    @Override
    public void close() {
        for(Connection<?> connection : pool) {
            try{
                connection.close();
            }
            catch (Exception e) {
                LogHelper.getLogger().log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}
