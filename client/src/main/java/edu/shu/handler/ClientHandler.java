package edu.shu.handler;

import edu.shu.async.AsyncInvokeFuture;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author liang
 * @create 2020/2/14 11:18 上午
 */
public class ClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements RpcHandler{
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private static final ConcurrentHashMap<String, AsyncInvokeFuture> invokeMap = new ConcurrentHashMap<>();

    private volatile Channel channel;
    private SocketAddress address;

    public Channel getChannel() {
        return channel;
    }

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
        AsyncInvokeFuture asyncInvokeFuture = invokeMap.remove(rpcResponse.getSpanId());
        asyncInvokeFuture.response = rpcResponse;
        asyncInvokeFuture.countDownLatch.countDown();
    }

    @Override
    public AsyncInvokeFuture syncSend(RpcRequest request) {
        AsyncInvokeFuture future = new AsyncInvokeFuture();
        invokeMap.put(request.getSpanId(), future);
        channel.writeAndFlush(request);
        logger.info("客户端已发送请求：{}", request);
        try {
            future.get(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
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
