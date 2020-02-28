package com.codelets.support.strategy;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年12月22日 下午7:59:25
 * 
 * 实现功能：当一个类实现了这个接口（ApplicationContextAware）之后，这个类就可以方便获得ApplicationContext中的所有bean
 */
@Service
@Lazy(false)
@Slf4j
public class ManageSpringBeans implements ApplicationContextAware {
	// 声明一个静态变量保存ApplicationContext上下文
	private static ApplicationContext context;

	/**
	 * {@link ApplicationContext#getBean(Class)} 获取spring beans
	 * 
	 * @param <T>
	 *            T
	 * 
	 * @param requiredType
	 *            bean类型
	 * @return 对应的bean
	 * 
	 */
	public static <T> T getBean(final Class<T> requiredType) {
		return context.getBean(requiredType);
	}

	/**
	 * {@link ApplicationContext#getBean(Class)} 获取spring beans
	 * 
	 * @param <T>
	 *            T
	 * 
	 * @param beanName
	 *            beanName
	 * @return 对应的bean
	 * 
	 */
	public static <T> T getBean(final String beanName) {
		@SuppressWarnings("unchecked")
		final T bean = (T) context.getBean(beanName);
		return bean;
	}

	/**
	 * {@link ApplicationContext#getBean(Class)} 获取spring beans <br/>
	 * 载入所有同类型的bean
	 * 
	 * @param <T>
	 *            T
	 * 
	 * @param requiredType
	 *            bean类型
	 * @return 对应的bean
	 * 
	 */
	public static <T> Map<String, T> getBeans(final Class<T> requiredType) {
		return context.getBeansOfType(requiredType);
	}

	/**
	 * 从annotation提取bean清单
	 * 
	 * @param annotationType
	 *            annotation类型
	 * @return 标注了该annotation的beans
	 */
	public static Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType) {
		return context.getBeansWithAnnotation(annotationType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		context = applicationContext;
		log.info("spring init ApplicationContext of " + this.getClass().getName());
	}
}
