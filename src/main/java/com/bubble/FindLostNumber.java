package com.bubble;

import java.util.HashMap;

/**
 * 查找数组中缺失的数字
 *
 * @author : wu gang
 * date : 2020/9/14 21:50
 */
public class FindLostNumber {

    public static void main(String[] args) {
        int[] array = new int[]{1, 5, 2, 5, 3, 0, 6};
        System.out.println(find(array));
    }

    private static int find(int[] array) {
        int[] temp = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            int val = array[i];
            temp[val] = val;
        }
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != i) {
                return i;
            }
        }
        return -1;
    }

}
