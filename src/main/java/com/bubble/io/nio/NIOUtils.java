package com.bubble.io.nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * NIO工具
 *
 * @author wugang
 * date: 2020-09-27 16:15
 **/
public class NIOUtils {
    public static final String HOST_NAME = "localhost";
    public static final int PORT = 9999;
    public static final int BUFFER_SIZE = 1024;
    public static final String STOP = "886";

    private static final BufferedReader KEYBOARD_INPUT = new BufferedReader(
            new InputStreamReader(System.in)
    );

    public static String getString(String data) {
        // 数据接收标记
        boolean flag = true;
        String str = null;
        while (flag) {
            System.out.println(data);
            try {
                // 读取输入的一行数据
                str = KEYBOARD_INPUT.readLine();
                if (str == null || "".equals(str)) {
                    System.out.println("输入数据为空！");
                } else {
                    flag = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str;
    }


}
