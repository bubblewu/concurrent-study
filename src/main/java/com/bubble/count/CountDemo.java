package com.bubble.count;

import com.bubble.common.LockUtils;

/**
 * @author wugang
 * date: 2020-09-18 11:16
 **/
public class CountDemo {
    private static int count = 0;
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new AddNumber("Add-1");
        Thread t2 = new AddNumber("Add-2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(count);
    }

    static class AddNumber extends Thread {

        public AddNumber(String name) {
            super(name);
        }

        @Override
        public void run() {
            LockUtils.getInstance().lock(CountDemo.class.toString());
            for (int j = 0; j < 10000; j++) {
                count++;
            }
            LockUtils.getInstance().unlock(CountDemo.class.toString());
        }
    }


}
