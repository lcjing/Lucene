package com.lcjing.netty.rpc.provider;

import com.lcjing.netty.rpc.api.IRpcHelloService;

/**
 * provider：实现对外提供的所有服务的具体功能
 *
 * @author lcjing
 * @date 2020/10/19
 */
public class RpcHelloServiceImpl implements IRpcHelloService {

    public String hello(String name) {  
        return "Hello " + name + "!";  
    }  
  
}  
