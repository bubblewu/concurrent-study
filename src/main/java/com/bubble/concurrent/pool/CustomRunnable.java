package com.bubble.concurrent.pool;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 自定义一个简单的Runnable类
 *
 * @author wugang
 * date: 2020-08-05 10:45
 **/
public class CustomRunnable implements Runnable {

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println(name + " Start: " + LocalDateTime.now().toString());
        doSomething();
        System.out.println(name + " End: " + LocalDateTime.now().toString());
    }

    private void doSomething() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
