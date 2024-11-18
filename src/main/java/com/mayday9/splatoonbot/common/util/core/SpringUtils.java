package com.mayday9.splatoonbot.common.util.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * bean名称
     *
     * @param name 要查询的bean的名称
     * @return true：包含
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 通过对象名称获取spring bean对象
     *
     * @param name bean的名称
     * @return 对象
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 返回与给定对象类型唯一匹配的bean实例(如果有)
     *
     * @param requiredType bean 必须匹配的类型； 可以是接口或超
     * @return 匹配所需类型的单个 bean 的实例
     */
    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 返回与给定对象类型（包括子类）匹配的 bean 实例
     *
     * @param type 要匹配的类或接口
     * @return Map<bean名称, bean实例>
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return applicationContext.getBeansOfType(type);
    }

    public static <T> T getBean(String beanName, Class<T> beanClass) {
        return applicationContext.getBean(beanName, beanClass);
    }

    public static <T> T createBean(Class<T> beanClass) {
        return applicationContext.getAutowireCapableBeanFactory().createBean(beanClass);
    }


    public static <T> List<T> getBeans(Class<T> beanClass) {
        return new ArrayList<>(applicationContext.getBeansOfType(beanClass).values());
    }

    @SuppressWarnings("unchecked")
    public static <T> T initializeBean(T bean) {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBean(bean);
        return (T) beanFactory.initializeBean(bean, bean.getClass().getName());
    }

    public static void registerSingletonBean(String beanName, Class beanClass) {
        GenericBeanDefinition definition = new GenericBeanDefinition();
        definition.setBeanClass(beanClass);
        definition.setScope(BeanDefinition.SCOPE_SINGLETON);
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
        registry.registerBeanDefinition(beanName, definition);
    }

    public static void registerSingletonBean(String beanName, Object bean) {
        SingletonBeanRegistry registry = (SingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();
        registry.registerSingleton(beanName, bean);
    }

    public static Object invokeMethod(Class<?> requiredType, String methodName, Object[] params) {
        Object service = getBean(requiredType);
        Class<? extends Object>[] paramClass = null;
        if (params != null) {
            int paramsLength = params.length;
            paramClass = new Class[paramsLength];
            for (int i = 0; i < paramsLength; i++) {
                paramClass[i] = params[i].getClass();
            }
        }
        // 找到方法
        Method method = ReflectionUtils.findMethod(service.getClass(), methodName, paramClass);
        // 执行方法
        return ReflectionUtils.invokeMethod(method, service, params);
    }

    /**
     * 根据bean名称、方法名反射调用spring bean中的方法
     *
     * @param serviceName 服务名
     * @param methodName  方法名
     * @param params      参数
     * @return 对象
     */
    public static Object invokeMethod(String serviceName, String methodName, Object[] params) {
        Object service = getBean(serviceName);
        Class<? extends Object>[] paramClass = null;
        if (params != null) {
            int paramsLength = params.length;
            paramClass = new Class[paramsLength];
            for (int i = 0; i < paramsLength; i++) {
                paramClass[i] = params[i].getClass();
            }
        }
        // 找到方法
        Method method = ReflectionUtils.findMethod(service.getClass(), methodName, paramClass);
        // 执行方法
        return ReflectionUtils.invokeMethod(method, service, params);
    }
}
