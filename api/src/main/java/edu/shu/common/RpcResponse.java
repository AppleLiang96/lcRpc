package edu.shu.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liang
 * @create 2020/2/13 9:19 下午
 */
@Data
public class RpcResponse implements Serializable {
    private Trace trace;
    private String spanId;
    private String exception;
    private Object result;
}
