package com.bubble.common.pool;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 重写定义DiscardPolicy(直接丢弃任务)
 * 使用这个策略时，在cancel的任务上调用get（）方法会抛出异常。
 *
 * <p>
 * 解决问题：
 * 线程池使用FutureTask时如果把拒绝策略设置为DiscardPolicy和DiscardOldestPolicy，
 * 并且在被拒绝的任务的Future对象上调用了无参get方法，那么调用线程会一直被阻塞。
 * <p>
 * 线程池四种拒绝策略：（如果队列已满且当前的线程数达到了maximumPoolSize，还有新的任务，直接采用拒绝策略处理。）
 * - AbortPolicy(抛出一个异常，默认的)
 * - DiscardPolicy(直接丢弃任务)
 * - DiscardOldestPolicy（丢弃队列里最老的任务，将当前这个任务继续提交给线程池）
 * - CallerRunsPolicy（交给线程池调用所在的线程进行处理)
 * <p>
 * 解决方案：
 * - 当使用Future时，尽量使用带超时时间的get方法，这样即使使用了DiscardPolicy拒绝策略也不至于一直等待，超时时间到了就会自动返回。
 * - 如果非要使用不带参数的get方法则可以重写DiscardPolicy的拒绝策略，在执行策略时设置该Future的状态大于COMPLETING即可。
 * - 查看FutureTask提供的方法，会发现只有cancel方法是public的，并且可以设置FutureTask的状态大于COMPLETING。(这样只会多出一个异常抛出，线程不会消亡)
 * - 最好的情况是，重写拒绝策略时设置FutureTask的状态为NORMAL，但是这需要重写FutureTask方法，因为FutureTask并没有提供接口让我们设置。
 *
 * @author wugang
 * date: 2020-09-04 10:45
 **/
public class CancelDiscardPolicy implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            if (r instanceof FutureTask) {
                ((FutureTask<?>) r).cancel(true);
            }
        }
    }
}
