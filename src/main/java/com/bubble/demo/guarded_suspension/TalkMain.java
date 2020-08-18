package com.bubble.demo.guarded_suspension;

/**
 * @author wugang
 * date: 2020-08-18 10:19
 **/
public class TalkMain {

    public static void main(String[] args) {
        RequestQueue input = new RequestQueue();
        RequestQueue output = new RequestQueue();
        input.putRequest(new Request("hello"));
        // 发生死锁
        // A等待B发生请求，B等待A发送请求
        new TalkThread(input, output, "A").start();
        new TalkThread(output, input, "B").start();
    }

}
