package com.bubble.concurrent.juc.lock;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 基于读写锁来实现线程安全的list
 * 由于ReentrantLock是独占锁，所以在读多写少的情况下性能很差，
 * 所以可以使用ReentrantReadWriteLock来改进。
 * <p>
 * 读写锁：（可重入锁、非独占锁、适合读多写少的场景）
 * - 内部维护来一个ReadLock和WriteLock，他们以来Sync实现具体功能，Sync是继承AQS的也提供了公平锁和非公平锁。
 * - AQS中只维护了一个state状态，而读写锁需要维护读和写两种状态。
 * 分别使用state的高16位表示读状态（获取到读锁的次数），后16位表示写状态（写锁的线程的可重入次数）。
 *
 * @author wugang
 * date: 2020-09-02 15:55
 **/
public class ReentrantReadWriteLockList {
    /**
     * 非线程安全的list
     */
    private final ArrayList<Object> array = new ArrayList<>();

    /**
     * 读写锁
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public void add(Object obj) {
        writeLock.lock();
        try {
            array.add(obj);
        } finally {
            writeLock.unlock();
        }
    }

    public void remove(Object obj) {
        writeLock.lock();
        try {
            array.remove(obj);
        } finally {
            writeLock.unlock();
        }
    }

    public Object get(int index) {
        readLock.lock();
        try {
            return array.get(index);
        } finally {
            readLock.unlock();
        }
    }

}
