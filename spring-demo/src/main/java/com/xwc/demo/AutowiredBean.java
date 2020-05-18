package com.xwc.demo;

import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * 作者：徐卫超（cc）
 * 时间： 2020/5/2 19:26
 * 描述：
 */
@Component
public class AutowiredBean {

	private String name = "AutowiredBean";
}
