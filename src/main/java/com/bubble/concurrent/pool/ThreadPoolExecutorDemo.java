package com.bubble.concurrent.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 阿里巴巴推荐的使用ThreadPoolExecutor构造函数自定义参数的方式来创建线程池
 * 能够更加明确线程池的运行规则，规避资源耗尽的风险。
 * 弊端是因为：
 * - FixedThreadPool和SingleThreadExecutor：允许请求的队列长度为Integer.MAX_VALUE，可能堆积大量的请求，导致OOM；
 * - CachedThreadPool和ScheduledThreadPool：允许创建的线程数量为Integer.MAX_VALUE，可能会创建大量线程，导致OOM；
 *
 * @author wugang
 * date: 2020-08-05 10:54
 **/
public class ThreadPoolExecutorDemo {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 10L;
    private static final int QUEUE_SIZE = 100;


    /**
     * 执行10个线程时：指定了核心线程数为5个，所以，线程池每次会同时执行5个任务，这5个任务执行完成之后，剩余的5个任务才会被执行。
     *
     * @param args
     */
    public static void main(String[] args) {
        /* corePoolSize：核心线程数，定义了最小可以同时执行的线程数量；
         * maximumPoolSize：能同时运行的最大线程数，当队列中存放的任务达到队列容量时，当前可以同时运行的线程数量变为最大线程数。
         * keepAliveTime：当线程池中的数量大于corePoolSize时，如没有新任务提交，核心线程外的线程不会被立即销毁，等待达到keepAliveTime时间才被回收销毁。
         * unit：keepAliveTime的时间单位
         * workQueue：当有新任务时，会先判断当前运行的线程数量是否达到corePoolSize核心线程数，如已经达到新任务就被存放在队列中。
         * handler：饱和策略。如果当前同时运行的线程数达到maximumPoolSize，且队列中的任务已经存放满时。
         *  - AbortPolicy：默认，直接抛出RejectedExecutionException异常来拒绝新任务的处理；
         *  - CallerRunsPolicy：调用执行自己的线程执行任务。当最大池被填满时，此策略可以提供可伸缩队列。（建议使用）
         *  - DiscardPolicy：不处理新任务，直接丢弃；
         *  - DiscardOldestPolicy：丢弃最早的未处理的任务请求；
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_SIZE),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            Runnable worker = new CustomRunnable();
            // execute()方法：用于提交不需要返回值的任务到线程池，所以无法判断任务是否被线程池执行成功；
            executor.execute(worker);

            OtherRunnable other = new OtherRunnable();
            executor.execute(other);
//            new Thread(other, "T-").start();
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Finished all threads");
    }

}
