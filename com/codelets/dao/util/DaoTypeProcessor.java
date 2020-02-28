/**
 * 
 */
package com.codelets.dao.util;

import com.codelets.support.annnotation.DaoType;
import com.codelets.support.enumtype.DaoTypeEnum;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年2月1日 下午4:59:52
 * 
 * 实现功能：DaoType操作工具类
 */
public final class DaoTypeProcessor {

	private DaoTypeProcessor() {
		super();
	}

	/**
	 * 提取dao上的正副本信息
	 * 
	 * @param classInfo
	 *            用来判断的类型信息
	 * 
	 * @return {@link DaoTypeEnum}
	 */
	public static DaoTypeEnum extractDaoType(final Class<?> classInfo) {
		final DaoType daoType = classInfo.getAnnotation(DaoType.class);
		if (daoType != null) {
			return daoType.type();
		}

		for (final Class<?> interFace : classInfo.getInterfaces()) {
			final DaoType interFaceDaoType = interFace.getAnnotation(DaoType.class);
			if (interFaceDaoType != null) {
				return interFaceDaoType.type();
			}
		}

		return null;
	}

}
