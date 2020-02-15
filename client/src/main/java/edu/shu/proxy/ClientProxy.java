package edu.shu.proxy;

import edu.shu.async.AsyncInvoke;
import edu.shu.common.AsyncInvokeFuture;
import edu.shu.common.RpcContext;
import edu.shu.common.RpcRequest;
import edu.shu.common.Trace;
import edu.shu.handler.ClientHandler;
import edu.shu.zookeeper.ConnectManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * @author liang
 * @create 2020/2/13 9:37 下午
 */
public class ClientProxy<T> implements InvocationHandler, AsyncInvoke {

    private static final Logger logger = LoggerFactory.getLogger(ClientProxy.class);

    private Class<T> clazz;

    public ClientProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object o = null;
        //如果是object中的方法就不要走rpc了
        if((o = invokeObjectMethod(proxy, method, args))!=null){
            return o;
        }
        RpcRequest request = buildRequest(method, args);

        if (Future.class.isAssignableFrom(method.getReturnType())){
            return asyncInvoke(request);
        }
        ClientHandler clientHandler = ConnectManage.getInstance().chooseHandler();
        AsyncInvokeFuture rpcFuture = clientHandler.syncSend(request);
        if (rpcFuture.response.getException()!=null){
            throw new RuntimeException("rpc调用失败，原因："+ rpcFuture.response.getException());
        }
        return rpcFuture.response.getResult();
    }

    private RpcRequest buildRequest(Method method, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setSpanId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        //链路跟踪设计
        RpcContext context = RpcContext.getContext();
        Trace trace = new Trace();
        request.setTrace(trace);
        if (context.getTraceId()!=null){
            trace.setTraceId(context.getTraceId());
        }else {
            trace.setTraceId(UUID.randomUUID().toString());
            context.setTraceId(trace.getTraceId());
        }
        if (context.getCurThreadId()==null){
            context.setCurThreadId(UUID.randomUUID().toString());
        }
        trace.setParentId(context.getCurThreadId());
        logger.info("链路跟踪：trace:{}, RpcContext:{}", trace, context);
        return request;
    }

    private Object invokeObjectMethod(Object proxy, Method method, Object[] args) {
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
        return null;
    }

    /**
     * 异步调用方法
     * @param request
     * @return
     */
    @Override
    public AsyncInvokeFuture asyncInvoke(RpcRequest request) {
        ClientHandler clientHandler = ConnectManage.getInstance().chooseHandler();
        AsyncInvokeFuture rpcFuture = clientHandler.asyncSend(request);
        return rpcFuture;
    }
}
