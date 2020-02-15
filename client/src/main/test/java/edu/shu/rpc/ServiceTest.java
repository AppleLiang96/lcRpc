package edu.shu.rpc;

import edu.shu.annotation.RpcAutowired;
import edu.shu.common.AsyncCallBack;
import edu.shu.common.AsyncInvokeFuture;
import edu.shu.common.RpcResponse;
import edu.shu.service.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ExecutionException;

/**
 * @author AppleLiang
 * @date 2019/9/16 21:19
 * @describtion
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class ServiceTest {

    @RpcAutowired
    HelloService helloService;

    /**
     * 异步调用+callback支持
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void asyncTest() throws ExecutionException, InterruptedException {
        AsyncInvokeFuture future = helloService.asyncHello("World");
        future.addCallBack(new AsyncCallBack() {
            @Override
            public void success(RpcResponse response) {
                System.out.println("执行成功");
            }

            @Override
            public void fail(RpcResponse response) {
                System.out.println("执行失败");
            }
        });
        System.out.println("已发送请求");
        System.out.println(future.get());
    }

    /**
     * 同步调用支持
     */
    @Test
    public void helloTest2() {
        String world = helloService.hello("World");
        String world2 = helloService.hello("World11");
        System.out.println(world);
        System.out.println(world2);
    }

}
