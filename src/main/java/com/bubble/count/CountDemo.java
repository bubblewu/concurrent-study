package com.bubble.count;

/**
 * @author wugang
 * date: 2020-09-18 11:16
 **/
public class CountDemo {
    private static int count;
    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            new AddNumber("Add-" + i, count).start();
        }
        System.out.println(count);
    }

}
