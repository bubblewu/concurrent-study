package com.bubble.concurrent.timer;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * B任务在A任务之后N秒或执行
 *
 * @author wugang
 * date: 2020-09-03 15:42
 **/
public class AThenBDemo {

    private static class TestTask implements Runnable {
        private String name;

        public TestTask(String tag) {
            name = tag;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            System.out.println(name + "\t" + LocalDateTime.now().toString());
        }
    }

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        int time = 3;
        TestTask a = new TestTask("A");
        TestTask b = new TestTask("B");

        executor.schedule(a, time, TimeUnit.SECONDS);

        // 再上一个任务结束时间的3秒后执行
        executor.schedule(b, time * 2, TimeUnit.SECONDS);
        executor.shutdown();
    }

}
