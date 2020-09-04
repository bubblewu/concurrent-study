package com.bubble.concurrent.forkjoin;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * 快速排序：
 * 思想：
 * - 利用数组的第1个元素把数组划分成两半，左边数组里面的元素小于或等于该元素，右边数组里面的元素比该元素大；
 * - 对左右的2个子数组分别排序。
 * <p>
 * 这里左右2个子数组是可以相互独立、并行计算的。因此可以利用ForkJoinPool。
 *
 * <p>
 * ForkJoinPool：（分治思想）
 * ForkJoinPool就是JDK7提供的一种“分治算法”的多线程并行计算框架。
 * 可以将ForkJoinPool看作一个单机版的Map/Reduce，只不过这里的并行不是多台机器并行计算，而是多个线程并行计算。
 * 相比于ThreadPoolExecutor，ForkJoinPool可以更好地实现计算的负载均衡，提高资源利用率。
 * - 如：假设有5个任务，在ThreadPoolExecutor中有5个线程并行执行，其中一个任务的计算量很大，其余4个任务的计算量很小，
 * 这会导致1个线程很忙，其他4个线程则处于空闲状态。
 * 利用ForkJoinPool，可以把大的任务拆分成很多小任务，然后这些小任务被所有的线程执行，从而实现任务计算的负载均衡。
 *
 * @author wugang
 * date: 2020-09-04 15:45
 **/
public class SortTaskDemo {

    public static void main(String[] args) throws InterruptedException {
        long[] array = {1, 3, 12, 4, 1, 23, 5, 9, 10, 16, 10};
        ForkJoinPool pool = new ForkJoinPool();
        SortTask sortTask = new SortTask(array);
        pool.submit(sortTask);

        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
    }


    static class SortTask extends RecursiveAction {
        private final long[] array;
        private final int start;
        private final int end;

        public SortTask(long[] array) {
            this.array = array;
            this.start = 0;
            this.end = array.length - 1;
        }

        public SortTask(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (start < end) {
                // 划分
                int m = partition(array, start, end);
                SortTask left = new SortTask(array, start, m - 1);
                SortTask right = new SortTask(array, m + 1, end);
                left.fork();
                right.fork();
                left.join();
                right.join();
                System.out.println(Arrays.toString(array));
            }
        }

        private int partition(long[] array, int start, int end) {
            long x = array[end];
            int i = start - 1;
            for (int j = start; j < end; j++) {
                if (array[j] <= x) {
                    i++;
                    swap(array, i, j);
                }
            }
            swap(array, i + 1, end);
            return i + 1;
        }

        private void swap(long[] array, int i, int j) {
            if (i != j) {
                long temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }

        }

    }


}
