package com.bubble.count;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * 数字加一相关操作
 * - 线程不安全且不进行线程等待时，结果的范围？ [0-2] 因为未等待，只有两个线程执行，一般会出现0，1，2三种情况。
 * - 线程不安全且进行线程等待时，结果的范围？
 * - 线程安全且不进行线程等待时，结果的范围？
 * - 线程安全且进行线程等待时，结果的范围？200
 *
 * @author wugang
 * date: 2020-09-18 10:09
 **/
public class CountAddDemo extends Thread {

    private static CountDownLatch countDownLatch = new CountDownLatch(2);
    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(2, () -> {
        System.out.println("done");
    });

    private static int i = 0;
    private Random random = new Random(2020);

    @Override
    public void run() {
        try {
            Thread.sleep(random.nextInt(3 * 1000));
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        synchronized (CountAddDemo.class) {
            for (int j = 0; j < 10000; j++) {
                i++;
                System.out.println("No." + i);
            }
//            try {
//                Thread.sleep(random.nextInt(3 * 1000));
//            } catch (InterruptedException exception) {
//                exception.printStackTrace();
//            }
            CountAddDemo.class.notifyAll();
        }

//        countDownLatch.countDown();
//        try {
//            cyclicBarrier.await();
//        } catch (InterruptedException | BrokenBarrierException exception) {
//            exception.printStackTrace();
//        }
    }

    private static void doSomething() {
        Thread t1 = new CountAddDemo();
        t1.setName("T1");
        Thread t2 = new CountAddDemo();
        t2.setName("T2");
        t1.start();
        t2.start();

        synchronized (CountAddDemo.class) {
            try {
                System.out.println("enter wait...");
                CountAddDemo.class.wait();
                System.out.println("out wait...");
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
//            t2.wait();
        }

        System.out.println(i);
    }


    public static void main(String[] args)  {
        // 线程不安全且不进行线程等待时，结果的范围？
        // 因为未等待，只有两个线程执行，
//        new CountAddDemo().start();
//        new CountAddDemo().start();
//        System.out.println(i);


        // 线程不安全且进行线程等待时，结果的范围？使用join：输出
//        Thread t1 = new CountAddDemo();
//        Thread t2 = new CountAddDemo();
//        t1.start();
//        t2.start();
//        t1.join(3000);
//        t2.join();
//        System.out.println(i);

        // 线程不安全且进行线程等待时，结果的范围？使用倒计数器：
//        new CountAddDemo().start();
//        new CountAddDemo().start();
//        countDownLatch.await();
//        System.out.println(i);

        // 线程不安全且进行线程等待时，结果的范围？使用循环栅栏：输出和预期不一致
//        new CountAddDemo().start();
//        new CountAddDemo().start();
//        System.out.println(i);


        doSomething();
    }
}
