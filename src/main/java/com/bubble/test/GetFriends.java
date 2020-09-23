package com.bubble.test;

import java.util.*;

/**
 * 计算两列好友列表中，不重复的关系；
 * 如：[1,2]和[2,1]两者只能保留一个
 *
 * @author : wu gang
 * date : 2020/9/14 22:00
 */
public class GetFriends {

    public static void main(String[] args) {
        List<Friend> dataList = new ArrayList<>();
        dataList.add(new Friend(1, 2));
        dataList.add(new Friend(2, 1));
        dataList.add(new Friend(3, 5));
        dataList.add(new Friend(1, 7));
        dataList.add(new Friend(6, 2));
        dataList.add(new Friend(5, 3));
        dataList.add(new Friend(3, 8));
        relationship(dataList).forEach(f -> System.out.println(f.toString()));
    }

    private static Set<Friend> relationship(List<Friend> dataList) {
        Set<Friend> result = new HashSet<>();
        if (null == dataList || dataList.isEmpty()) {
            return result;
        }
        for (Friend friend : dataList) {
            result.add(new Friend(friend.getA(), friend.getB()));
        }
        return result;
    }

    static class Friend {
        private int a;
        private int b;
        public Friend(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Friend friend = (Friend) o;
            return (a == friend.a && b == friend.b) || (a == friend.b && b == friend.a);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b) + Objects.hash(b, a);
        }

        @Override
        public String toString() {
            return "Friend{" +
                    "a=" + a +
                    ", b=" + b +
                    '}';
        }
    }

}
