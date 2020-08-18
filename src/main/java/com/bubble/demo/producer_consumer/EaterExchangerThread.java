package com.bubble.demo.producer_consumer;

import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

/**
 * 表示来吃蛋糕的客人
 * 基于juc包下的Exchanger类： 用于让两个线程安全地交换对象。
 * 主要步骤：
 * - 使用exchange方法将空的缓冲区传递给生产端；
 * - 传递完成后，作为交换，接收生产端已经填满的缓冲区；
 * - 使用满的缓冲区的数据；
 *
 * @author wugang
 * date: 2020-07-16 20:00
 **/
public class EaterExchangerThread extends Thread {
    private final Exchanger<Object[]> exchanger;
    private Object[] dishArray;
    private final Random random;

    public EaterExchangerThread(String name, Exchanger<Object[]> exchanger, Object[] dishArray, long seed) {
        super(name);
        this.exchanger = exchanger;
        this.dishArray = dishArray;
        this.random = new Random(seed);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 交换缓冲区
                System.out.println(getName() + ": Before exchange");
                dishArray = exchanger.exchange(dishArray);
                System.out.println(getName() + ": After exchange");
                // 从缓冲区中取出蛋糕
                for (int i = 0; i < dishArray.length; i++) {
                    System.out.println(getName() + " take: " + dishArray[i]);
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
