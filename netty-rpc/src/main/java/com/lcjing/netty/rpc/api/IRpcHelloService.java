package com.lcjing.netty.rpc.api;

/**
 * api: 主要用来定义对外开放的功能与服务接口
 *
 * @author lcjing
 * @date 2020/10/19
 */
public interface IRpcHelloService {
    String hello(String name);
}  
