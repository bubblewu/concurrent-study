package com.bubble.demo.immutable;

/**
 * 表示人的类：
 * - 线程安全的：
 * 字段值仅可以通过构造函数来设置，没有setXX方法。
 * 所以，Person类的实例一旦创建，其字段的值就不会发生变化。
 * 这时，即使多个线程同时访问同一个实例，该类也是安全的。
 * Person类中的所有方法无需声明为synchronized，就可以允许多个线程同时执行。
 *
 * - 防止子类修改其字段值：
 * 1、Person声明为final类型。表示我们无法创建其类的子类，也是防止子类修改其字段值的一种措施。
 * 2、字段的可见性都为private。表示这2个字段都只有从该类的内部才可以访问。
 * 3、字段都声明为final类型。表示一旦字段被赋值一次，就不会再被赋值。
 *
 * @author wugang
 * date: 2020-07-31 18:55
 **/
public final class Person {
    private final String name;
    private final String address;

    public Person(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "[Person: " + "name = " + name + ", address = " + address + ']';
    }
}
