package com.bubble.concurrent.juc.lock;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于ReentrantLock来实现一个简单的线程安全的list
 * 通过在操作array元素前进行加锁，保证同一时间只有一个线程可以对array数组进行修改，但是也只能有一个线程对array元素进行访问
 * <p>
 * ReentrantLock：（可重入的独占锁）
 * - 是可重入的独占锁，同时只能有一个线程可以获取该锁，其他获取锁的线程会被阻塞而放入到该锁的AQS阻塞队列中。
 * - 基于继承AQS的Sync类来实现具体功能，包括公平锁和非公平锁两种形式，默认位非公平锁。（子类NonfairSync和FairSync分别实现了获取锁的非公平与公平策略。）
 * 公平锁的tryAcquire策略与非公平的类似，不同之处在于：在设置CAS前添加了hasQueuedPredecessors方法（该方法是实现公平性的核心代码）。
 * hasQueuedPredecessors方法来判断当前线程节点是否有前驱节点。如果当前线程节点有前驱节点则返回true，否则如果当前AQS队列为空或者当前线程节点是AQS的第一个节点则返回false。
 * - AQS的state状态值表示线程获取该锁的可重入次数，在默认情况下，state的值为0表示当前锁没有被任何线程持有。
 * 当一个线程第一次获取该锁时会尝试使用CAS设置state的值为1，如果CAS成功则当前线程获取了该锁，然后记录该锁的持有者为当前线程。
 * 在该线程没有释放锁的情况下第二次获取该锁后，状态值被设置为2，这就是可重入次数。
 * 在该线程释放该锁时，会尝试使用CAS让状态值减1，如果减1后状态值为0，则当前线程释放该锁。
 * - 一个ReentrantLock对象对应一个AQS线程阻塞队列和多个Condition对象。
 * ReentrantLock提供了一个Condition（条件）类，用来实现分组唤醒需要唤醒的线程们，而不是像synchronized要么随机唤醒一个线程要么唤醒全部线程。
 *
 * @author wugang
 * date: 2020-09-02 15:25
 **/
public class ReentrantLockList {
    /**
     * 非线程安全的list
     */
    private final ArrayList<Object> array = new ArrayList<>();

    /**
     * 独占锁，使用volatile保证可见性
     */
    private volatile ReentrantLock lock = new ReentrantLock(false);

    public void add(Object obj) {
        lock.lock();
        try {
            array.add(obj);
        } finally {
            lock.unlock();
        }
    }

    public void remove(Object obj) {
        lock.lock();
        try {
            array.remove(obj);
        } finally {
            lock.unlock();
        }
    }

    public Object get(int index) {
        lock.lock();
        try {
            return array.get(index);
        } finally {
            lock.unlock();
        }
    }

}
