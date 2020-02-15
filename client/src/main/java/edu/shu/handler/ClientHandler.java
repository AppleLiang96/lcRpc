package edu.shu.handler;

import edu.shu.common.AsyncInvokeFuture;
import edu.shu.common.RpcRequest;
import edu.shu.common.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.*;

/**
 * @author liang
 * @create 2020/2/14 11:18 上午
 */
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements RpcHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private static final ConcurrentHashMap<String, AsyncInvokeFuture> invokeMap = new ConcurrentHashMap<>();

    private volatile Channel channel;
    private SocketAddress address;

    public Channel getChannel() {
        return channel;
    }

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public void setAddress(SocketAddress address) {
        this.address = address;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.address = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        logger.info("收到来自服务器的响应, rpcResponse:{}", rpcResponse);
        Object result = rpcResponse.getResult();
        if (result instanceof AsyncInvokeFuture){
            rpcResponse.setResult(((AsyncInvokeFuture) result).response.getResult());
        }
        AsyncInvokeFuture asyncInvokeFuture = invokeMap.remove(rpcResponse.getSpanId());
        asyncInvokeFuture.response = rpcResponse;
        runCallBack(asyncInvokeFuture);
        asyncInvokeFuture.countDownLatch.countDown();
    }

    private void runCallBack(AsyncInvokeFuture asyncInvokeFuture) {
        if (asyncInvokeFuture.getCallBack() != null) {
            threadPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    if (asyncInvokeFuture.response.getException()==null){
                        asyncInvokeFuture.getCallBack().success(asyncInvokeFuture.response);
                    }else {
                        asyncInvokeFuture.getCallBack().fail(asyncInvokeFuture.response);
                    }
                }
            });
        }
    }

    @Override
    public AsyncInvokeFuture syncSend(RpcRequest request) {
        AsyncInvokeFuture future = new AsyncInvokeFuture();
        invokeMap.put(request.getSpanId(), future);
        channel.writeAndFlush(request);
        logger.info("客户端已发送请求：{}", request);
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        catch (TimeoutException e) {
//            e.printStackTrace();
//        }
        return future;
    }

    @Override
    public AsyncInvokeFuture asyncSend(RpcRequest request) {
        AsyncInvokeFuture future = new AsyncInvokeFuture();
        invokeMap.put(request.getSpanId(), future);
        channel.writeAndFlush(request);
        return future;
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
