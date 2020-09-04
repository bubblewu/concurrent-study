package com.bubble.common.pool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadFactory接口：是一个线程工厂，用来创建线程。
 * - 为了统一在创建线程时设置一些参数，如是否守护线程和线程一些特性等，如优先级。
 * - 通过这个TreadFactory创建出来的线程能保证有相同的特性。
 * - 它首先是一个接口类，而且方法只有一个。就是创建一个线程。
 * <p>
 * 自定义命名线程工程类：为线程池中的线程指定名称，方便问题追踪
 * 根据Executors.defaultThreadFactory()来改写。
 *
 * @author wugang
 * date: 2020-09-03 18:31
 **/
public class NamedThreadFactory implements ThreadFactory {
    /**
     * 记录当前线程池的编号，是static的原子变量。它是应用级别的，所有线程池共用一个。
     * 比如：创建第一个线程池时线程池编号为1，创建第二个线程池时线程池的编号为2，所以pool-1-thread-1里面的pool-1中的1就是这个值。
     */
    private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
    /**
     * 它是线程池级别的，每个线程池使用该变量来记录池中线程的编号。
     * 如：pool-1-thread-1里面的thread-1中的1就是这个值
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * 线程组
     */
    private final ThreadGroup group;
    /**
     * 线程名的前缀，默认为"pool"
     */
    private final String namePrefix;

    public NamedThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        if (null == name || name.isEmpty()) {
            name = "pool";
        }
        namePrefix = name + "-" + POOL_NUMBER.getAndIncrement() + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        // 真正创建线程的地方，设置了线程的线程组及线程名
        Thread thread = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            // 默认是正常优先级
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }

}
