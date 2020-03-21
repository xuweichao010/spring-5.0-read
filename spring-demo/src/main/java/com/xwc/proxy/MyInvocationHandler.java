package com.xwc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 作者：徐卫超（cc）
 * 时间： 2020/3/19 21:20
 * 描述：
 */
public class MyInvocationHandler implements InvocationHandler {
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("代理被执行");
		return null;
	}
}
