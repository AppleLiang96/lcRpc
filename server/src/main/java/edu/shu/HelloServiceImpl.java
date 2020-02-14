package edu.shu;

import edu.shu.service.HelloService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello"+name;
    }
}
