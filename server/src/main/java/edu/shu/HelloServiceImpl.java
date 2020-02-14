package edu.shu;

import edu.shu.common.AsyncInvokeFuture;
import edu.shu.service.HelloService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello"+name;
    }

    @Override
    public AsyncInvokeFuture asyncHello(String name) {
        AsyncInvokeFuture future = new AsyncInvokeFuture();
        future.response.setResult("async Hello "+ name);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return future;
    }


}
