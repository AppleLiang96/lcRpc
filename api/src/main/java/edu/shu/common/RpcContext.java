package edu.shu.common;

import lombok.ToString;

/**
 * @author liang
 * @create 2020/2/15 12:40 下午
 */
@ToString
public class RpcContext {

    private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    protected RpcContext() {
    }

    public static RpcContext getContext() {
        return LOCAL.get();
    }

    private String curThreadId;
    private String traceId;

    public String getCurThreadId() {
        return curThreadId;
    }

    public void setCurThreadId(String curThreadId) {
        this.curThreadId = curThreadId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }



}
