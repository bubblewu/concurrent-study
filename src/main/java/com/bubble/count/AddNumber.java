package com.bubble.count;

import com.bubble.common.annotation.NotThreadSafe;

/**
 * 数字相加
 *
 * @author wugang
 * date: 2020-09-18 10:41
 **/
@NotThreadSafe
public class AddNumber extends Thread {

    private int count;

    public AddNumber(String name, int count) {
        super(name);
        this.count = count;
    }

    @Override
    public void run() {
        for (int j = 0; j < 10000; j++) {
            count++;
        }
    }

}
