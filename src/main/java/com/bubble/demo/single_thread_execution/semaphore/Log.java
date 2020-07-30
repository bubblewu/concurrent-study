package com.bubble.demo.single_thread_execution.semaphore;

/**
 * @author wugang
 * date: 2020-07-30 10:23
 **/
public class Log {

    public static void println(String s) {
        System.out.println(Thread.currentThread().getName() + ": " + s);
    }
}
