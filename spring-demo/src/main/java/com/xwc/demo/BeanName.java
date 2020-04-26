package com.xwc.demo;

import jdk.nashorn.internal.objects.annotations.Constructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 作者：徐卫超（cc）
 * 时间： 2020/3/17 20:38
 * 描述： 看这个Bean能否被Spring扫描到
 */
@Component("beanName")
public class BeanName {

	public BeanName() {
	}

	public BeanName( TestSpring testSpring){
		System.out.println("TestSpring");
	}

}
