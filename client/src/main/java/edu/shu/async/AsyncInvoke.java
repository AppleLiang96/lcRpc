package edu.shu.async;

import edu.shu.common.AsyncInvokeFuture;
import edu.shu.common.RpcRequest;

/**
 * @author liang
 * @create 2020/2/14 10:09 上午
 */
public interface AsyncInvoke {

    AsyncInvokeFuture asyncInvoke(RpcRequest request);

}
