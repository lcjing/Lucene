package com.lcjing.netty.rpc.consumer;

import com.lcjing.netty.rpc.api.IRpcService;
import com.lcjing.netty.rpc.api.IRpcHelloService;
import com.lcjing.netty.rpc.consumer.proxy.RpcProxy;
/**
 *
 * consumer: 客户端调用
 * @author lcjing
 * @date 2020/10/19
 */
public class RpcConsumer {
	
    public static void main(String [] args){  
        IRpcHelloService rpcHello = RpcProxy.create(IRpcHelloService.class);
        
        System.out.println(rpcHello.hello("Netty RPC"));

        IRpcService service = RpcProxy.create(IRpcService.class);
        
        System.out.println("8 + 2 = " + service.add(8, 2));
        System.out.println("8 - 2 = " + service.sub(8, 2));
        System.out.println("8 * 2 = " + service.mult(8, 2));
        System.out.println("8 / 2 = " + service.div(8, 2));
    }
    
}
