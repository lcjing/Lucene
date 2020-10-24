package com.lcjing.netty.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * protocol：主要定义自定义传输协议的内容
 *
 * @author lcjing
 * @date 2020/10/19
 */
@Data
public class InvokerProtocol implements Serializable {
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 参数类型：形参列表
     */
    private Class<?>[] params;
    /**
     * 实参列表
     */
    private Object[] values;

}
