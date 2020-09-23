package com.bubble.collection.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wugang
 * date: 2020-09-06 22:02
 **/
public class HashMapDemo {

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>(10);
        for (int i = 0; i < 10; i++) {
            map.put("key-" + i, i);
        }
        map.forEach((k, v) -> System.out.println(k + ":" + v));

        Map<String, Object> map2 = new ConcurrentHashMap<>();
    }

}
