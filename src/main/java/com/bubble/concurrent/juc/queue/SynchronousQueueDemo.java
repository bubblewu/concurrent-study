package com.bubble.concurrent.juc.queue;

import java.util.Random;
import java.util.concurrent.SynchronousQueue;

/**
 * SynchronousQueue是一个不存储元素的阻塞队列。
 * SynchronousQueue中的每个put操作都必须等待一个take操作完成，否则不能继续添加元素。
 * 我们可以将SynchronousQueue看作一个“快递员”，它负责把生产者线程的数据直接传递给消费者线程，
 * 非常适用于传递型场景，比如将在一个线程中使用的数据传递给另一个线程使用。
 * SynchronousQueue的吞吐量高于LinkedBlockingQueue和ArrayBlockingQueue。
 *
 * @author wugang
 * date: 2020-09-07 19:39
 **/
public class SynchronousQueueDemo {

    public static void main(String[] args) {
        SynchronousQueue<Integer> queue = new SynchronousQueue<>();
        new Producer(queue).start();
        new Consumer(queue).start();
    }

    static class Producer extends Thread {
        private SynchronousQueue<Integer> queue;
        public Producer(SynchronousQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                int num = new Random(2020).nextInt(1000);
                try {
                    queue.put(num);
                    System.out.println("Producer: " + num);
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }

    }

    static class Consumer extends Thread {
        SynchronousQueue<Integer> queue;
        public Consumer(SynchronousQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    int num = queue.take();
                    System.out.println("Consumer: " + num);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }

    }

}
