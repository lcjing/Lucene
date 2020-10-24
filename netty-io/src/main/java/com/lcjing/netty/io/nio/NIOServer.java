package com.lcjing.netty.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 同步非阻塞IO模型---NIO服务端
 *
 * @author lcjing
 * @date 2020/10/15
 */
public class NIOServer {

    private int port = 8080;
    //轮询器 Selector
    private Selector selector;
    //缓冲区 Buffer
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    //初始化完毕

    public NIOServer(int port) {
        try {
            this.port = port;
            ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(this.port));
            // 设置为非阻塞式: BIO 升级版本 NIO, 为了兼容BIO, NIO模型默认是采用阻塞式
            server.configureBlocking(false);
            selector = Selector.open();
            // 可以开始接收客户端的连接了
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        System.out.println("NIO服务已启动，监听端口是：" + this.port);
        try {
            //轮询主线程
            while (true) {
                selector.select();
                //每次都拿到所有事件
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                //不断地迭代，就叫轮询
                //同步体现在这里，因为每次只能拿一个key，每次只能处理一种状态
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    //每一个key代表一种状态
                    //数据就绪、数据可读、数据可写 等
                    process(key);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每一次轮询就是调用一次process方法，而每一次调用，只能干一件事
     * 在同一时间点，只能干一件事
     *
     * @param key 轮询状态
     * @throws IOException
     */
    private void process(SelectionKey key) throws IOException {
        //针对于每一种状态给一个反应
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            //这个方法体现非阻塞，不管你数据有没有准备好
            //你给我一个状态和反馈
            SocketChannel channel = server.accept();
            //一定一定要记得设置为非阻塞
            channel.configureBlocking(false);
            //当数据准备就绪的时候，将状态改为可读
            channel.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            //key.channel 从多路复用器中拿到客户端的引用
            SocketChannel channel = (SocketChannel) key.channel();
            int len = channel.read(buffer);
            if (len > 0) {
                buffer.flip();
                String content = new String(buffer.array(), 0, len);
                key = channel.register(selector, SelectionKey.OP_WRITE);
                //在key上携带一个附件，一会再写出去
               //key.attach(content);
                System.out.println("读取到客户端内容：" + content);
            }
        } else if (key.isWritable()) {
            SocketChannel channel = (SocketChannel) key.channel();

            //String content = (String) key.attachment();
            channel.write(ByteBuffer.wrap(("客户端, 你好, 我已经收到你的信息!").getBytes()));

            channel.close();
        }
    }

    public static void main(String[] args) {
        new NIOServer(8080).listen();
    }


}
