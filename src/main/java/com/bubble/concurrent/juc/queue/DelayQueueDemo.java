package com.bubble.concurrent.juc.queue;

import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * DelayQueue延迟队列测试：出队的顺序和delay时间有关，而与创建任务的顺序无关。
 *
 *
 * - DelayQueue并发队列是一个无界阻塞延迟队列。
 * - 队列中的每个元素都有个过期时间，当从队列获取元素时，只有过期元素才会出队列。
 * - 队列头元素是最快要过期的元素。
 * <p>
 * 其内部使用PriorityQueue存放数据，使用ReentrantLock实现线程同步。
 * 另外队列里面的元素要实现Delayed接口，其中一个是获取当前元素到过期时间剩余时间的接口，
 * 在出队时判断元素是否过期了，一个是元素之间比较的接口，因为这是一个有优先级的队列。
 *
 * @author wugang
 * date: 2020-09-02 18:19
 **/
public class DelayQueueDemo {

    static class DelayData implements Delayed {
        /**
         * 延迟时间
         */
        private final long delayTime;
        /**
         * 到期时间
         */
        private final long expireTime;
        private String name;

        public DelayData(String name, long delayTime) {
            this.name = name;
            this.delayTime = delayTime;
            this.expireTime = System.currentTimeMillis() + delayTime;
        }

        /**
         * 剩余时间 = 到期时间 - 当前时间
         *
         * @param unit 时间单位
         * @return 剩余时间
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expireTime - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }

        @Override
        public String toString() {
            return "{" +
                    "delayTime=" + delayTime +
                    ", expireTime=" + expireTime +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        // 创建delay队列
        DelayQueue<DelayData> delayQueue = new DelayQueue<>();
        // 创建延迟任务
        Random random = new Random(2020);
        for (int i = 0; i < 10; i++) {
            DelayData data = new DelayData(
                    "task-" + i,
                    random.nextInt(500));
                    delayQueue.offer(data);
        }
        // 一次取出任务并打印
        DelayData delayData = null;
        // 避免虚假唤醒，则不能把全部元素都打印出来
        try {
            for (;;) {
                while (null != (delayData = delayQueue.take())) {
                    System.out.println(delayData.toString());
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
