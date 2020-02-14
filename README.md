## 基于Spring，Zookeeper，Netty实现的简易RPC框架

实现了同步调用，异步调用，异步回调调用。

实现了基于消费者端的负载均衡。提供丰富的负载均衡策略（todo，可以考虑策略模式），包括：轮询、随机、LRU、LFU、一致性HASH等；

实现了类似Dubbo的@Reference注解及@Service注解。

