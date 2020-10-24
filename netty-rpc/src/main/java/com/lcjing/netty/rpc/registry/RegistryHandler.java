package com.lcjing.netty.rpc.registry;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.lcjing.netty.rpc.protocol.InvokerProtocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * registry：主要负责保存所有可用的服务名称和服务地址
 *
 * @author lcjing
 * @date 2020/10/19
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    /**
     * 用保存所有可用的服务： key: 服务类接口名   value: 服务类实例
     */
    public static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<String, Object>();

    /**
     * 保存所有相关的服务类
     */
    private List<String> classNames = new ArrayList<String>();

    public RegistryHandler() {
        //完成递归扫描
        scannerClass("com.lcjing.netty.rpc.provider");
        doRegister();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        InvokerProtocol request = (InvokerProtocol) msg;

        //当客户端建立连接时，需要从自定义协议中获取信息，拿到具体的服务和实参
        //使用反射调用
        if (registryMap.containsKey(request.getClassName())) {
            Object clazz = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParams());
            result = method.invoke(clazz, request.getValues());
        }
        ctx.writeAndFlush()
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * 扫描所有的服务类
     *
     * @param packageName 扫描的包名
     */
    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            //如果是一个文件夹，继续递归
            if (file.isDirectory()) {
                scannerClass(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    /**
     * 完成注册
     */
    private void doRegister() {
        if (classNames.size() == 0) {
            return;
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName(), clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
