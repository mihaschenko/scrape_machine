package com.scraperservice.manager;

import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class CompletableFutureManager<T> {
    private final Queue<CompletableFuture<T>> completableFutureManagerQueue = new ConcurrentLinkedQueue<>();
    private final Lock queueLock = new ReentrantLock();

    public boolean add(CompletableFuture<T> completableFuture) {
        clearQueue();
        return completableFutureManagerQueue.add(completableFuture);
    }

    public long getAmountCompletableFutureIsWorking() {
        clearQueue();
        return completableFutureManagerQueue.stream()
                .filter(completableFuture -> !completableFuture.isDone()).count();
    }

    private void clearQueue() {
        if(queueLock.tryLock()) {
            completableFutureManagerQueue.removeIf(CompletableFuture::isDone);
            queueLock.unlock();
        }
    }
}
