package com.xwc.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 作者：徐卫超（cc）
 * 时间： 2020/3/17 20:38
 * 描述： 看这个Bean能否被Spring扫描到
 */
@Component("beanName")
public class BeanName {
	@Autowired
	private AutowiredBean autowiredBean;
	private TestSpring testSpring;


	public BeanName(TestSpring testSpring) {
		this.testSpring = testSpring;
		System.out.println("TestSpring");
	}

}
