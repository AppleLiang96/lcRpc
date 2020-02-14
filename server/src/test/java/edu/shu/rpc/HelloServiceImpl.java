package edu.shu.rpc;


import edu.shu.RpcService;
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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello" +name;
    }

}
