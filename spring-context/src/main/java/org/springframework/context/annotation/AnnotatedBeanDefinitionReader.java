/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Convenient adapter for programmatic registration of annotated bean classes.
 * This is an alternative to {@link ClassPathBeanDefinitionScanner}, applying
 * the same resolution of annotations but for explicitly registered classes only.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @author Phillip Webb
 * @see AnnotationConfigApplicationContext#register
 * @since 3.0
 */
public class AnnotatedBeanDefinitionReader {

	private final BeanDefinitionRegistry registry;

	private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

	private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

	private ConditionEvaluator conditionEvaluator;


	/**
	 * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry.
	 * If the registry is {@link EnvironmentCapable}, e.g. is an {@code ApplicationContext},
	 * the {@link Environment} will be inherited, otherwise a new
	 * {@link StandardEnvironment} will be created and used.
	 * @param registry the {@code BeanFactory} to load bean definitions into,
	 * in the form of a {@code BeanDefinitionRegistry}
	 * @see #AnnotatedBeanDefinitionReader(BeanDefinitionRegistry, Environment)
	 * @see #setEnvironment(Environment)
	 */
	/**
	 * 这里的BeanDefinitionRegistry registry是通过在AnnotationConfigApplicationContext
	 * 的构造方法中传进来的this
	 * 由此说明AnnotationConfigApplicationContext是一个BeanDefinitionRegistry类型的类
	 * 何以证明我们可以看到AnnotationConfigApplicationContext的类关系：
	 * GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry
	 * <p>
	 * 看到他实现了BeanDefinitionRegistry证明上面的说法，那么BeanDefinitionRegistry的作用是什么呢？
	 * <p>
	 * BeanDefinitionRegistry 顾名思义就是BeanDefinition的注册器
	 * 由此可以得到结论 BeanDefinitionRegistry = AnnotationConfigApplicationContext
	 * 那么何为BeanDefinition呢？参考BeanDefinition的源码的注释
	 *
	 * @param registry
	 */
	public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
		//一般是由spring创建的 getOrCreateEnvironment(registry);
		//spring核心注入
		this(registry, getOrCreateEnvironment(registry));
	}

	/**
	 * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry and using
	 * the given {@link Environment}.
	 *
	 * @param registry    the {@code BeanFactory} to load bean definitions into,
	 *                    in the form of a {@code BeanDefinitionRegistry}
	 * @param environment the {@code Environment} to use when evaluating bean definition
	 *                    profiles.
	 * @since 3.1
	 */
	/**
	 *
	 */
	public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		Assert.notNull(environment, "Environment must not be null");
		this.registry = registry;
		this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
		//向Spring中注入注解配置处理器 重点
		AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
	}


	/**
	 * Return the BeanDefinitionRegistry that this scanner operates on.
	 */
	public final BeanDefinitionRegistry getRegistry() {
		return this.registry;
	}

	/**
	 * Set the Environment to use when evaluating whether
	 * {@link Conditional @Conditional}-annotated component classes should be registered.
	 * <p>The default is a {@link StandardEnvironment}.
	 *
	 * @see #registerBean(Class, String, Class...)
	 */
	public void setEnvironment(Environment environment) {
		this.conditionEvaluator = new ConditionEvaluator(this.registry, environment, null);
	}

	/**
	 * Set the BeanNameGenerator to use for detected bean classes.
	 * <p>The default is a {@link AnnotationBeanNameGenerator}.
	 */
	public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new AnnotationBeanNameGenerator());
	}

	/**
	 * Set the ScopeMetadataResolver to use for detected bean classes.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 */
	public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
		this.scopeMetadataResolver =
				(scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
	}


	/**
	 * Register one or more annotated classes to be processed.
	 * <p>Calls to {@code register} are idempotent; adding the same
	 * annotated class more than once has no additional effect.
	 *
	 * @param annotatedClasses one or more annotated classes,
	 *                         e.g. {@link Configuration @Configuration} classes
	 */
	public void register(Class<?>... annotatedClasses) {
		for (Class<?> annotatedClass : annotatedClasses) {
			registerBean(annotatedClass);
		}
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 *
	 * @param annotatedClass the class of the bean
	 */
	public void registerBean(Class<?> annotatedClass) {
		doRegisterBean(annotatedClass, null, null, null);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, using the given supplier for obtaining a new
	 * instance (possibly declared as a lambda expression or method reference).
	 *
	 * @param annotatedClass   the class of the bean
	 * @param instanceSupplier a callback for creating an instance of the bean
	 *                         (may be {@code null})
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> annotatedClass, @Nullable Supplier<T> instanceSupplier) {
		doRegisterBean(annotatedClass, instanceSupplier, null, null);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations, using the given supplier for obtaining a new
	 * instance (possibly declared as a lambda expression or method reference).
	 *
	 * @param annotatedClass   the class of the bean
	 * @param name             an explicit name for the bean
	 * @param instanceSupplier a callback for creating an instance of the bean
	 *                         (may be {@code null})
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> annotatedClass, String name, @Nullable Supplier<T> instanceSupplier) {
		doRegisterBean(annotatedClass, instanceSupplier, name, null);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 *
	 * @param annotatedClass the class of the bean
	 * @param qualifiers     specific qualifier annotations to consider,
	 *                       in addition to qualifiers at the bean class level
	 */
	@SuppressWarnings("unchecked")
	public void registerBean(Class<?> annotatedClass, Class<? extends Annotation>... qualifiers) {
		doRegisterBean(annotatedClass, null, null, qualifiers);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 *
	 * @param annotatedClass the class of the bean
	 * @param name           an explicit name for the bean
	 * @param qualifiers     specific qualifier annotations to consider,
	 *                       in addition to qualifiers at the bean class level
	 */
	@SuppressWarnings("unchecked")
	public void registerBean(Class<?> annotatedClass, String name, Class<? extends Annotation>... qualifiers) {
		doRegisterBean(annotatedClass, null, name, qualifiers);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from
	 * class-declared annotations.
	 *
	 * @param annotatedClass        the class of the bean
	 * @param instanceSupplier      a callback for creating an instance of the bean
	 *                              (may be {@code null})
	 * @param name                  an explicit name for the bean
	 * @param qualifiers            specific qualifier annotations to consider, if any,
	 *                              in addition to qualifiers at the bean class level
	 * @param definitionCustomizers one or more callbacks for customizing the
	 *                              factory's {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
	 * @since 5.0
	 */
	<T> void doRegisterBean(Class<T> annotatedClass, @Nullable Supplier<T> instanceSupplier, @Nullable String name,
							@Nullable Class<? extends Annotation>[] qualifiers, BeanDefinitionCustomizer... definitionCustomizers) {

		/**
		 * 根据指定的bean创建一个AnnotatedGenericBeanDefinition
		 * 这个AnnotatedGenericBeanDefinition可以理解为一个数据结构
		 * AnnotatedGenericBeanDefinition包含了类的其他信息,比如一些元信息
		 * scope，lazy等等
		 */
		AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(annotatedClass);
		/**
		 * 判断这个类是否需要跳过解析
		 * 通过代码可以知道spring判断是否跳过解析，主要判断类有没有加注解
		 */
		if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
			return;
		}
		//TODO (CC) 不知道
		abd.setInstanceSupplier(instanceSupplier);
		//
		/**
		 * 得到作用域并把作用域设置到Bean的元数据中
		 * scopeMetadataResolver == AnnotationScopeMetadataResolver
		 *  用于处理Bean的scope注解
		 */
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
		abd.setScope(scopeMetadata.getScopeName());
		/**
		 * 创建Bean的名字 如果bean的名字没有定义就用beanNameGenerator 来为Bean生成一个名字
		 * beanNameGenerator = AnnotationBeanNameGenerator
		 */
		String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));
		/**
		 * 处理类当中的通用注解
		 * Lazy DependsOn Primary Role等等注解
		 * 处理完成之后processCommonDefinitionAnnotations会把处理的信息写入到abd这个参数中
		 */
		AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
		/**
		 *  Spring 加载初始化配置类的时候 qualifiers = null
		 *  如果在向容器注册注解Bean定义时，使用了额外的限定符注解则解析
		 *  关于Qualifier和Primary主要涉及到spring的自动装配
		 *  这里需要注意的byName和qualifiers这个变量是Annotation类型的数组，里面存不仅仅是Qualifier注解
		 *  理论上里面里面存的是一切注解，所以可以看到下面的代码spring去循环了这个数组
		 *  然后依次判断了注解当中是否包含了Primary，是否包含了Lazy
		 */
		if (qualifiers != null) {
			for (Class<? extends Annotation> qualifier : qualifiers) {
				if (Primary.class == qualifier) {
					//如果配置了@Primary注解，如果加了则作为首选
					abd.setPrimary(true);
				} else if (Lazy.class == qualifier) {
					//懒加载 在这里AnnotationConfigUtils.processCommonDefinitionAnnotations(abd)处理了
					abd.setLazyInit(true);
				} else {
					//如果使用了除@Primary和@Lazy以外的其他注解，则为该Bean添加一个根据名字自动装配的限定符
					abd.addQualifier(new AutowireCandidateQualifier(qualifier));
				}
			}
		}
		for (BeanDefinitionCustomizer customizer : definitionCustomizers) {
			customizer.customize(abd);
		}
		/**
		 * BeanDefinitionHolder 就是为了数据方便传输定义的一个数据包装对象
		 */
		BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
		//TODO (CC) Spring MVC
		definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
		/**
		 * 把上述的这个数据结构注册给registry
		 * registy就是AnnotatonConfigApplicationContext
		 * AnnotatonConfigApplicationContext在初始化的时候通过构造方法实创建了一个DefaultListableBeanFactory
		 * registerBeanDefinition里面就是把definitionHolder这个数据结构包含的信息注册到
		 * DefaultListableBeanFactory这个工厂中
		 */
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
	}


	/**
	 * Get the Environment from the given registry if possible, otherwise return a new
	 * StandardEnvironment.
	 */
	/**
	 * 这里传入的spring
	 */
	private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		if (registry instanceof EnvironmentCapable) {
			return ((EnvironmentCapable) registry).getEnvironment();
		}
		return new StandardEnvironment();
	}

}
