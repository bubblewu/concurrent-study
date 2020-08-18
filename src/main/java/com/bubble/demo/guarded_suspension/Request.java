package com.bubble.demo.guarded_suspension;

/**
 * 表示一个请求的类
 *
 * @author wugang
 * date: 2020-08-17 18:25
 **/
public class Request {

    private final String name;

    public Request(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[Request: " + name + "]";
    }
}
