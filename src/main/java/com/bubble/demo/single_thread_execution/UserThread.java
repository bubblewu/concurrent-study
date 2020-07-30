package com.bubble.demo.single_thread_execution;

/**
 * 表示人的类。将不断有人通过门
 *
 * @author wugang
 * date: 2020-07-29 16:04
 **/
public class UserThread extends Thread {
    private final Gate gate;
    private final String name;
    private final String address;

    public UserThread(Gate gate, String name, String address) {
        this.gate = gate;
        this.name = name;
        this.address = address;
    }

    @Override
    public void run() {
        System.out.println(name + " BEGIN");
        // 反复调pass方法，表示这个人在门里不断地穿梭通过
        while (true) {
            this.gate.pass(this.name, this.address);
        }
    }


}
