package com.bubble.concurrent.pool;

import com.bubble.common.pool.CancelDiscardPolicy;
import com.bubble.common.pool.NamedThreadFactory;

import java.util.concurrent.*;

/**
 * 线程池使用FutureTask时如果把拒绝策略设置为DiscardPolicy和DiscardOldestPolicy，
 * 并且在被拒绝的任务的Future对象上调用了无参get方法，那么调用线程会一直被阻塞。
 * <p>
 * 线程池四种拒绝策略：（如果队列已满且当前的线程数达到了maximumPoolSize，还有新的任务，直接采用拒绝策略处理。）
 * - AbortPolicy(抛出一个异常，默认的)
 * - DiscardPolicy(直接丢弃任务)
 * - DiscardOldestPolicy（丢弃队列里最老的任务，将当前这个任务继续提交给线程池）
 * - CallerRunsPolicy（交给线程池调用所在的线程进行处理)
 *
 * 解决方案：
 * - 当使用Future时，尽量使用带超时时间的get方法，这样即使使用了DiscardPolicy拒绝策略也不至于一直等待，超时时间到了就会自动返回。
 * - 如果非要使用不带参数的get方法则可以重写DiscardPolicy的拒绝策略，在执行策略时设置该Future的状态大于COMPLETING即可。
 * - 查看FutureTask提供的方法，会发现只有cancel方法是public的，并且可以设置FutureTask的状态大于COMPLETING。(这样只会多出一个异常抛出，线程不会消亡)
 * - 最好的情况是，重写拒绝策略时设置FutureTask的状态为NORMAL，但是这需要重写FutureTask方法，因为FutureTask并没有提供接口让我们设置。
 * @author wugang
 * date: 2020-09-03 19:18
 **/
public class FutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 线程池单个线程，线程池队列元素个数为1
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1),
                new NamedThreadFactory("one"),
                new CancelDiscardPolicy()
        );
        // 添加任务1
        // 向线程池提交了一个任务one，并且这个任务会由唯一的线程来执行，任务在打印后会阻塞该线程2s。
        Future futureOne = executor.submit(() -> {
            System.out.println("run one.");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
//            return "one";
        });
        // 添加任务2
        // 向线程池提交了一个任务two，这时候会把任务two放入阻塞队列。
        Future futureTwo = executor.submit(() -> {
            System.out.println("run two.");
//            return "two";
        });
        // 添加任务3
        // 向线程池提交任务three，由于队列已满所以触发拒绝策略丢弃任务three:
        // - ThreadPoolExecutor.AbortPolicy(): 直接拒绝抛出异常。会正常返回，并且会输出如下结果Task java.util.concurrent.FutureTask@3941a79c rejected from java.util.concurrent.ThreadPoolExecutor@506e1b77[Running, pool size = 1, active threads = 1, queued tasks = 1, completed tasks = 0]
        // - ThreadPoolExecutor.DiscardPolicy(): 直接丢弃任务。任务three会一直阻塞而不会返回
        // - ThreadPoolExecutor.DiscardOldestPolicy()：丢弃队列里最老的任务，将当前这个任务继续提交给线程池。任务three执行，任务two被阻塞。
        // - ThreadPoolExecutor.CallerRunsPolicy():交给线程池调用所在的线程进行处理。任务正常执行(即使4个任务呢也会正常执行)
        Future futureThree = null;
        try {
            futureThree = executor.submit(() -> {
                System.out.println("run three.");
//                return "three";
            });
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
//        Future<String> futureFour = executor.submit(() -> {
//            System.out.println("run four.");
//            return "four";
//        });
        System.out.println("task one: " + futureOne.get());
        System.out.println("task two: " + futureTwo.get());
        System.out.println("task three: " + (futureThree == null ? null : futureThree.get()));
//        System.out.println("task four: " + futureFour.get());

        executor.shutdown();
    }

}
