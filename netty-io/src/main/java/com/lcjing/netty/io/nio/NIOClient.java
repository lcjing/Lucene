package com.lcjing.netty.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class NIOClient {
    public static void main(String[] args) throws IOException {
        //1. 获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));
        //2. 切换非阻塞模式
        sChannel.configureBlocking(false);

        //3. 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //发送数据
        String name = UUID.randomUUID().toString();
        buf.put(name.getBytes());
        buf.flip();
        sChannel.write(buf);
        buf.clear();
        //5. 关闭通道
        sChannel.close();
    }



}
