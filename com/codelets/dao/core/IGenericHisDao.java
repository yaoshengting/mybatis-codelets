package com.codelets.dao.core;

import java.util.List;

import com.codelets.dao.util.TableMeta;
import com.codelets.support.api.IBaseEntry;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月23日 下午4:09:36
 * 
 * 实现功能：历史表通用DAO
 */
public interface IGenericHisDao {

	/**
	 * 单表单条数据插入
	 * 
	 * @param <E>
	 *            表对象类型
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param entry
	 *            待新增实体
	 * @return 主键值
	 */
	<E extends IBaseEntry> long insert(final TableMeta<E> tableMeta, final IBaseEntry entry);

	/**
	 * 单表多条数据插入
	 * 
	 * @param <E>
	 *            表对象类型
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param entryList
	 *            待新增实体list
	 * @return 新增条数
	 */
	<E extends IBaseEntry> int insertList(final TableMeta<E> tableMeta, final List<IBaseEntry> entryList);
}
