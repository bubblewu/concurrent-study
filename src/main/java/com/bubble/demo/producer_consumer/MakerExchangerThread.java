package com.bubble.demo.producer_consumer;

import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

/**
 * 表示糕点师
 * 基于juc包下的Exchanger类： 用于让两个线程安全地交换对象。
 * 主要步骤：
 * - 生产端填充字符，直到缓冲区被填满；
 * - 使用exchange方法将填满的缓冲区传递给消费端；
 * - 传递完成后，作为交换，接收消费端已经消费完的空的缓冲区；
 *
 * @author wugang
 * date: 2020-07-16 20:00
 **/
public class MakerExchangerThread extends Thread {
    private final Exchanger<Object[]> exchanger;
    private Object[] dishArray;
    private final Random random;
    /**
     * 蛋糕的流水号，所有糕点师共用
     */
    private static int id = 0;

    public MakerExchangerThread(String name, Exchanger<Object[]> exchanger, Object[] dishArray, long seed) {
        super(name);
        this.exchanger = exchanger;
        this.dishArray = dishArray;
        this.random = new Random(seed);
    }


    @Override
    public void run() {
        while (true) {
            try {
                // 向缓冲区填充字符
                for (int i = 0; i < dishArray.length; i++) {
                    dishArray[i] = nextId();
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                    System.out.println(getName() + " put: " + dishArray[i]);
                }
                // 交换缓冲区
                System.out.println(getName() + ": Before exchange");
                dishArray = exchanger.exchange(dishArray);
                System.out.println(getName() + ": After exchange");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static synchronized int nextId() {
        return id++;
    }

}
