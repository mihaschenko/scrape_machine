package com.scraperservice.connection.pool;

import com.scraperservice.connection.Connection;
import com.scraperservice.connection.ConnectionBuilder;
import com.scraperservice.scraper.helper.LogHelper;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

public class ConnectionPool implements ObjectPool<Connection>, Closeable {
    private final ArrayBlockingQueue<Connection> pool;

    public ConnectionPool(int poolSize, Class<? extends Connection> connectionClass, Object[] parameters)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if(poolSize < 0)
            throw new IllegalArgumentException("int poolSize < 0");

        pool = new ArrayBlockingQueue<>(poolSize);
        pool.addAll(ConnectionBuilder.build(connectionClass, parameters, poolSize));
    }

    @Override
    public Connection acquire() throws InterruptedException {
        return pool.take();
    }

    @Override
    public void release(Connection object) throws InterruptedException {
        pool.put(object);
    }

    @Override
    public void close() {
        for(Connection connection : pool) {
            try{
                connection.close();
            }
            catch (Exception e) {
                LogHelper.getLogger().log(Level.WARNING, e.getMessage(), e);
            }
        }
    }
}
