package com.bubble.concurrent.timer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时任务执行：开启定时任务有几种方案：Timer、Handler、ScheduleExecutorService等
 * <p>
 * 当一个Timer运行多个TimerTask时，只要其中一个TimerTask在执行中向run方法外抛出了异常，则其他任务也会自动终止。
 * <p>
 * 解决：
 * - 使用ScheduleThreadPoolExecutor：如果ScheduledThreadPoolExecutor中的一个任务抛出异常，其他任务则不受影响。
 * <p>
 * 区别：
 * - Timer是固定的多线程生产单线程消费，
 * - 但是ScheduledThreadPoolExecutor是可以配置的，既可以是多线程生产单线程消费也可以是多线程生产多线程消费，
 * 所以在日常开发中使用定时器功能时应该优先使用ScheduledThreadPoolExecutor。
 *
 * @author wugang
 * date: 2020-09-03 14:18
 **/
public class TimerDemo {
    /**
     * 创建定时器对象
     */
    private static Timer timer = new Timer();

    public static void main(String[] args) {
        testError();
    }

    /**
     * 当一个Timer运行多个TimerTask时，只要其中一个TimerTask在执行中向run方法外抛出了异常，则其他任务也会自动终止。
     */
    private static void testError() {
        // 添加任务1，延迟500ms执行
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("One Task");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                throw new RuntimeException("RuntimeException");
            }
        }, 500);
        // 添加任务2，延迟1000ms执行
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Two Task");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }, 1000);
    }


}
