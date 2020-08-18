package com.bubble.demo.guarded_suspension;

import com.bubble.common.exception.LivenessException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 请求存放队列
 *
 * @author wugang
 * date: 2020-08-17 18:25
 **/
public class RequestQueueV2 {
    /**
     * LinkedBlockingQueueFIFO队列，存放请求。
     * take取出队首元素，put向末尾添加元素。（已经考虑了互斥处理，所以下面的方法无需加Synchronized来保护queue字段）
     * 当队列为空时，若使用take方法便会进行wait。
     */
    private final BlockingQueue<Request> queue = new LinkedBlockingQueue<>();

    /**
     * 取出并返回最先存放的那个请求。
     * 如队列为空，就一直等待，直到唤醒。
     * - 判断queue中是痘存在可取的元素；
     * - 从queue中取出一个元素
     *
     * @return 最先存放的那个请求
     */
    public synchronized Request getRequest() {
        Request request = null;
        try {
//            request = queue.take();
            request = queue.poll(10L, TimeUnit.SECONDS);
            if (request == null) {
                throw new LivenessException("thrown by:" + Thread.currentThread().getName());
            }
        } catch (InterruptedException ignored) {
        }
        return request;
    }

    /**
     * 添加一个请求到队列
     *
     * @param request 请求
     */
    public synchronized void putRequest(Request request) {
        try {
//            queue.put(request);
            boolean offered = queue.offer(request, 10L, TimeUnit.SECONDS);
            if (!offered) {
                throw new LivenessException("thrown by:" + Thread.currentThread().getName());
            }
        } catch (InterruptedException ignored) {
        }
    }

}
