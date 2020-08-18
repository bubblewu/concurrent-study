package com.bubble.demo.balking;

import java.util.concurrent.TimeUnit;

/**
 * 定期保存数据内容的类
 *
 * @author wugang
 * date: 2020-08-18 11:17
 **/
public class SaverThread extends Thread {
    private final Data data;

    public SaverThread(String name, Data data) {
        super(name);
        this.data = data;
    }

    @Override
    public void run() {
        while (true) {
            // 保存数据
            data.save();
            try {
                // 每隔1s就保存一次
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

}
