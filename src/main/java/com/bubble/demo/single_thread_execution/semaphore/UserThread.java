package com.bubble.demo.single_thread_execution.semaphore;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 资源使用者
 *
 * @author wugang
 * date: 2020-07-30 10:30
 **/
public class UserThread extends Thread {
    private final static Random random = new Random(2020);
    private final BoundedResource resource;

    public UserThread(BoundedResource resource) {
        this.resource = resource;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.resource.use();
                TimeUnit.MILLISECONDS.sleep(random.nextInt(3000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
