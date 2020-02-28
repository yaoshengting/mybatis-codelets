/**
 * 
 */
package com.codelets.support.util;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

/**
 * 扫描 在指定包下某个接口的所有子接口
 * 
 */
public final class InterfacesScanner extends ClassPathScanningCandidateComponentProvider {

	/**
	 *delete by yaoshengting 2019-02-02<br/>
	 * 获取在指定包下的所有接口
	 * 
	 * @param <E>
	 *            接口类型
	 * 
	 * @param packagePath
	 *            指定包，格式如"com/tintinloan/*"
	 * 
	 * @return 指定包下的所有接口
	 */
//	@SneakyThrows(ClassNotFoundException.class)
//	public static <E> Set<Class<? extends E>> interfacesScanner(final String packagePath) {
//		final InterfacesScanner provider = new InterfacesScanner();
//
//		provider.addIncludeFilter(new AssignableTypeFilter(Object.class));
//		provider.addIncludeFilter(new InterfaceTypeFilter(Object.class));
//		final Set<BeanDefinition> components = provider.findCandidateComponents(packagePath);
//
//		final Set<Class<? extends E>> interfacesList = new HashSet<Class<? extends E>>();
//		for (final BeanDefinition component : components) {
//			@SuppressWarnings("unchecked")
//			final Class<E> cls = (Class<E>) Class.forName(component.getBeanClassName());
//
//			if (cls.isInterface()) {
//				interfacesList.add(cls);
//			}
//		}
//		return interfacesList;
//	}

	/**
	 * delete by yaoshengting 2019-02-02<br/>
	 * 扫描 在指定包下某个接口的所有所有子接口
	 * 
	 * @param <E>
	 *            接口类型
	 * 
	 * @param parentInterface
	 *            父类
	 * @param packagePath
	 *            指定包，格式如"com/tintinloan/*"
	 * @return 该父类对应的所有子类接口集合
	 */
//	@SneakyThrows(ClassNotFoundException.class)
//	public static <E> Set<Class<? extends E>> scan(final Class<E> parentInterface, final String packagePath) {
//		final InterfacesScanner provider = new InterfacesScanner();
//
//		provider.addIncludeFilter(new AssignableTypeFilter(parentInterface));
//		provider.addIncludeFilter(new InterfaceTypeFilter(parentInterface));
//		final Set<BeanDefinition> components = provider.findCandidateComponents(packagePath);
//
//		final Set<Class<? extends E>> childrenInterfaceList = new HashSet<Class<? extends E>>();
//		for (final BeanDefinition component : components) {
//			@SuppressWarnings("unchecked")
//			final Class<E> cls = (Class<E>) Class.forName(component.getBeanClassName());
//
//			if (isDefaultFilters(cls, parentInterface)) {
//				childrenInterfaceList.add(cls);
//			}
//		}
//
//		return childrenInterfaceList;
//	}

	/** 
	 * delete by yaoshengting 2019-02-02<br/>
	 * 考虑在InterfaceTypeFilter过滤 */
//	private static <E> boolean isDefaultFilters(final Class<E> cls, final Class<E> parentInterface) {
//		if (!cls.isInterface()) {
//			return false;
//		}
//
//		// 自身筛选
//		if (cls.getName().equals(parentInterface.getName())) {
//			return false;
//		}
//
//		return containsInstance(Arrays.asList(cls.getInterfaces()), parentInterface);
//	}

	/** */
	private InterfacesScanner() {
		super(false);
	}

	@Override
	protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
		return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
	}

}
