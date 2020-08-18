package com.bubble.demo.guarded_suspension;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 请求存放队列
 *
 * @author wugang
 * date: 2020-08-17 18:25
 **/
public class RequestQueue {
    /**
     * FIFO队列，存放请求。
     * LinkedList是非线程安全的。
     * 所以：下面的get和put方法都是用了Synchronized来保护queue字段（LinkedList的实例），
     * 保证它是SingleThreadedExecution模式的即getRequest()中的两个处理（前置条件和目标处理）必须同时由一个线程来执行。
     * - 判断queue中是痘存在可取的元素；
     * - 从queue中取出一个元素
     */
    private final Queue<Request> queue = new LinkedList<>();

    /**
     * 取出并返回最先存放的那个请求。
     * 如队列为空，就一直等待，直到唤醒。
     *
     * @return 最先存放的那个请求
     */
    public synchronized Request getRequest() {
        // 如队列存在元素，就会返回头元素（不删除）；如为空，则返回null
        // 也就是Guarded Suspension模式中的守护条件，即目前进行处理的前置条件
        while (queue.peek() == null) {
            try {
                // 线程要执行某个实例的wait方法时，线程必须获取该实例的锁。
                // wait方法被调用时，获取的时this的锁。
                // 执行this的wait方法后，线程进入this的等待队列，并释放持有的this锁。
                // notify、notifyAll或interrupt会让线程退出等待队列，但在实际地继续执行处理之前，还必须再获取this的锁。
                System.out.println("Wait: " + Thread.currentThread().getName() + ": wait begin, queue = " + queue);
                wait();
                System.out.println("Wait: " + Thread.currentThread().getName() + ": wait end, queue = " + queue);
            } catch (InterruptedException ignored) {
            }
        }
        // 移除队列中的第一个元素并返回，如队列为空则抛出NoSuchElementException
        return queue.remove();
    }

    /**
     * 添加一个请求到队列
     *
     * @param request 请求
     */
    public synchronized void putRequest(Request request) {
        queue.offer(request);
        System.out.println("Notify: " + Thread.currentThread().getName() + ": notifyAll begin, queue = " + queue);
        notifyAll();
        System.out.println("Notify: " + Thread.currentThread().getName() + ": notifyAll begin, queue = " + queue);
    }

}
