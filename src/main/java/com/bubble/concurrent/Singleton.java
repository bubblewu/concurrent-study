package com.bubble.concurrent;

/**
 * 双重锁检查的单例模式
 *
 * @author wugang
 * date: 2020-08-03 18:12
 **/
public class Singleton {
    /**
     * 对实例保证可见性和禁止重排序
     */
    private volatile static Singleton instance;

    private Singleton() {

    }

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        System.out.println(Singleton.getInstance().hashCode());
        System.out.println(Singleton.getInstance().hashCode());
    }

}
