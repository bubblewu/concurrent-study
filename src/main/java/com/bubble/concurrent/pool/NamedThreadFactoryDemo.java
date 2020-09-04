package com.bubble.concurrent.pool;

import com.bubble.common.pool.NamedThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 当在一个应用中需要创建多个线程或者线程池时最好给每个线程或者线程池根据业务类型设置具体的名称，
 * 以便在出现问题时方便进行定位
 *
 * @author wugang
 * date: 2020-09-03 19:03
 **/
public class NamedThreadFactoryDemo {
    private static ThreadPoolExecutor executorOne = new ThreadPoolExecutor(5, 5,
            1, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new NamedThreadFactory("ThreadOne"));
    private static ThreadPoolExecutor executorTwo = new ThreadPoolExecutor(5, 5,
            1, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new NamedThreadFactory("executorTwo"));

    public static void main(String[] args) {
        executorOne.execute(() -> {
            System.out.println("one do something");
            throw new NullPointerException();
        });
        executorTwo.execute(() -> {
            System.out.println("two do something");
        });

        executorOne.shutdown();
        executorTwo.shutdown();
    }

}
