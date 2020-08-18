package com.bubble.demo.guarded_suspension;

/**
 * @author wugang
 * date: 2020-08-18 10:19
 **/
public class TalkThread extends Thread {
    private final RequestQueue input;
    private final RequestQueue output;

    public TalkThread(RequestQueue input, RequestQueue output, String name) {
        super(name);
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        System.out.println("--> " + Thread.currentThread().getName() + ": 开始");
        for (int i = 0; i < 20; i++) {
            // 接收对方的请求
            Request request = input.getRequest();
            System.out.println(Thread.currentThread().getName() + " gets "+ request);
            // 添加一个感叹号再返回给对方
            Request request2 = new Request(request.getName() + "!");
            System.out.println(Thread.currentThread().getName() + " puts "+ request2);
            output.putRequest(request2);
        }
        System.out.println("  <-- " + Thread.currentThread().getName() + ": 结束");
    }

}
