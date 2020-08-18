package com.bubble.concurrent;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Volatile关键字：变量自增运算测试（非线程安全）
 * 作用：
 * - 保证可见性，即一个线程修改了该变量的值后，新值对其他线程是立即可知道的。（volatile变量每次使用前都会刷新）
 * - 禁止指令重排序。
 *
 * @author wugang
 * date: 2020-08-03 17:47
 **/
public class VolatileTest {
    private static final int THREAD_COUNT = 20;
    private static volatile int count = 0;
    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void increase() {
        synchronized (VolatileTest.class) {
            count++;
        }
        atomicInteger.incrementAndGet();
    }

    /**
     * 20个线程，每个线程对count变量进行10000次自增操作，如正确执行并发的话，应该输出：20 * 10000。
     * 但实际每次输出结果都不同，都是小于200000的。
     * <p>
     * 注意：
     * 在IDEA中运行这段程序，会由于IDE自动创建一条名为Monitor Ctrl-Break的线程（从名字看应该是监控Ctrl-Break中断信号的）
     * 而导致while循环无法结束，改为大于2 或者用Thread::join()方法代替可以解决该问题。
     */
    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10000; j++) {
                        increase();
                    }
                }
            });
//            System.out.println("add " + i);
            threads[i].start();
        }

        // 等待所有累加线程都结束
        while (Thread.activeCount() > 2) {
            // 主动让出线程的执行时间
            Thread.yield();
        }
        System.out.println("count: " + count);
        System.out.println("atomicInteger: " + atomicInteger.get());
    }

}
