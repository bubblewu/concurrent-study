package com.bubble.demo.balking;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 表示可以修改并保存的数据类
 *
 * @author wugang
 * date: 2020-08-18 11:16
 **/
public class Data {
    /**
     * 保存的文件名称
     */
    private final String fileName;
    /**
     * 数据内容
     */
    private String content;
    /**
     * 守护条件：修改后的内容如果还未保存，就未true
     */
    private boolean changed;

    public Data(String fileName, String content) {
        this.fileName = fileName;
        this.content = content;
        this.changed = true;
    }

    /**
     * 修改数据内容
     *
     * @param newContent 新内容
     */
    public synchronized void change(String newContent) {
        content = newContent;
        changed = true;
    }

    /**
     * 若数据内容已经修改，则保存到文件中
     */
    public synchronized void save() {
        if (!changed) {
            System.out.println(Thread.currentThread().getName() + "no change, return");
            return;
        }
        doSave();
        changed = false;
    }

    /**
     * 将数据内容保存到文件
     */
    private void doSave() {
        System.out.println(Thread.currentThread().getName() + " save content: " + content);
        try {
            Writer writer = new FileWriter(fileName);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            System.err.println(String.format("save error, fileName = %s, content = %s", fileName, content) + e);
        }
    }

}
