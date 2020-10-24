package com.lcjing.netty.rpc.provider;

import com.lcjing.netty.rpc.api.IRpcService;

/**
 * provider：实现对外提供的所有服务的具体功能
 *
 * @author lcjing
 * @date 2020/10/19
 */
public class RpcServiceImpl implements IRpcService {

    public int add(int a, int b) {
        return a + b;
    }

    public int sub(int a, int b) {
        return a - b;
    }

    public int mult(int a, int b) {
        return a * b;
    }

    public int div(int a, int b) {
        return a / b;
    }

}
