package com.bubble.concurrent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 安全删除集合内的元素：
 * 不要在foreach里面进行元素的remove/add操作，remove请使用Iterator方式。
 * 否则会有下标越界错误。
 *
 * @author wugang
 * date: 2020-08-20 18:37
 **/
public class RemoveListDemo {

    public static void main(String[] args) {
        List<Integer> lists = Lists.newArrayList(1, 2, 3, 4, 5, 6);
        // lambda写法
//        lists.removeIf(obj -> 3 == obj);
        Iterator<Integer> iterator = lists.iterator();
        while (iterator.hasNext()) {
            Integer obj = iterator.next();
            if (3 == obj) {
                iterator.remove();
            }
        }
        System.out.println(lists);

        Map<String, String> map = Maps.newHashMap();
        map.put(null, "1");
        map.put("n", null);
        map.forEach((k,v) -> System.out.println(k + ":" + v));
    }

}
