package com.bubble.collection.map;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * @author wugang
 * date: 2020-09-06 22:02
 **/
public class HashMapDemo {

    public static void main(String[] args) {
//        Map<String, Integer> map = new HashMap<>(10);
//        for (int i = 0; i < 10; i++) {
//            map.put("key-" + i, i);
//        }
//        map.forEach((k, v) -> System.out.println(k + ":" + v));
//
//        Map<String, Object> map2 = new ConcurrentHashMap<>();


//        Map<String, Integer> map = new HashMap<>();
//        map.put("23_B", 71);
//        map.put("21_A", 10);
//        map.put("21_B", 13);
//        map.put("21_C", 10);
//        map.put("2_A", 10);
//        map.put("1_B", 10);
//        map.put("11_C", 2);
//        map.put("23_A", 0);
//        splitByKey(map);

//        System.out.println(0/0);
        System.out.println(10/20);
        System.out.println(1/3 * 1.00);
        System.out.println(BigDecimal.valueOf((float) 1 / 3).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        System.out.println(BigDecimal.valueOf((float) 1 * 100 / 3)
                .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
        + "%");
        System.out.println(BigDecimal.valueOf((float) 2 * 100 / 3)
                .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
                + "%");
    }

    private static void splitByKey(Map<String, Integer> map) {
//        // 排序
//        map.entrySet().stream().sorted(new Comparator<Map.Entry<String, Integer>>() {
//            @Override
//            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
//                String uid = o1.getKey().split("_")[0];
//                String otherUid = o2.getKey().split("_")[0];
//                return  Integer.parseInt(uid) - Integer.parseInt(otherUid);
//            }
//        }).forEach(entry -> {
//            String[] ut = entry.getKey().split("_");
//            String uid = ut[0];
//            String type = ut[1];
//            System.out.printf("%s : %s : %s%n", uid, type, entry.getValue());
//        });

        // 分组并求和
        map.entrySet().parallelStream().collect(Collectors.groupingBy(entry -> {
                    String[] ut = entry.getKey().split("_");
                    return ut[0];
                })
        ).forEach((key, value) -> {
            System.out.println(key);
            System.out.println(value);
            System.out.println("sum = " + value.stream().mapToInt(Map.Entry::getValue).sum());
            System.out.println("---");
        });
    }

}
