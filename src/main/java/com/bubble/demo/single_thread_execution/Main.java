package com.bubble.demo.single_thread_execution;

/**
 * 创建门，并让三个人不断通过的类
 *
 * @author wugang
 * date: 2020-07-29 15:53
 **/
public class Main {

    /**
     * 由于Gate是非线程安全的，所以输出结果是混乱的。
     *
     */
    public static void main(String[] args) {
        System.out.println("测试开始，按[Ctrl + C]键退出");
        // 创建一个门，让三个人不断地通过
        Gate gate = new Gate();
        new UserThread(gate,"A小王", "A北京").start();
        new UserThread(gate,"B小李", "B上海").start();
        new UserThread(gate,"C小苏", "C南京").start();
    }

}
