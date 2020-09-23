package com.bubble.concurrent.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * 基于ForkJoin的分治思想，实现1到n个数的的和。
 *
 * @author wugang
 * date: 2020-09-04 16:26
 **/
public class SumTaskDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SumTask sumTask = new SumTask(0, 100);
        ForkJoinPool pool = new ForkJoinPool();
        Future<Long> future = pool.submit(sumTask);

        pool.shutdown();
        System.out.println(future.get());
    }

    static class SumTask extends RecursiveTask<Long> {
        private static final long serialVersionUID = -7983575829281192803L;

        private final static int THRESHOLD = 10;
        private final long start;
        private final long end;

        public SumTask(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            long sum = 0;
            long diff = end - start;
            if (diff <= THRESHOLD) {
                for (long i = start; i <= end; i++) {
                    sum += i;
                }
            } else {
//                long mid = (start + end) / 2;
                long mid = (start + end) >>> 1;
                // 分治、递归
                SumTask left = new SumTask(start, mid);
                SumTask right = new SumTask(mid + 1, end);
                left.fork();
                right.fork();
                sum = left.join() + right.join();
            }
            return sum;
        }
    }

}
