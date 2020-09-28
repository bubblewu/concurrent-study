package com.bubble.common;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 加锁工具类
 *
 * @author wugang
 * date: 2020-09-28 10:30
 **/
public class LockUtils {
    private static final String PREFIX = "Lock_";
    /**
     * 锁的池化效果
     */
    private static BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(40);
    private static Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    private LockUtils() {
    }

    private static class Single {
        private final static LockUtils LOCK_UTILS = new LockUtils();
    }

    public static LockUtils getInstance() {
        return Single.LOCK_UTILS;
    }

    /**
     * 加锁
     * @param key 唯一字符
     */
    public void lock(String key) {
        try {
            queue.put(1);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        getLock(key).lock();
    }

    /**
     * 解锁
     * @param key 唯一字符
     */
    public void unlock(String key) {
        getLock(key).unlock();
        try {
            queue.take();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 获取对象锁，每个字符串在缓存中有一个锁
     *
     * @param key  唯一字符
     * @return ReentrantLock锁
     */
    private synchronized ReentrantLock getLock(String key) {
        ReentrantLock lock = locks.get(PREFIX + key);
        if (lock == null) {
            lock = new ReentrantLock();
            locks.put(PREFIX + key, lock);
        }
        return lock;
    }

    /**
     * 清除锁的缓存
     *
     * @param key  唯一字符
     */
    public void cleanCacheLock(String key) {
        ReentrantLock lock = locks.get(PREFIX + key);
        if (lock == null) {
            return;
        }
        // 查询当前线程是否保持此锁定
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
        // 判断有没有线程在等待这个许可
        if (!lock.hasQueuedThreads()) {
            locks.remove(PREFIX + key);
        }
    }


    public static void main(String[] args) {
        System.out.println(LockUtils.getInstance().hashCode());
        System.out.println(LockUtils.getInstance().hashCode());
    }

}
