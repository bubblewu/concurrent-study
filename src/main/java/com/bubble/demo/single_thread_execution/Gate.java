package com.bubble.demo.single_thread_execution;

import java.util.concurrent.TimeUnit;

/**
 * 表示门的类。会记录通行者的姓名和出生地
 * 非线程安全/pass/toString方法加synchronized为线程安全
 *
 * @author wugang
 * date: 2020-07-29 15:55
 **/
public class Gate {
    /**
     * 表示到目前为止已经通过这道门的人数
     **/
    private int counter = 0;
    /**
     * 表示最后一个通行者的姓名
     **/
    private String name = "NoBody";
    /**
     * 表示最后一个通行者的出生地
     **/
    private String address = "NoWhere";

    /**
     * 表示通过门
     */
    public synchronized void pass(String name, String address) {
        this.counter++;
        this.name = name;
        // 在name和address赋值之间调用sleep，延长临界区，可以提高检查出错误的可能性，不需等数万次执行才发现。
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.address = address;
        check();
    }

    /**
     * 检查门的最后一个通行者的记录数据是否正确。
     * 注意：不需要添加synchronized。
     * 因为check方法只有pass方法会调用。且时私有的，也就是不会被其他类调用，所以是安全的
     */
    private void check() {
        // 如姓名和首字母不同，说明数据异常
        if (name.charAt(0) != address.charAt(0)) {
            System.out.println("*** 异常 *** :" + toString());
        }
    }

    /**
     * 一般来说，多个线程共享的字段必须使用synchronized或者volatile来保护。
     *
     * 假设当线程A正在执行pass方法时，其他线程B调用了toString方法。
     * 在线程B引用了name字段的值，但尚未引用address期间，线程A可能会修改address的值。
     * 这样，toString方法对线程B创建时使用name和address对首字母就可能会不一致。
     */
    @Override
    public synchronized String toString() {
        return "No." + counter + ": name='" + name + ", address=" + address;
    }
}
