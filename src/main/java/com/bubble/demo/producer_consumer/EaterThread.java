package com.bubble.demo.producer_consumer;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 表示来吃蛋糕的客人
 *
 * @author wugang
 * date: 2020-07-16 20:00
 **/
public class EaterThread extends Thread {
    private final Random random;
    private final Table table;

    public EaterThread(String name, Table table, long seed) {
        super(name);
        this.table = table;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String cake = table.take();
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
