package com.bubble.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于NIO的Server端:
 * - 定义了serverSocketChannel用于ServerSocketChannel的建立和端口的绑定；
 * - byteBuffer缓冲区用于不同Channel之间的数据交互；
 * - selector用于监听服务器各个Channel上数据的变化并做出响应。
 *
 * @author wugang
 * date: 2020-09-27 14:34
 **/
public class NIOServer {

    static class EchoClientHandle implements Runnable {
        // 客户端
        private SocketChannel clientChannel;
        // 循环结束标记
        private boolean flag = true;

        public EchoClientHandle(SocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(NIOUtils.BUFFER_SIZE);
            try {
                while (this.flag) {
                    byteBuffer.clear();
                    int read = this.clientChannel.read(byteBuffer);
                    String msg = new String(byteBuffer.array(), 0, read).trim();
                    // 回应信息
                    String outMsg = "【Echo】" + msg + "\n";
                    if (NIOUtils.STOP.equals(msg)) {
                        outMsg = "会话结束，下次再见！";
                        this.flag = false;
                    }
                    byteBuffer.clear();
                    // 回传信息放入缓冲区
                    byteBuffer.put(outMsg.getBytes());
                    // byteBuffer从写模式变成读模式
                    byteBuffer.flip();
                    // 回传信息
                    this.clientChannel.write(byteBuffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public static void main(String[] args) throws IOException {
        // 为了性能问题及响应时间，设置固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        // NIO基于Channel控制，所以有Selector管理所有的Channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 设置监听端口
        serverSocketChannel.bind(new InetSocketAddress(NIOUtils.PORT));
        // 设置Selector管理所有Channel
        Selector selector = Selector.open();
        // 注册并设置连接时处理
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务启动成功，监听端口为：" + NIOUtils.PORT);
        // NIO使用轮询，当有请求连接时，则启动一个线程
        int keySelect = 0;
        while ((keySelect = selector.select()) > 0) {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                //  如果是连接的
                if (next.isAcceptable()) {
                    SocketChannel accept = serverSocketChannel.accept();
                    if (accept != null) {
                        executorService.submit(new EchoClientHandle(accept));
                    }
                    iterator.remove();
                }
            }
        }
        executorService.shutdown();
        serverSocketChannel.close();
    }

}
