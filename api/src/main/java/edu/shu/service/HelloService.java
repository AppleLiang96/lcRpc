package edu.shu.service;

import edu.shu.common.AsyncInvokeFuture;

/**
 * @author liang
 * @create 2020/2/13 9:31 下午
 */
public interface HelloService {
    String hello(String name);

    AsyncInvokeFuture asyncHello(String name);
}
