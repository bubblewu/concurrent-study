package com.bubble.demo.balking;

import java.util.Random;

/**
 * 修改并保存数据内容的类
 *
 * @author wugang
 * date: 2020-08-18 11:18
 **/
public class ChangerThread extends Thread {
    private final Data data;
    private final Random random;

    public ChangerThread(String name, Data data) {
        super(name);
        this.data = data;
        this.random = new Random();
    }

    @Override
    public void run() {
        for (int i = 0; true ; i++) {
            try {
                data.change("No." + i);
                Thread.sleep(random.nextInt(1000));
                data.save();
            } catch (InterruptedException ignored) {
            }
        }
    }

}
