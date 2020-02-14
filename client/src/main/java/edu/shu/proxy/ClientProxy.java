package edu.shu.proxy;

import edu.shu.async.AsyncInvoke;
import edu.shu.async.AsyncInvokeFuture;
import edu.shu.common.RpcRequest;
import edu.shu.handler.ClientHandler;
import edu.shu.zookeeper.ConnectManage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author liang
 * @create 2020/2/13 9:37 下午
 */
public class ClientProxy<T> implements InvocationHandler, AsyncInvoke {

    private Class<T> clazz;

    public ClientProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        RpcRequest request = new RpcRequest();
        request.setSpanId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        if (method.getName().startsWith("async")){
            return asyncInvoke(request);
        }
        ClientHandler clientHandler = ConnectManage.getInstance().chooseHandler();
        AsyncInvokeFuture rpcFuture = clientHandler.syncSend(request);
        if (rpcFuture.response.getException()!=null){
            throw new RuntimeException("rpc调用失败，原因："+ rpcFuture.response.getException());
        }
        return rpcFuture.response.getResult();
    }

    /**
     * 异步调用方法
     * @param request
     * @return
     */
    @Override
    public AsyncInvokeFuture asyncInvoke(RpcRequest request) {
        ClientHandler clientHandler = ConnectManage.getInstance().chooseHandler();
        AsyncInvokeFuture rpcFuture = clientHandler.syncSend(request);
        return rpcFuture;
    }
}
