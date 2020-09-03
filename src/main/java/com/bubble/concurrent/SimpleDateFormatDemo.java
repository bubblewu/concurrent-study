package com.bubble.concurrent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SimpleDateFormat是线程不安全的。
 * 它是Java提供的一个格式化和解析日期的工具类，多线程共用一个SimpleDateFormat实例对日期进行解析或者格式化会导致程序出错。
 * 如下面的错误案例：会报java.lang.NumberFormatException
 * <p>
 * 原因：
 * - 因为每个SimpleDateFormat实例里面都有一个Calendar对象。
 * - 而Calendar对象里面存放日前数据的变量都是线程不安全的，如fields、time等；
 * - 所以，Calendar是非线程安全的，导致SimpleDateFormat也是非线程安全的。
 * <p>
 * 解决：
 * - 将SimpleDateFormat定义成局部变量;
 * - 使用synchronized关键字修身parse的方法块，效率低；
 * - 使用ThreadLocal变为线程的本地变量，这样就会保证线程安全；（推荐）
 * - 使用JDK8的线程安全的新接口，如：LocalDate、LocalTime、LocalDateTime
 *
 * @author wugang
 * date: 2020-09-03 11:45
 **/
public class SimpleDateFormatDemo {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final ThreadLocal<SimpleDateFormat> THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
//        testNotSafe(exec);
        testSafe(exec);
        exec.shutdown();
    }

    private static void testNotSafe(ExecutorService exec) {
        for (int i = 0; i < 10; i++) {
            exec.execute(() -> {
                try {
                    System.out.println(sdf.parse("2020-09-03 11:48:00"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static void testSafe(ExecutorService exec) {
        for (int i = 0; i < 10; i++) {
            exec.execute(() -> {
                try {
                    System.out.println(THREAD_LOCAL.get().parse("2020-09-03 11:48:00"));
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    // 防止内存泄漏：因为ThreadLocal的key是弱引用，如果内部不足被回收了，导致value依然存在。
                    THREAD_LOCAL.remove();
                }
            });
        }
    }

}
