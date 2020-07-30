package com.bubble.demo.single_thread_execution.semaphore;

/**
 * @author wugang
 * date: 2020-07-30 10:34
 **/
public class Main {

    public static void main(String[] args) {
        // 设置3个资源
        BoundedResource resource = new BoundedResource(3);
        // 10个线程交替使用资源，但同时使用的资源最多只能是3个
        for (int i = 0; i < 10; i++) {
            new UserThread(resource).start();
        }
    }

}
