package com.codelets.dao.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.codelets.dao.core.impl.SingleTableDao;
import com.codelets.support.api.IBaseEntry;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年12月23日 下午2:53:48
 * 
 * 实现功能：元信息数据提取器
 */
public final class MetaDataExtractor {

	/**
	 * 提取实体类型信息
	 * 
	 * @param <E>
	 *            继承baseEntry的entry
	 * 
	 * @param daoClass
	 *            dao类型
	 * 
	 * @return 实体类型信息
	 */
	public static <E extends IBaseEntry> Class<E> extractEntryClassInfo(final Class<?> daoClass) {
		for (Class<?> current = daoClass; current != null; current = current.getSuperclass()) {
			if (current == SingleTableDao.class) {
				break;
			}
			if (current == Object.class) {
				break;
			}

			final Class<E> firstClass = extractClass(current.getGenericSuperclass());
			if (firstClass != null) {
				return firstClass;
			}
		}

		throw new IllegalStateException("无法获取有效的entry信息" + daoClass);
	}

	private static <E extends IBaseEntry> Class<E> extractClass(final Type genericSuperType) {
		if (!(genericSuperType instanceof ParameterizedType)) {
			return null;
		}

		final ParameterizedType genericSuperClass = (ParameterizedType) genericSuperType;
		final Type[] actualTypes = genericSuperClass.getActualTypeArguments();
		if (actualTypes.length == 0) {
			return null;
		}

		final Type firstType = actualTypes[0];
		if (!(firstType instanceof Class)) {
			return null;
		}

		@SuppressWarnings("unchecked")
		final Class<E> firstClass = (Class<E>) firstType;
		if (IBaseEntry.class.isAssignableFrom(firstClass)) {
			return firstClass;
		}
		return null;
	}

	private MetaDataExtractor() {
	}
}
