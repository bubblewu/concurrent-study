package com.bubble.demo.balking;

/**
 * 文本内容自动保存测试
 *
 * @author wugang
 * date: 2020-08-18 11:18
 **/
public class Main {

    public static void main(String[] args) {
        Data data = new Data("data.txt", "(empty)");
        new ChangerThread("ChangerThread", data).start();
        new SaverThread("SaverThread", data).start();
    }

}
