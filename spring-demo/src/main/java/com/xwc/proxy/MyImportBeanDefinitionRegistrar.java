package com.xwc.proxy;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Proxy;

/**
 * 作者：徐卫超（cc）
 * 时间： 2020/3/19 21:13
 * 描述：
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		/**
		 * BeanDefinition
		 */

		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MyProxyInterface.class);
		GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanDefinitionBuilder.getBeanDefinition();
		beanDefinition.setBeanClass(MyFactoryBean.class);
		beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(MyProxyInterface.class.getName());
		registry.registerBeanDefinition(MyProxyInterface.class.getSimpleName(), beanDefinition);
	}
}
