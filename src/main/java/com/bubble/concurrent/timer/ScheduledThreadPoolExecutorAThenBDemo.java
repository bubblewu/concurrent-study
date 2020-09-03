package com.bubble.concurrent.timer;

import com.sun.xml.internal.bind.v2.model.core.ID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务ScheduledThreadPoolExecutor测试：
 * 三种场景：（假设间隔时间为N，N2）
 * - schedule(): 提交后仅间隔N后执行一次；
 * - scheduleWithFixedDelay(): 按间隔固定时间执行。第一次执行后，在上一次执行结束时间的基础上再间隔N2再执行。循环执行。
 * - scheduleAtFixedRate(): 按固定频率执行。第一次执行后，在上一次执行开始时间基础上再间隔N2再执行。循环执行。
 *
 * @author wugang
 * date: 2020-09-03 14:49
 **/
public class ScheduledThreadPoolExecutorAThenBDemo {
    private ScheduledThreadPoolExecutor executor;
    private Runnable task;

    @Before
    public void before() {
        executor = initExecutor();
        task = initTask();
    }

    @After
    public void after() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    /**
     * 提交一个延迟执行的任务，任务从提交时间算起延迟单位为unit的delay时间后开始执行。
     * 提交的任务不是周期性任务，任务只会执行一次，执行结束会自动停止
     */
    @Test
    public void testSchedule() {
        print("testSchedule start main thread: " + getNowTime());
        // 仅在启动5s后执行一次
        executor.schedule(task, 5, TimeUnit.SECONDS);
        sleep(TimeUnit.SECONDS, 10);
        print("testSchedule end main thread: " + getNowTime());
    }

    /**
     * 当任务执行完毕后，让其延迟固定时间后再次运行（fixed-delay任务）。
     * - 其中initialDelay表示提交任务后延迟多少时间开始执行任务command,
     * - delay表示当任务执行完毕后延长多少时间后再次运行command任务，
     * - unit是initialDelay和delay的时间单位。
     * 任务会一直重复运行直到任务运行中抛出了异常，被取消了，或者关闭了线程池
     */
    @Test
    public void testDelayedTask() {
        print("start main thread: " + getNowTime());
        // 在启动5s后执行第一次，以后每隔10秒执行一次线程（线程结束时间开始计时）
        executor.scheduleWithFixedDelay(task, 5, 10, TimeUnit.SECONDS);
        sleep(TimeUnit.SECONDS, 120);
        print("end main thread: " + getNowTime());
    }

    /**
     * 该方法相对起始时间点以固定频率调用指定的任务（fixed-rate任务）。
     * 当把任务提交到线程池并延迟initialDelay时间（时间单位为unit）后开始执行任务command。
     * 然后从initialDelay+period时间点再次执行，而后在initialDelay + 2 *period时间点再次执行，
     * 循环往复，直到抛出异常或者调用了任务的cancel方法取消了任务，或者关闭了线程池。
     */
    @Test
    public void testFixedTask() {
        print("start main thread: " + getNowTime());
        // 在启动5s后执行第一次，以后每隔10秒执行一次线程（线程开始时间开始计时）
        executor.scheduleAtFixedRate(task, 5, 10, TimeUnit.SECONDS);
        sleep(TimeUnit.SECONDS, 120);
        print("end main thread: " + getNowTime());
    }

    private ScheduledThreadPoolExecutor initExecutor() {
//        return (ScheduledThreadPoolExecutor) new ThreadPoolExecutor(
//                2,
//                Integer.MAX_VALUE,
//                0, TimeUnit.NANOSECONDS,
//                new DelayQueue(),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.AbortPolicy());
        return new ScheduledThreadPoolExecutor(3);
    }

    private Runnable initTask() {
        return () -> {
            print("start task: " + LocalDateTime.now().toString());
            sleep(TimeUnit.SECONDS, 2);
            print("end task: " + LocalDateTime.now().toString());
        };
    }

    private String getNowTime() {
        return LocalDateTime.now().toString();
    }

    private void sleep(TimeUnit unit, long time) {
        try {
            unit.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void print(String msg) {
        System.out.println(msg);
    }
}
