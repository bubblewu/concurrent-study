package com.bubble.demo.guarded_suspension;

/**
 * 测试：一个线程ClientThread将请求Request的实例传递给另一个线程ServerThread
 *
 * @author wugang
 * date: 2020-08-17 18:22
 **/
public class Main {

    public static void main(String[] args) {
        RequestQueue requestQueue = new RequestQueue();
//        for (int i = 0; i < 3; i++) {
//            new ClientThread(requestQueue, "大泡泡-" + i, 2020).start();
//            new ClientThread(requestQueue, "Bubble-" + i, 2020).start();
//        }
        new ClientThread(requestQueue, "大泡泡", 2020).start();
        new ServerThread(requestQueue, "Bubble", 2020).start();

    }

}
