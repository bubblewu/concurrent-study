package com.bubble.concurrent.juc.queue;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * PriorityBlockingQueue优先级队列测试：
 * - 任务执行的先后顺序和它们被放入队列的先后顺序没有关系，而是和它们的优先级有关系。
 *
 * @author wugang
 * date: 2020-09-02 18:10
 **/
public class PriorityBlockingQueueDemo {

    static class Task implements Comparable<Task> {
        private int priority = 0;
        private String name;

        @Override
        public int compareTo(Task o) {
            if (this.priority >= o.priority) {
                return 1;
            } else {
                return -1;
            }
        }

        public void doSomething() {
            System.out.println(name + ": " + priority);
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) {
        // 创建任务并添加到优先级队列
        PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>();
        Random random = new Random(2020);
        for (int i = 0; i < 10; i++) {
            Task task = new Task();
            task.setPriority(random.nextInt(10));
            task.setName("task-" + i);
            queue.offer(task);
        }

        // 取出任务执行
        while (!queue.isEmpty()) {
            Task task = queue.poll();
            if (null != task) {
                task.doSomething();
            }
        }
    }

}
