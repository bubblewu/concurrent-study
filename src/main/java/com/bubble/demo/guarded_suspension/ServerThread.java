package com.bubble.demo.guarded_suspension;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 接收请求的类
 *
 * @author wugang
 * date: 2020-08-17 18:26
 **/
public class ServerThread extends Thread {
    private final Random random;
    private final RequestQueue queue;

    public ServerThread(RequestQueue queue, String name, long seed) {
        super(name);
        this.queue = queue;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            Request request = queue.getRequest();
            System.out.println(Thread.currentThread().getName() + "处理：" + request);
            try {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            } catch (InterruptedException ignore) {
            }
        }
    }

}
