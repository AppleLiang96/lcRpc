package edu.shu.common;


import lombok.Data;

import java.io.Serializable;

/**
 * @author liang
 * @create 2020/2/13 8:28 下午
 */
@Data
public class RpcRequest implements Serializable {
    //用于链路跟踪，一次业务请求生成一个trace
    private Trace trace;
    private String spanId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
