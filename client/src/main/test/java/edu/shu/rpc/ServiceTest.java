package edu.shu.rpc;

import edu.shu.annotation.RpcAutowired;
import edu.shu.service.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author AppleLiang
 * @date 2019/9/16 21:19
 * @describtion
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class ServiceTest {
//    @Autowired
//    private RpcClient rpcClient;
    @RpcAutowired
    HelloService helloService;

    @Test
    public void helloTest1() {
        String result = helloService.hello("World");
        System.out.println("已发送请求");
        System.out.println(result);
    }

//    @Test
//    public void helloTest2() {
//        PersonService personService = rpcClient.create(PersonService.class);
//        List<Person> result = personService.GetTestPerson("World",1);
//        System.out.println(result);
//    }

//    @Test
//    public void helloFutureTest2() throws ExecutionException, InterruptedException {
//        IAsyncObjectProxy helloService = rpcClient.createAsync(HelloService.class);
//        Person person = new Person("Yong", "Huang");
//        RPCFuture result = helloService.call("hello", person);
//        System.out.println("已发送请求");
//        System.out.println((String) result.get());
//    }
}
