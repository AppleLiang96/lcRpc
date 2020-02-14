package edu.shu.common;

/**
 * @author liang
 * @create 2020/2/14 7:06 下午
 */
public interface AsyncCallBack {
    void success(RpcResponse response);
    void fail(RpcResponse response);
}
