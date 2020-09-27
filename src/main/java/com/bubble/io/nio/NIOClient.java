package com.bubble.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 基于NIO的客户端：
 * BIO是同步阻塞IO操作，当线程在处理任务时，另一方会阻塞着等待该线程的执行完毕，为了提高效率；
 * JDK1.4后，引入NIO来提升数据的通讯性能
 * NIO中采用Reactor设计模式，注册的汇集点为Selector，NIO有三个主要组成部分：Channel（通道）、Buffer（缓冲区）、Selector（选择器）
 * - NIO采用了轮询的方式来观察事件是否执行完毕，如：
 * A让B打印某个文件，BIO会一直等待着B返回，期间自己不做其他事情，
 * 而NIO则会不断的询问B是否完成，未完成则处理自己的时，直至B完成。
 * <p>
 * - Channel（通道）：Channel是一个对象，可以通过它读取和写入数据；
 * - Selector（对象选择器）： Selector是一个对象，它可以注册到很多个Channel上，监听各个Channel上发生的事件，
 * 并且能够根据事件情况决定Channel读写
 *
 * @author wugang
 * date: 2020-09-27 14:35
 **/
public class NIOClient {

    public static void main(String[] args) throws IOException {
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(
                new InetSocketAddress(NIOUtils.HOST_NAME, NIOUtils.PORT)
        );
        ByteBuffer byteBuffer = ByteBuffer.allocate(NIOUtils.BUFFER_SIZE);
        boolean flag = true;
        while (flag) {
            byteBuffer.clear();
            String input = NIOUtils.getString("请输入待发送的信息：").trim();
            // 将数据存入缓冲区
            byteBuffer.put(input.getBytes());
            // 重置缓冲区: byteBuffer从写模式变成读模式
            byteBuffer.flip();
            // 发送数据
            clientChannel.write(byteBuffer);
            byteBuffer.clear();

            int read = clientChannel.read(byteBuffer);
            byteBuffer.flip();
            System.err.print(new String(byteBuffer.array(), 0, read));
            if (NIOUtils.STOP.equalsIgnoreCase(input)) {
                flag = false;
            }
        }
        clientChannel.close();
    }

}
