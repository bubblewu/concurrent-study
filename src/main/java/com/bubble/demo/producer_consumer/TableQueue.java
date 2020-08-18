package com.bubble.demo.producer_consumer;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 基于juc的队列来实现Table
 *
 * @author wugang
 * date: 2020-07-17 17:57
 **/
public class TableQueue extends ArrayBlockingQueue<String> {
    public TableQueue(int capacity) {
        super(capacity);
    }

    @Override
    public void put(String cake) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " put: " + cake);
        super.put(cake);
    }

    @Override
    public String take() throws InterruptedException {
        String cake = super.take();
        System.out.println(Thread.currentThread().getName() + " take: " + cake);
        return cake;
    }

}
