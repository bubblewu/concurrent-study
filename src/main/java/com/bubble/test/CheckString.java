package com.bubble.test;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author wugang
 * date: 2020-09-20 14:53
 **/
public class CheckString {

    public static void main(String[] args) {
        System.out.println(check("abba", "北京 杭州 杭州 北京"));
        System.out.println(check("aabb", "北京 杭州 杭州 北京"));
        System.out.println(check("abc", "北京 杭州 杭州 南京"));
        System.out.println(check("acac", "北京 杭州 北京 广州"));
    }

    private static boolean check(String pattern, String str) {
        if (StringUtils.isBlank(pattern) || StringUtils.isBlank(str)) {
            return false;
        }
        String[] strArray = str.split(" ");
        int sLen = strArray.length;
        int pLen = pattern.length();
        if (sLen != pLen) {
            return false;
        }
        Map<Character, String> map = Maps.newHashMap();
        for (int i = 0; i < sLen; i++) {
            String val = strArray[i];
            char rule = pattern.charAt(i);
            if (map.containsKey(rule)) {
                if (!val.equals(map.get(rule))) {
                    return false;
                }
            }
            map.put(rule, val);
        }
        return true;
    }

}
