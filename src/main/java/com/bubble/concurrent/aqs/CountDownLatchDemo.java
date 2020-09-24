package com.bubble.concurrent.aqs;

import com.bubble.common.pool.NamedThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wugang
 * date: 2020-09-24 17:51
 **/
public class CountDownLatchDemo {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 1L;
    private static final int QUEUE_SIZE = 1;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(CORE_POOL_SIZE);
        Random random = new Random(2020);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_SIZE),
                new NamedThreadFactory("OrderConsumer"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            executor.execute(new Worker(countDownLatch, random));
        }

        countDownLatch.await();
        executor.shutdown();
        System.out.println("done");
    }

    static class Worker implements Runnable {
        private CountDownLatch countDownLatch;
        private Random random;

        public Worker(CountDownLatch countDownLatch, Random random) {
            this.countDownLatch = countDownLatch;
            this.random = random;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(random.nextInt(3000));
                System.out.printf("do: %s%n", Thread.currentThread().getName());
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }

}
