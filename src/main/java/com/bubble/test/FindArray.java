package com.bubble.test;

/**
 * @author wugang
 * date: 2020-09-20 14:40
 **/
public class FindArray {

    public static void main(String[] args) {
        int[] array = new int[]{1, 3, 5, 6};
        System.out.println(find(array, 5));
        System.out.println(find(array, 2));
        System.out.println(find(array, 7));
        System.out.println(find(array, 0));
    }

    private static int find(int[] array, int target) {
        if (array == null || array.length == 0) {
            throw new RuntimeException("请输入正确的数组");
        }
        int size = array.length;
        if (target < array[0]) {
            return 0;
        } else if (target > array[size - 1]) {
            return size;
        }
        for (int i = 0; i < size - 1; i++) {
            int current = array[i];
            int next = array[i + 1];
            if (current == target) {
                return i;
            } else if (target > current && target < next) {
                return i + 1;
            }
        }
        return size;
    }

}
