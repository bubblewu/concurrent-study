package com.bubble.common.bloom;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * 基于Guava实现的布隆过滤器
 *
 * @author wugang
 * date: 2020-09-29 14:21
 **/
public class BloomFilterDemo {
    /**
     * 预计要插入数据量
     */
    private static final int SIZE = 100 * 10000;
    /**
     * 期望的误判率
     */
    private static final double FPP = 0.01;
    private static BloomFilter<Integer> bloomFilter = BloomFilter.create(
            Funnels.integerFunnel(), SIZE, FPP
    );

    public static void main(String[] args) {
        for (int i = 0; i < SIZE; i++) {
            bloomFilter.put(i);
        }

        int count = 0;
        for (int i = SIZE; i < 2 * SIZE; i++) {
            if (bloomFilter.mightContain(i)) {
                count++;
            }
        }
        System.out.println(String.format("误判总数为：%s, 误判率为：%s", count, (count * 1.0 / SIZE)));
    }

}
