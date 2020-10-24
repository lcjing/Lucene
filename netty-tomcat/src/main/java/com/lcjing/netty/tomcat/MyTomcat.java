package com.lcjing.netty.tomcat;


import com.lcjing.netty.tomcat.http.MyRequest;
import com.lcjing.netty.tomcat.http.MyResponse;
import com.lcjing.netty.tomcat.http.MyServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Netty 实现一个简单的 Tomcat 服务器
 *
 * @author lcjing
 * @date 2020/10/16
 */

public class MyTomcat {
    private int port = 8080;
    /**
     * 用于保存 url 与 servlet 的对应关系
     */
    private Map<String, MyServlet> servletMapping = new HashMap<String, MyServlet>();

    private Properties webxml = new Properties();

    /**
     * 加载web.xml文件,同时初始化 ServletMapping 对象
     */
    private void init() {
        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            // web.properties 配置了 url 和 servlet 的关系, 代替 web.xml
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");
            webxml.load(fis);

            for (Object k : webxml.keySet()) {
                String key = k.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");
                    MyServlet obj = (MyServlet) Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动 tomcat 服务
     */
    public void start() {
        init();

        //Netty封装了NIO-->Reactor模型：Boss/worker
        // Boss线程
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // Worker线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // Netty服务
            //ServerBootstrap----->ServerSocketChannel
            ServerBootstrap server = new ServerBootstrap();
            // 链路式编程
            server.group(bossGroup, workerGroup)
                    // 主线程处理类,看到这样的写法，底层就是用反射
                    .channel(NioServerSocketChannel.class)
                    // 子线程处理类 , Handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 客户端初始化处理
                        protected void initChannel(SocketChannel client) throws Exception {
                            // 无锁化串行编程
                            //Netty对HTTP协议的封装，顺序有要求
                            // HttpResponseEncoder 编码器
                            client.pipeline().addLast(new HttpResponseEncoder());
                            // HttpRequestDecoder 解码器
                            client.pipeline().addLast(new HttpRequestDecoder());
                            // 业务逻辑处理
                            client.pipeline().addLast(new MyTomcatHandler());
                        }

                    })
                    // 针对主线程的配置 分配线程最大数量 128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 针对子线程的配置 保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 启动服务器
            ChannelFuture f = server.bind(port).sync();
            System.out.println("My Tomcat 已启动，监听的端口是：" + port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 具体的业务逻辑处理
     */
    public class MyTomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;

                // 转交给我们自己的request实现
                MyRequest request = new MyRequest(ctx, req);
                // 转交给我们自己的response实现
                MyResponse response = new MyResponse(ctx, req);

                // 实际业务处理
                String url = request.getUrl();
                if (servletMapping.containsKey(url)) {
                    System.out.println("当前请求 url: " + url);
                    // servlet 响应处理
                    servletMapping.get(url).service(request, response);
                } else {
                    response.write("404 - Not Found");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }
    }

    public static void main(String[] args) {
        new MyTomcat().start();
    }

}
