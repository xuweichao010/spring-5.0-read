package com.xwc.proxy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Proxy;

/**
 * 作者：徐卫超（cc）
 * 时间： 2020/3/19 21:22
 * 描述：
 */
public class MyFactoryBean implements FactoryBean {
	private Class<?> clazz;

	public MyFactoryBean(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Object getObject() throws Exception {
		return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{this.clazz}, new MyInvocationHandler());
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}
}
