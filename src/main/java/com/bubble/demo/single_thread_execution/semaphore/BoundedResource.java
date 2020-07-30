package com.bubble.demo.single_thread_execution.semaphore;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 使用资源
 *
 * @author wugang
 * date: 2020-07-30 10:24
 **/
public class BoundedResource {
    private final Semaphore semaphore;
    private final int permits;
    private final static Random random = new Random(2020);

    public BoundedResource(int permits) {
        this.semaphore = new Semaphore(permits);
        this.permits = permits;
    }

    public void use() throws InterruptedException {
        semaphore.acquire();
        try {
            doSomething();
        } finally {
            semaphore.release();
        }

    }

    private void doSomething() throws InterruptedException {
        Log.println("-> Begin: used = NO." + (this.permits - this.semaphore.availablePermits()));
        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
        Log.println("<--- End: used = NO." + (this.permits - this.semaphore.availablePermits()));
    }

}
