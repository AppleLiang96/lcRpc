package edu.shu;

import edu.shu.common.RpcContext;
import edu.shu.common.RpcRequest;
import edu.shu.common.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

/**
 * @author liang
 * @create 2020/2/14 1:17 下午
 */
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);


    private Map<String, Object> handlerMap;

    public ServerHandler(Map<String, Object> handlerMap){
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setSpanId(request.getSpanId());
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Exception e) {
            response.setException(e.toString());
            logger.error("RPCserver执行方法时出错：{}", e);
        }
        channelHandlerContext.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                logger.info("已对requestId:{}的请求", request.toString());
                logger.info("请求回应response{}", response.toString());
            }
        });
    }

    private Object handle(RpcRequest request) throws InvocationTargetException {
        Object invoker = handlerMap.get(request.getClassName());
        buildRpcContext(request, invoker);
        if (invoker==null){
            logger.error("request请求的服务没有注册！");
            return null;
        }
        Class<?>   serviceClass   = invoker.getClass();
        String     methodName     = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[]   parameters     = request.getParameters();
        System.out.println(request);
        logger.info("收到的请求要求的服务名为{}",serviceClass.getName());
        logger.info("收到的请求要求的方法名为{}",methodName);
        for (int i = 0; i < parameterTypes.length; ++i) {
            logger.info("收到的请求要求的参数类别为:{}",parameterTypes[i].getName());
        }
        for (int i = 0; i < parameters.length; ++i) {
            logger.info("收到的请求要求的参数名为:{}",parameters[i].toString());
        }
        FastClass serviceFastClass = FastClass.create(serviceClass);
        int methodIndex = serviceFastClass.getIndex(methodName, parameterTypes);
        return serviceFastClass.invoke(methodIndex, invoker, parameters);
    }

    private void buildRpcContext(RpcRequest request, Object invoker) {
        RpcContext context = RpcContext.getContext();
        if (context.getCurThreadId()==null){
            context.setCurThreadId(UUID.randomUUID().toString());
        }
        context.setTraceId(request.getTrace().getTraceId());
        logger.info("链路跟踪, RpcContext:{}", context);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server caught exception", cause);
        ctx.close();
    }
}
