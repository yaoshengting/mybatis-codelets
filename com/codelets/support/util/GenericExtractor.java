/**
 * 
 */
package com.codelets.support.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年12月22日 下午7:19:48
 * 
 * 实现功能：泛型提取器
 */
public final class GenericExtractor {
	/**
	 * @param type
	 *            用来提取类型信息的Type
	 * @param i
	 *            第i个类型信息
	 * @return 对应的类型信息
	 */
	public static Class<?> getClass(final Type type, final int i) {
		if (type instanceof ParameterizedType) { // 处理泛型类型
			return getGenericClass((ParameterizedType) type, i);
		} else if (type instanceof TypeVariable) {// TypeVariable是类型变量
			final TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			return getClass(typeVariable.getBounds()[0], 0); // 处理泛型擦拭对象
		} else {// class本身也是type，强制转型
			return (Class<?>) type;
		}
	}

	/**
	 * @param parameterizedType
	 * @param i
	 * @return
	 */
	private static Class<?> getGenericClass(final ParameterizedType parameterizedType, final int i) {
		final Type genericClass = parameterizedType.getActualTypeArguments()[i];
		if (genericClass instanceof ParameterizedType) { // 处理多级泛型
			return (Class<?>) ((ParameterizedType) genericClass).getRawType();
		} else if (genericClass instanceof GenericArrayType) { // 处理数组泛型
			return (Class<?>) ((GenericArrayType) genericClass).getGenericComponentType();
		} else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象
			final TypeVariable<?> typeVariable = (TypeVariable<?>) genericClass;
			final Type bound = typeVariable.getBounds()[0];
			return getClass(bound, 0); // 处理泛型擦拭对象
		} else {
			return (Class<?>) genericClass;
		}
	}

	/**  */
	private GenericExtractor() {
		super();
	}
}
