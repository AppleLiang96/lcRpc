package edu.shu.rpc;


import edu.shu.RpcService;
import edu.shu.common.AsyncInvokeFuture;
import edu.shu.common.RpcResponse;
import edu.shu.service.HelloService;

/**
 * @author AppleLiang
 * @date 2019/9/14 19:48
 * @describtion
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello" +name;
    }

    @Override
    public AsyncInvokeFuture asyncHello(String name) {
        AsyncInvokeFuture future = new AsyncInvokeFuture();
        future.response = new RpcResponse();
        future.response.setResult("async Hello "+ name);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return future;
    }
}
