package com.bubble.concurrent.juc.map;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 并发组件ConcurrentHashMap使用案例：
 * 场景：记录每个直播间的用户数据
 * <p>
 * 总结：
 * put（K key, V value）方法判断如果key已经存在，则使用value覆盖原来的值并返回原来的值，如果不存在则把value放入并返回null。
 * 而putIfAbsent（K key,V value）方法则是如果key已经存在则直接返回原来对应的值并不使用value覆盖，如果key不存在则放入value并返回null。
 * 另外要注意，判断key是否存在和放入是原子性操作。
 *
 * @author wugang
 * date: 2020-09-03 11:24
 **/
public class ConcurrentHashMapDemo {

    private static ConcurrentHashMap<String, List<String>> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 3; i++) {
            final int num = i;
            service.execute(() -> {
                String room;
                if (num == 1) {
                    room = "room-" + 0;
                } else {
                    room = "room-" + num;
                }
                List<String> users = new ArrayList<>();
                users.add(room + "-1");
                users.add(room + "-2");
                List<String> oldList = map.putIfAbsent(room, users);
                if (null != oldList) {
                    // 将该key的新结果，追加到老的用户集合
                    oldList.addAll(users);
                }
                System.out.println(JSON.toJSONString(map));
            });
        }

        service.shutdown();
    }

}
