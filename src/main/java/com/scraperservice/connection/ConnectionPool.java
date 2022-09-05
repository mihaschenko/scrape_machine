package com.scraperservice.connection;

import com.scraperservice.helper.LogHelper;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

public class ConnectionPool implements ObjectPool<Connection>, AutoCloseable {
    private final ArrayBlockingQueue<Connection> pool;

    public ConnectionPool(int poolSize, Class<? extends Connection> connectionClass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        if(poolSize < 0)
            throw new IllegalArgumentException("int poolSize < 0");

        pool = new ArrayBlockingQueue<>(poolSize);
        pool.addAll(ConnectionBuilder.build(connectionClass, poolSize));
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
