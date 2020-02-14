package edu.shu.annotation;

import edu.shu.proxy.ClientProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author liang
 * @create 2020/2/14 11:03 上午
 */
@Component
public class RpcServiceInjectProcessor implements BeanPostProcessor {

    @Autowired
    private ApplicationContext applicationContext;
    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        Class<?> curClass = o.getClass();
        Field[] fields = curClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcAutowired.class)){
                Class beforeClass= (Class) field.getGenericType();
                Object o2= Proxy.newProxyInstance(beforeClass.getClassLoader(), new Class<?>[]{beforeClass}, new ClientProxy<>(beforeClass));
                try {
                    field.setAccessible(true);
                    field.set(o, o2);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return o;
            }
        }
        return o;
    }
}
