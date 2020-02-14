## 基于Spring，Zookeeper，Netty实现的简易版Dubbo

- 实现了同步调用（支持超时时间配置），异步回调调用（基于Future和CountDownLatch实现）。

```JAVA
@RpcAutowired//实现了类似@Reference的注解，可以直接注入Rpc服务
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
  System.out.println(world);
}
```

- 实现了基于消费者端的负载均衡。目前使用轮询，后续支持包括：轮询、随机、LRU等；

- 实现了类似Dubbo的@Reference注解及@Service注解。

```JAVA
@RpcAutowired
HelloService helloService;//在生产方只需这样注入服务即可

@RpcService(HelloService.class)//在消费者端只需加入这个注解即可
public class HelloServiceImpl implements HelloService
```

### quckStart

1. 下载zookeeper并启动在默认端口。
2. 启动server模块中的RpcBootstrap中的run方法即可
3. 启动client模块中ServiceTest中的test方法即可测试。

Ref：

https://github.com/xuxueli/xxl-rpc

https://github.com/luxiaoxun/NettyRpc

https://github.com/tang-jie/NettyRPC

