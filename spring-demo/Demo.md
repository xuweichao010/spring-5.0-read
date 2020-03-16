# Spring 核心解读
## BeanPostProcess
BeanPostProcessor是Spring框架的提供的一个扩展类，通过实现BeanPostProcessor接口，程序员就可插手bean实例化的过程.
```java
public interface BeanPostProcessor {
    /**
     * 在bean初始化之前执行
     */
    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    /**
     * 初始化之后
     */
    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
```

## BeanFactoryPostProcessor
BeanFactoryPostProcessor是Spring框架提供的一个扩展点， 实现该接口，可以在spring的bean创建之前修改bean的定义属性。
Spring允许BeanFactoryPostProcessor在容器实例化任何其它bean之前读取配置元数据，可以根据需要进行修改，例如可以把bean
的scope从singleton改为prototype，也可以把property的值给修改掉。可以同时配置多个BeanFactoryPostProcessor，并通过
设置'order'属性来控制各个BeanFactoryPostProcessor的执行次序。BeanFactoryPostProcessor是在spring容器加载了bean
的定义文件之后，在bean实例化之前执行的.
