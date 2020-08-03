package com.bubble.demo.immutable;

import java.util.concurrent.TimeUnit;

/**
 * 显示Person实例的线程的类
 *
 * @author wugang
 * date: 2020-07-31 18:55
 **/
public class PrintPersonThread extends Thread {
    private Person person;

    public PrintPersonThread(Person person) {
        this.person = person;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // sleep 让各线程可以清晰的交叉打印
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " prints " + person.toString());
        }
    }

}
