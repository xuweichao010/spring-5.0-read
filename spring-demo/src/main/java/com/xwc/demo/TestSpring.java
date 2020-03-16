package com.xwc.demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * 作者：徐卫超（cc）
 * 时间： 2020/3/15 20:46
 * 描述：
 */
@Configuration
public class TestSpring {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TestSpring.class);
	}
}
