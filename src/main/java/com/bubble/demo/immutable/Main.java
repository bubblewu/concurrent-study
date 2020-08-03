package com.bubble.demo.immutable;

/**
 * 测试主类
 *
 * @author wugang
 * date: 2020-07-31 18:55
 **/
public class Main {

    /**
     * 创建一个Person类，并启动三个线程来访问该实例
     */
    public static void main(String[] args) {
        Person person = new Person("Bubble", "北京");
        new PrintPersonThread(person).start();
        new PrintPersonThread(person).start();
        new PrintPersonThread(person).start();
    }

}
