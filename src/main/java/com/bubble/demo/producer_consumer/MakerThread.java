package com.bubble.demo.producer_consumer;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 表示糕点师
 *
 * @author wugang
 * date: 2020-07-16 20:00
 **/
public class MakerThread extends Thread {
    private final Random random;
    private final Table table;
    /**
     * 蛋糕的流水号，所有糕点师共用
     */
    private static int id = 0;

    public MakerThread(String name, Table table, long seed) {
        super(name);
        this.table = table;
        this.random = new Random(seed);
    }


    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                String cake = "Cake No." + nextId() + " by " + getName();
                table.put(cake);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static synchronized int nextId() {
        return id++;
    }

}
