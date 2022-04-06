package com.scraperservice.connection;

public interface ObjectPool<T> {
    T acquire() throws InterruptedException;
    void release(T object) throws InterruptedException;
}
