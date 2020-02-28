package com.codelets.support.util;

import com.codelets.support.api.IBaseEntry;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月24日 下午2:15:15
 * 
 * 实现功能：主键容器接口
 */
public interface IPrimaryKeyFieldContainer {
	/**
	 * 获取主键域名
	 * 
	 * @param entryClass
	 *            实体类型
	 * @return 该实体类型被标注过annotation的字段名称
	 */
	String getPrimaryKeyFieldName(final Class<? extends IBaseEntry> entryClass);
}
