package com.bubble.demo.producer_consumer;

/**
 * Channel角色：表示桌子
 *
 * @author wugang
 * date: 2020-07-16 19:59
 **/
public class Table {
    /**
     * 盘子：放置蛋糕的数组
     */
    private final String[] dishArray;
    /**
     * 下一次放置蛋糕的位置
     */
    private int tail;
    /**
     * 下一次取蛋糕的位置
     */
    private int head;
    /**
     * 当前桌子上放置的蛋糕个数
     */
    private int count;

    public Table(int totalCount) {
        this.dishArray = new String[totalCount];
        this.head = 0;
        this.tail = 0;
        this.count = 0;
    }

    /**
     * 放置蛋糕
     *
     * @param cake 蛋糕
     */
    public synchronized void put(String cake) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + " put: " + cake);
        // 最多只能放置3个
        while (count >= dishArray.length) {
            wait();
        }
        dishArray[tail] = cake;
        // 取下一次要放置的位置
        tail = (tail + 1) % dishArray.length;
        count++;
        notifyAll();
    }

    /**
     * 取蛋糕
     *
     * @return 蛋糕
     */
    public synchronized String take() throws InterruptedException {
        // 桌子上没有蛋糕，等待
        while (count <= 0) {
            wait();
        }
        String cake = dishArray[head];
        // 取下一次要取的位置
        head = (head + 1) % dishArray.length;
        count--;
        notifyAll();
        System.out.println(Thread.currentThread().getName() + " take: " + cake);
        return cake;
    }

}
