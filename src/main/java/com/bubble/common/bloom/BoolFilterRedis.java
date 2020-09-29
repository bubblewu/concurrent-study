package com.bubble.common.bloom;

import com.google.common.annotations.Beta;
import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 基于Redis的位图bitmap结构实现布隆过滤器
 *
 * @author wugang
 * date: 2020-09-29 14:29
 **/
public class BoolFilterRedis {
    /**
     * 预计要插入数据量
     */
    private static final int SIZE = 100 * 10000;
    /**
     * 期望的误判率
     */
    private static final double FPP = 0.01;

    /**
     * bit数组的长度
     */
    private static long numBits;
    /**
     * hash函数的数量
     */
    private static int numHashFunc;

    static {
        numBits = getNumBits(SIZE, FPP);
        numHashFunc = getNumHashFunc(SIZE, FPP);
    }

    /**
     * 计算Hash函数的数量
     *
     * @param size 预计要插入数据量
     * @param fpp  期望误判率
     * @return Hash函数的数量
     */
    private static int getNumHashFunc(int size, double fpp) {
        return Math.max(1, (int) Math.round((double) fpp / size * Math.log(2)));
    }

    /**
     * 计算bit数组的长度
     *
     * @param size 预计要插入数据量
     * @param fpp  期望误判率
     * @return bit数组长度
     */
    private static long getNumBits(int size, double fpp) {
        if (fpp == 0) {
            fpp = Double.MIN_VALUE;
        }
        return (long) (-size * Math.log(fpp) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 根据key获取bitmap的下标
     *
     * @param key key值
     * @return 在bitmap中的下标集合
     */
    private static long[] getIndexes(String key) {
        long hash_1 = hash(key);
        long hash_2 = hash_1 >>> 16;
        long[] result = new long[numHashFunc];
        for (int i = 0; i < numHashFunc; i++) {
            long combinedHash = hash_1 + i * hash_2;
            if (combinedHash < 0) {
                combinedHash = ~combinedHash;
            }
            result[i] = combinedHash % numBits;
        }
        return result;
    }

    @Beta
    private static long hash(String key) {
        return Hashing.murmur3_128().hashObject(
                key, Funnels.stringFunnel(StandardCharsets.UTF_8)
        ).asLong();
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6379);
//        for (int i = 0; i < SIZE; i++) {
//            long[] indexes = getIndexes(String.valueOf(i));
//            Arrays.stream(indexes).forEach(index -> {
//                jedis.setbit("bloom:test", index, true);
//            });
//        }

        int errorCount = 0;
        for (int i = SIZE; i < 2 * SIZE; i++) {
            long[] indexes = getIndexes(String.valueOf(i));
            boolean isContain = Arrays.stream(indexes).allMatch(index -> jedis.getbit("bloom:test", index));
            if (isContain) {
                errorCount++;
            }
        }
        System.out.println(String.format("误判总数为：%s, 误判率为：%s", errorCount, (errorCount * 1.0 / SIZE)));
    }


}
