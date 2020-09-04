package com.bubble.concurrent;

import com.bubble.common.pool.NamedThreadFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadLocal的内存泄漏案例
 * <p>
 * 问题：
 * - 没有调用线程池的shutdown或者shutdownNow方法，所以线程池里面的用户线程不会退出，进而JVM进程也不会退出。
 * 基于jconsole命令可以得知：
 * - 没有remove时，当主线程处于休眠时，进程占用了大概77MB内存；
 * - 有remove时，当主线程处于休眠时，进程占用了大概20MB内存；
 * 明显没有remove时，发生了内存泄漏。
 * <p>
 * 分析：
 * - 在设置线程的localVariable变量后没有调用localVariable.remove（）方法，
 * 这导致线程池里面5个核心线程的threadLocals变量里面的new LocalVariable（）实例没有被释放。
 * 虽然线程池里面的任务执行完了，但是线程池里面的5个线程会一直存在直到JVM进程被杀死。
 * 这里需要注意的是：
 * - 由于localVariable被声明为了static变量，虽然在线程的ThreadLocalMap里面对localVariable进行了弱引用，
 * 但是localVariable不会被回收。第二次运行代码时，由于线程在设置localVariable变量后及时调用了localVariable. remove（）方法进行了清理，
 * 所以不会存在内存泄漏问题。
 * <p>
 * 总结：
 * 如果在线程池里面设置了ThreadLocal变量，则一定要记得及时清理，
 * 因为线程池里面的核心线程是一直存在的，如果不清理，线程池的核心线程的threadLocals变量会一直持有ThreadLocal变量。
 * <p>
 * ThreadLocal内存泄漏的原因：
 * 因为ThreadLocalMap中的Entry数组是继承自弱引用WeakReference的，所以key是对ThreadLocal对象对弱引用。
 * 如果使用强引用，即使其他地方没有对ThreadLocal对象引擎，ThreadLocalMap中的ThreadLocal对象还是不会被回收，
 * 而使用弱引用，ThreadLocal在下一次垃圾回收的时候会被回收掉的。但是value还是不能回收的。
 * 所以会出现，ThreadLocalMap中的key会为null，而value不为null的Entry。导致内存泄漏。
 * <p>
 * 解决方案：
 * 虽然ThreadLocalMap提供了set、get和remove方法，可以在一些时机下对这些Entry项进行清理，但是这是不及时的，也不是每次都会执行，
 * 所以在一些情况下还是会发生内存漏，因此在使用完毕后及时调用remove方法才是解决内存泄漏问题的王道。
 *
 * @author wugang
 * date: 2020-09-04 14:24
 **/
public class ThreadLocalMemLeakDemo {

    static class LocalVariable {
        private Long[] data = new Long[1024 * 1024];
    }

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5,
            1L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new NamedThreadFactory("test"),
            new ThreadPoolExecutor.CallerRunsPolicy());
    private static ThreadLocal<LocalVariable> local = new ThreadLocal<>();

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            executor.execute(() -> {
                local.set(new LocalVariable());
                System.out.println("use local variable");
                local.remove();
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
        System.out.println("pool executor over.");
    }

}
