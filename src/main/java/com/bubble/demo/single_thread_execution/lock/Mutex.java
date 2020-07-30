package com.bubble.demo.single_thread_execution.lock;

/**
 * 互斥锁：
 * 将当前锁的个数记录到locks字段中。
 * 该锁个数是lock的调用次数减去unlock的调用次数后得到的结果。
 * 同时，调用lock方法的线程被记录到owner字段中。
 *
 * @author wugang
 * date: 2020-07-30 11:59
 **/
public class Mutex {
    private long locks = 0;
    private Thread owner = null;

    public synchronized void lock() {
        Thread me = Thread.currentThread();
        while (locks > 0 && owner != me) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        assert locks == 0 || owner == me;
        owner = me;
        locks++;
    }

    public synchronized void unlock() {
        Thread me = Thread.currentThread();
        if (locks == 0 || owner != me) {
            return;
        }
        assert  locks > 0 && owner == me;
        locks--;
        if (locks == 0) {
            owner = null;
            notifyAll();
        }
    }



}
