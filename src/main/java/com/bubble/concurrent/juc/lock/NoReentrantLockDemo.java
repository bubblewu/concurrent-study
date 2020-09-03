package com.bubble.concurrent.juc.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用自定义的独占锁来实现生产者消费者简单模型测试
 *
 * @author wugang
 * date: 2020-09-02 11:25
 **/
public class NoReentrantLockDemo {
    private final static NonReentrantLock LOCK = new NonReentrantLock();
//    private final static ReentrantLock LOCK = new ReentrantLock();

    private final static Condition NOT_FULL = LOCK.newCondition();
    private final static Condition NOT_EMPTY = LOCK.newCondition();

    private final static Queue<String> QUEUE = new LinkedBlockingQueue<>();
    private final static int QUEUE_SIZE = 10;

    public static void main(String[] args) {
        new Producer("Producer").start();
        for (int i = 0; i < 12; i++) {
//            new Producer("Producer" + i).start();
            new Consumer("Consumer" + i).start();
        }

    }

    private static class Producer extends Thread {

        public Producer(String name) {
            super(name);
        }

        @Override
        public void run() {
            // 获取独占锁
            LOCK.lock();
            try {
                List<String> dataList = genData();
                for (String s : dataList) {
                    // 如果队列满了，则等待。
                    // 使用while而不使用if，是为了防止虚假唤醒。
                    while (QUEUE.size() == QUEUE_SIZE) {
                        // 队列满，无空间，开始阻塞
                        NOT_EMPTY.await();
                    }
                    // 添加元素到队列
                    QUEUE.add(s);

                    // 告诉Consumer队列有数据了，唤醒NOT_FULL的全部阻塞线程
                    NOT_FULL.signalAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 释放锁
                LOCK.unlock();
            }
        }
    }

    private static class Consumer extends Thread {
        public Consumer(String name) {
            super(name);
        }

        @Override
        public void run() {
            // 获取独占锁
            LOCK.lock();
            try {
                // 队列为空，阻塞等待
                while (QUEUE.size() == 0) {
                    NOT_FULL.await();
                }
                // 消费
                String data = QUEUE.poll();
                System.out.println(Thread.currentThread().getName() + ": " + data);
                // 唤醒Producer线程
                NOT_EMPTY.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // 释放锁
                LOCK.unlock();
            }
        }
    }

    private static List<String> genData() {
        int count = 23;
        List<String> data = new ArrayList<>(count + 1);
        for (int i = 0; i < count; i++) {
            data.add(String.valueOf(i));
        }
        return data;
    }

}
