package com.xwc.proxy;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

/**
 * 作者：徐卫超（cc）
 * 时间： 2020/3/19 21:12
 * 描述：
 */
@Import(MyImportBeanDefinitionRegistrar.class)
public class SpringProxy {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringProxy.class);
		MyProxyInterface myProxyInterface = (MyProxyInterface) applicationContext.getBean("MyProxyInterface");
		myProxyInterface.get();

	}
}
