package com.bubble.demo.guarded_suspension;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 发送请求的类：将请求加入到队列中
 *
 * @author wugang
 * date: 2020-08-17 18:25
 **/
public class ClientThread extends Thread {
    private final Random random;
    private final RequestQueue queue;

    public ClientThread(RequestQueue queue, String name, long seed) {
        super(name);
        this.queue = queue;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            Request request = new Request("No." + i);
            System.out.println(Thread.currentThread().getName() + "请求：" + request);
            queue.putRequest(request);
            try {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            } catch (InterruptedException ignore) {
            }
        }
    }

}
