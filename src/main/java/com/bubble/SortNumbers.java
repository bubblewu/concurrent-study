package com.bubble;

import java.util.Arrays;

/**
 * 各种常见的排序算法（参考：https://www.cnblogs.com/guoyaohua/p/8600214.html）
 * 算法分类：
 * - 内部排序：
 * - 外部排序：
 * 其中 内部排序包括：
 * 1、插入排序：直接插入排序、希尔排序；
 * 2、选择排序：简单选择排序、堆排序；
 * 3、交换排序：冒泡排序、快速排序；
 * 4、归并排序：
 * 5、基数排序：
 *
 *
 * @author : wu gang
 * date : 2020/9/14 22:49
 */
public class SortNumbers {
    public static void main(String[] args) {
        int[] array = new int[]{10, 1, 0, 3, 6, 81, 2, 4, 6, 2};
//        System.out.println("冒泡排泡排序：" + Arrays.toString(bubbleSort(array)));
        System.out.println("选择排序：" + Arrays.toString(selectionSort(array)));
    }

    /**
     * 冒泡排序：
     * 平均时间复杂度O(n^2)
     * 最佳情况：T(n) = O(n)   最差情况：T(n) = O(n^2)   平均情况：T(n) = O(n^2)
     * <P></>
     * 思想：
     * 遍历数组，一次比较两个元素，如果它们之间顺序错误就进行交换。直到不需要交换。
     * 这样越小的元素会经过交换后慢慢浮到数组的顶端。
     * <P></>
     * 步骤：
     * 1、比较相邻的2个元素，如果第一个比第二个大，就交换两者；
     * 2、对每一对相邻元素做同样的工作，从第一对到最后一对，这样最后的元素就会是最大值；
     * 3、针对所有的元素重复执行上面的步骤，除了最后一个；
     * 4、重复1-3步骤，直到排序完成
     *
     * @param array 数组
     * @return 排序后的数组
     */
    private static int[] bubbleSort(int[] array) {
        int size = array.length;
        if (size == 0) {
            return array;
        }
        for (int i = 0; i < size; i++) {
            // 一轮循环结束后，最大值在尾部，下次不参与排序
            for (int j = 0; j < size - 1 - i; j++) {
                // 如后面的比前面的小，交换位置
                if (array[j] > array[j + 1]) {
                    int temp = array[j + 1];
                    array[j + 1] = array[j];
                    array[j] = temp;
                }
            }
        }
        return array;
    }


    /**
     * 选择排序：
     * 最佳情况：T(n) = O(n^2)  最差情况：T(n) = O(n^2)  平均情况：T(n) = O(n^2)
     * <P></>
     * 表现最稳定的排序算法之一，因为无论什么数据进去都是O(n2)的时间复杂度，所以用到它的时候，数据规模越小越好。
     * 唯一的好处可能就是不占用额外的内存空间。
     * <P></>
     * 思想：
     * 首先在未排序数组中找到最小（大）元素，存放到排序数组的起始位置，
     * 然后，再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序数组的末尾。
     * 以此类推，直到所有元素均排序完毕。
     * <P></>
     * 步骤：
     * n个记录的直接选择排序可经过n-1趟直接选择排序得到有序结果。
     * 1、遍历数组，从索引为0开始，找到全局最小的数字，交换位置到数组头部；
     * 2、继续遍历剩余的未知大小的元素，也是找到局部最小值，将位置交换到局部数组的头部。
     *
     * @param array 数组
     * @return 排序后的数组
     */
    private static int[] selectionSort(int[] array) {
        int size = array.length;
        if (size == 0) {
            return array;
        }
        for (int i = 0; i < size; i++) {
            int minIndex = i;
            for (int j = i + 1; j < size; j++) {
                // 找到数组中最小的数
                if (array[j] < array[minIndex]) {
                    // 将最小数的索引保存
                    minIndex = j;
                }
            }
            int temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;
        }
        return array;
    }


}
