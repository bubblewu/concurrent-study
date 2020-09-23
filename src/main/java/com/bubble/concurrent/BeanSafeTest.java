package com.bubble.concurrent;

import com.bubble.common.pool.NamedThreadFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Bean的线程安全测试
 *
 * @author wugang
 * date: 2020-09-23 17:39
 **/
public class BeanSafeTest {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 1L;
    private static final int QUEUE_SIZE = 10000;

    private static final Map<Integer, SrcCount> userCountMap = new ConcurrentHashMap<>(176);

    public static void main(String[] args) throws InterruptedException {
        Instant begin = Instant.now();
        CountDownLatch countDownLatch = new CountDownLatch(CORE_POOL_SIZE);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_SIZE),
                new NamedThreadFactory("BeanSafeTest"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        List<Integer> userList = new ArrayList<>(1000);
        for (int i = 0; i < 22; i++) {
            if (i < 10) {
                for (int j = 0; j < 100; j++) {
                    userList.add(i);
                }
            } else {
                userList.add(i);
            }
        }
        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            int size = userList.size();
            int batchSize = size / (CORE_POOL_SIZE - 1);
            int start = i * batchSize;
            int end = i * batchSize + batchSize;
            final List<Integer> currentList = userList.subList(start, Math.min(end, size));
            executor.execute(new BeanAdd(userCountMap, currentList, countDownLatch));
        }
        countDownLatch.await();
        executor.shutdown();
        // sleep等待所有线程执行结束：类似于join、CountdownLatch、CyclicBarrier
//        if (!executor.isShutdown()) {
//            sleep(100);
//        }

        userCountMap.forEach((k, v) -> {
            System.out.println(String.format("%s : %s", k, v.toString()));
        });
        System.out.println(String.format("OrderSrcJob total costs %s ms", Duration.between(begin, Instant.now()).toMillis()));
    }


    static class BeanAdd implements Runnable {
        private List<Integer> userList;
        private Map<Integer, SrcCount> userCountMap;
        private CountDownLatch countDownLatch;

        public BeanAdd(Map<Integer, SrcCount> userCountMap, List<Integer> userList, CountDownLatch countDownLatch) {
            this.userCountMap = userCountMap;
            this.userList = userList;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
//                userList.forEach(System.out::println);
                for (int i = 0; i < userList.size(); i++) {
                    for (int j = 0; j < 1000; j++) {
                        int uid = userList.get(i);
                        if (userCountMap.containsKey(uid)) {
                            SrcCount old = userCountMap.get(uid);
                            SrcCount srcCount = new SrcCount();
                            srcCount.setTotalCount(old.getTotalCount() + 1);
                            srcCount.setApiCount(old.getApiCount() + 1);
                            srcCount.setCrawlerCount(old.getCrawlerCount() + 1);
                            srcCount.setSupplyCount(old.getSupplyCount() + 1);
                            srcCount.setSelfSupportCount(old.getSelfSupportCount() + 1);
                            userCountMap.put(uid, srcCount);
                        } else {
                            SrcCount srcCount = new SrcCount();
                            srcCount.setTotalCount(1);
                            srcCount.setApiCount(1);
                            srcCount.setCrawlerCount(1);
                            srcCount.setSupplyCount(1);
                            srcCount.setSelfSupportCount(1);
                            userCountMap.put(uid, srcCount);
                        }
                    }
                }
                sleep(1000);
            } finally {
                countDownLatch.countDown();
            }

        }

        private static void sleep(long time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
