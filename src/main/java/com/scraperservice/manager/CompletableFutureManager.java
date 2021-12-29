package com.scraperservice.manager;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CompletableFutureManager<T> implements Runnable {
    private final Queue<CompletableFuture<T>> completableFutureManagerQueue = new ConcurrentLinkedQueue<>();

    public boolean add(CompletableFuture<T> completableFuture) {
        return completableFutureManagerQueue.add(completableFuture);
    }

    public long getAmountCompletableFutureIsWork() {
        return completableFutureManagerQueue.stream()
                .filter(completableFuture -> !completableFuture.isDone()).count();
    }

    @Override
    public void run() {
        while(Thread.currentThread().isInterrupted()) {
            completableFutureManagerQueue.removeIf(CompletableFuture::isDone);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
