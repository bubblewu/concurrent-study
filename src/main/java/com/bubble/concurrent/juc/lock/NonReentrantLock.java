package com.bubble.concurrent.juc.lock;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于AQS实现一个不可重入的独占锁：
 * 自定义AQS需要重写一系列函数，还需要定义原子变量state的含义和锁支持条件变量。
 * - state为0表示目前锁没有被线程持有，state为1表示锁已经被某个线程持有。
 *
 * @author wugang
 * date: 2020-09-02 11:04
 **/
public class NonReentrantLock implements Lock, Serializable {
    private static final long serialVersionUID = 5930239729983720312L;

    /**
     * 内部类Sync继承了AQS来实现具体的锁操作。
     * 独占锁：所以需要重写tryAcquire、tryRelease和isHeldExclusively三个方法。
     * 还需提供newCondition方法来支持条件变量
     */
    private static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 8681114571950588876L;

        /**
         * 如果state为0，则尝试获取锁
         *
         * @param acquires 数量
         * @return boolean
         */
        @Override
        protected boolean tryAcquire(int acquires) {
            assert acquires == 1;
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 尝试释放锁，设置state为0
         *
         * @param releases 数量
         * @return boolean
         */
        @Override
        protected boolean tryRelease(int releases) {
            assert releases == 1;
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        /**
         * 是否锁已经被持有
         *
         * @return boolean
         */
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        /**
         * 提供条件变量接口
         *
         * @return 条件变量
         */
        Condition newCondition() {
            return new ConditionObject();
        }

    }

    /**
     * 内部类Sync继承了AQS来实现具体的锁操作。
     */
    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
