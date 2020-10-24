package com.lcjing.netty.rpc.api;

/**
 * api: 主要用来定义对外开放的功能与服务接口
 *
 * @author lcjing
 * @date 2020/10/19
 */
public interface IRpcService {

    /**
     * 加
     */
    int add(int a, int b);

    /**
     * 减
     */
    int sub(int a, int b);

    /**
     * 乘
     */
    int mult(int a, int b);

    /**
     * 除
     */
    int div(int a, int b);

}
