/**
 * 
 */
package com.codelets.support.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import lombok.SneakyThrows;

/**
 * 子类扫描
 * 
 * 
 */
public final class SubCLassScanner {

	/**
	 * 获取在指定包下某个class的所有非抽象子类
	 * 
	 * @param <E>
	 *            抽象类类型
	 * 
	 * @param parentClass
	 *            父类
	 * @param packagePath
	 *            指定包，格式如"com/tintinloan/*"
	 * @return 该父类对应的所有子类列表
	 */
	@SneakyThrows(ClassNotFoundException.class)
	public static <E> List<Class<? extends E>> scan(final Class<E> parentClass, final String packagePath) {
		final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AssignableTypeFilter(parentClass));
		final Set<BeanDefinition> components = provider.findCandidateComponents(packagePath);
		final List<Class<? extends E>> subClasses = new ArrayList<Class<? extends E>>();
		for (final BeanDefinition component : components) {
			@SuppressWarnings("unchecked")
			final Class<E> cls = (Class<E>) Class.forName(component.getBeanClassName());
			subClasses.add(cls);
		}
		return subClasses;
	}

	/** * */
	private SubCLassScanner() {
		super();
	}
}
