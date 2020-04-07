package com.xwc.demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 作者：徐卫超（cc）
 * 时间： 2020/3/15 20:46
 * 描述：
 */
@Configuration
@ComponentScan("com.xwc")
public class TestSpring {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestSpring.class);
	}
}
/**
 1.通过 AnnotationConfigApplicationContext#AnnotationConfigApplicationContext(java.lang.Class[])方法创建
 	1.1在这个方法中调用this()默认的无参构造->AnnotationConfigApplicationContext#AnnotationConfigApplicationContext()
 		1.1.1 无参构造方法中默认调用super()方法->GenericApplicationContext#GenericApplicationContext()
 			1.1.1.1 在这个无参构造方法中创建了一个new DefaultListableBeanFactory()对象(后面的不在是重点)并调用super方法忽略几个接口BeanNameAware,BeanFactoryAware,BeanClassLoaderAware
 		1.1.2 创建一个被注解的BeanDefinition读取器 AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(BeanDefinitionRegistry).这里的 BeanDefinitionRegistry == AnnotationConfigApplicationContext
 			  那么就证明 AnnotationConfigApplicationContext <-e- GenericApplicationContext <-i- BeanDefinitionRegistry 在这个地方BeanDefinitionRegistry有什么用？ 通过这个接口的名称可以知道 他是BeanDefinition注册类
 			  那么就证明 BeanDefinitionRegistry其实就是 AnnotationConfigApplicationContext 这个对象。
 			1.1.2.1 AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader(BeanDefinitionRegistry, Environment) 继续调用这个两个参数的构造方法
 				1.1.2.1.1 调用 AnnotationConfigUtils#registerAnnotationConfigProcessors(BeanDefinitionRegistry) 一个空壳方法
 					1.1.2.1.1.1 AnnotationConfigUtils#registerAnnotationConfigProcessors(BeanDefinitionRegistry, Object) 调用这个方法向Spring容器中注入一个注解配置处理器(Spring 核心对象之一)
 						1.1.2.1.1.1.1 获取 DefaultListableBeanFactory对象 这个对象在 @See 1.1.1.1 中创建出来的。
 						1.1.2.1.1.1.2 如果 DefaultListableBeanFactory != null && !(beanFactory.getDependencyComparator() instanceof AnnotationAwareOrderComparator)
 									  则创建一个 AnnotationAwareOrderComparator（主要能解析@Order注解和@Priority）对象并添加到 DefaultListableBeanFactory 中
 						1.1.2.1.1.1.3 如果 DefaultListableBeanFactory != null && !(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)
 								 	  则创建一个ContextAnnotationAutowireCandidateResolver（提供处理延迟加载的功能）添加到 DefaultListableBeanFactory 中
 						1.1.2.1.1.1.4 如果注册器中不包含 CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME（internalConfigurationAnnotationProcessor）这个对象 则往注册器中添加一个
 									  ConfigurationClassPostProcessor对象并且名字为internalConfigurationAnnotationProcessor 需要注意的是 ConfigurationClassPostProcessor <-i-
 									  BeanDefinitionRegistryPostProcessor <-e- BeanFactoryPostProcessor
 		1.2.3 创建一个 ClassPathBeanDefinitionScanner#ClassPathBeanDefinitionScanner(BeanDefinitionRegistry) bean的定义扫描器（真正的bean的扫描不在这里）
 	1.2 调用 AnnotationConfigApplicationContext#register(java.lang.Class[])方法
 		1.2.1 调用在1.1.2创建的读取器并调用他的 AnnotatedBeanDefinitionReader#register(Class[]) 注册一个被注解的Class数组 这里只用我们在1中传入的配置对象。
 			1.2.1.1 通过数组循环调用 AnnotatedBeanDefinitionReader#registerBean(Class) 这是一个空壳方法
 				1.2.1.1.1 继续调用 AnnotatedBeanDefinitionReader#doRegisterBean(java.lang.Class, Supplier, String, Class[], BeanDefinitionCustomizer...)
 					1.2.1.1.1.1 根据指定的对象创建一个 AnnotatedGenericBeanDefinition#AnnotatedGenericBeanDefinition(Class)回去收集这个类上注解的元信息
 					1.2.1.1.1.2 判断这个类是否需要跳过 主要判断是否是被注解了Conditional注解
 					1.2.1.1.1.3 调用 ScopeMetadataResolver#resolveScopeMetadata(BeanDefinition) 处理Bean的Scope注解（scopeMetadataResolver == AnnotationScopeMetadataResolver）
 					1.2.1.1.1.4 如果没用定义Bean的名字则创建一个bean的名字
 					1.2.1.1.1.5 调用 AnnotationConfigUtils#processCommonDefinitionAnnotations(AnnotatedBeanDefinition)方法处理类当中的 Lazy DependsOn Primary Role等等注解
 								会把处理的信息写入到AnnotatedBeanDefinition这个对象中.
 					1.2.1.1.1.6 处理 qualifiers的情况
 								Spring 加载初始化配置类的时候 qualifiers = null,如果在向容器注册注解Bean定义时，使用了额外的限定符注解则解析,关于Qualifier和Primary主要涉及到spring的自动装配,
 								这里需要注意的byName和qualifiers这个变量是Annotation类型的数组，里面存不仅仅是Qualifier注解,理论上里面里面存的是一切注解，所以可以看到下面的代码spring去循环了这个数组,
 								然后依次判断了注解当中是否包含了Primary，是否包含了Lazy
 					1.2.1.1.1.6 应用Scope代理模式（Spring MVC使用）
 					1.2.1.1.1.6 BeanDefinitionReaderUtils#registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry) 调用工具类注册bean的定义信息。
 	1.3 调用 AbstractApplicationContext#refresh()方法 正式开始spring的声明周期
 		1.3.1  AbstractApplicationContext#prepareRefresh()  设置启动时间和激活容器标志位并执行初始化的属性源
 		1.3.2 调用 AbstractApplicationContext#obtainFreshBeanFactory() 获取ConfigurableListableBeanFactory（DefaultListableBeanFactory）对象  这个对象是在 @See1.1.1.1 创建出来的
 		1.3.3 调用 AbstractApplicationContext#prepareBeanFactory(ConfigurableListableBeanFactory) 准备工厂所需要的上下文信息。比如：ClassLoader、
 			  StandardBeanExpressionResolver(Bean表达式解析器)、AwarePostProcessors（ApplicationContextAwareProcessor）回调和环境信息等。
 		1.3.4 调用 AbstractApplicationContext#postProcessBeanFactory(ConfigurableListableBeanFactory) 无任何功能。
 		1.3.5 调用 AbstractApplicationContext#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory) 方法执行spring环境中注入的BeanFactoryPostProcessors和自定义的BeanFactoryPostProcessors







 */
