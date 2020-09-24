package com.bubble.test;

import com.bubble.common.annotation.Recommend;
import com.bubble.common.annotation.ThreadSafe;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 多线程下ConcurrentHashMap涉及读写时的线程安全的推荐写法
 *
 * @author wugang
 * date: 2020-09-20 15:50
 **/
@Recommend
@ThreadSafe
public class GetWordCount {

    public static void main(String[] args) throws InterruptedException {
        Instant begin = Instant.now();
        int poolSize = 5;
        CountDownLatch countDownLatch = new CountDownLatch(poolSize);
        Map<String, Integer> wordCountMap = new ConcurrentHashMap<>(1400);
//        // 读取文件数据
//        try {
//            List<String> dataList = Files.readAllLines(Paths.get(""));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i > 50) {
                dataList.add("a d f");
            } else {
                dataList.add("a b c");
            }
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                poolSize, 10,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < poolSize; i++) {
            int size = dataList.size();
            int cur = size / (poolSize - 1);
            int start = i * cur;
            int end = i * cur + cur;
            int min = Math.min(size, end);
            List<String> currentList = dataList.subList(start, min);
            executor.execute(new Reader(currentList, wordCountMap, countDownLatch));
        }
        countDownLatch.await();
        executor.shutdown();

        wordCountMap.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue() - o1.getValue())
                .limit(100)
                .forEach(e -> System.out.println("word: " + e.getKey() + ", count: " + e.getValue()));
        System.out.println("Total costs [" + Duration.between(begin, Instant.now()).toMillis() + "] ms");
    }

    static class Reader implements Runnable {
        private final Map<String, Integer> countMap;
        private final List<String> dataList;
        private final CountDownLatch countDownLatch;

        public Reader(List<String> dataList, Map<String, Integer> countMap, CountDownLatch countDownLatch) {
            this.dataList = dataList;
            this.countMap = countMap;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                dataList.forEach(line -> {
                    String[] lines = line.split(" ");
                    for (String word : lines) {
                        if (countMap.computeIfPresent(word, (k, v) -> v + 1) == null) {
                            // 注意：需锁定类，不能this对象实例，不然多线程下每次都会生成新的对象实例，加锁无效
                            synchronized (Reader.class) {
                                if (countMap.computeIfPresent(word, (k, v) -> v + 1) == null) {
                                    countMap.putIfAbsent(word, 1);
                                }
                            }
                        }
                    }
                });
            } finally {
                countDownLatch.countDown();
            }
        }

    }


}
