package edu.shu.handler;

import edu.shu.async.AsyncInvokeFuture;
import edu.shu.common.RpcRequest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author liang
 * @create 2020/2/14 11:20 上午
 */
public interface RpcHandler {
    public AsyncInvokeFuture syncSend(RpcRequest request) throws InterruptedException, ExecutionException, TimeoutException;
    public AsyncInvokeFuture asyncSend(RpcRequest request);
}
