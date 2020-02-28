package com.codelets.support.util;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 */
public final class NumericChecker {
	private static final Set<Class<?>> NUMERIC_CLASS = new HashSet<Class<?>>();

	static {
		NUMERIC_CLASS.add(Integer.class);
		NUMERIC_CLASS.add(Short.class);
		NUMERIC_CLASS.add(Long.class);
		NUMERIC_CLASS.add(Float.class);
		NUMERIC_CLASS.add(Double.class);
		NUMERIC_CLASS.add(BigDecimal.class);
		NUMERIC_CLASS.add(Byte.class);

		NUMERIC_CLASS.add(Byte.TYPE);
		NUMERIC_CLASS.add(Integer.TYPE);
		NUMERIC_CLASS.add(Short.TYPE);
		NUMERIC_CLASS.add(Long.TYPE);
		NUMERIC_CLASS.add(Float.TYPE);
		NUMERIC_CLASS.add(Double.TYPE);
	}

	/**
	 * Check the object, return true if it is a number
	 * 
	 * @param clazz
	 *            类型信息
	 * @return 是否数字类型
	 */
	public static boolean isNumeric(final Class<?> clazz) {
		return NUMERIC_CLASS.contains(clazz);
	}

	/**
	 * 
	 */
	private NumericChecker() {
		super();
	}
}
