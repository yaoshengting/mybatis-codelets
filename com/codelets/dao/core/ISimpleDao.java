/**
 * 
 */
package com.codelets.dao.core;

import java.util.List;

import com.codelets.dao.util.TableMeta;
import com.codelets.support.api.IBaseEntry;
import com.codelets.support.enumtype.DaoTypeEnum;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月23日 下午4:20:42
 * 
 * 实现功能：
 */
public interface ISimpleDao {

	/**
	 * 新增数据-使用数据库自增主键
	 * 
	 * @param <E>
	 *            表对象类型
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param entry
	 *            数据实体
	 *
	 * @return 主键
	 */
	<E extends IBaseEntry> long insert(final TableMeta<E> tableMeta, final IBaseEntry entry);

	/**
	 * 新增数据-用自带的主键
	 * 
	 * @param <E>
	 *            表对象类型
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param entry
	 *            数据实体
	 * @return 主键
	 */
	<E extends IBaseEntry> long insertWithoutPK(final TableMeta<E> tableMeta, final IBaseEntry entry);

	/**
	 * 无主键批量新增 批量新增列表，如果新增数据含有历史表则也插入到历史表，如果不包含历史表，则不插入历史表<br/>
	 * 通过Entry中的历史表注解@History判断是否含有历史表
	 * 
	 * @param tableMeta表级别的元数据
	 *            类型信息
	 * @param entryList待新增数据
	 *
	 * 
	 * @return
	 */
	<E extends IBaseEntry> int insertListWithoutPK(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList);

	/**
	 * 批量插入表，返回主键List 批量新增列表，如果新增数据含有历史表则也插入到历史表，如果不包含历史表，则不插入历史表<br/>
	 * 
	 * @param tableMeta
	 * @param entryList
	 * @return
	 */
	<E extends IBaseEntry> List<String> insertList(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList);

	/**
	 * 新增插入历史表
	 * 
	 * @param <E>
	 *            表对象类型
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param tobeUpdate
	 *            待更新的数据，含主键
	 * @param daoType
	 *            dao类型
	 * @return 是否更新成功
	 */
	<E extends IBaseEntry> boolean saveHistory(final TableMeta<E> tableMeta, final IBaseEntry entry,
			final DaoTypeEnum daoType);

	/**
	 * 使用实体主键删除数据
	 * 
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param <E>
	 *            实体类型
	 * 
	 * @param entryID
	 *            实体主键
	 * 
	 * @return 是否成功删除
	 */
	<E extends IBaseEntry> boolean deleteByEntryID(final TableMeta<E> tableMeta, final long entryID);

	/**
	 * 使用主键更新信息
	 * 
	 * @param <E>
	 *            表对象类型
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param tobeUpdate
	 *            待更新的数据，含主键
	 * @param daoType
	 *            dao类型
	 * @return 是否更新成功
	 */
	<E extends IBaseEntry> boolean update(final TableMeta<E> tableMeta, final IBaseEntry tobeUpdate,
			final DaoTypeEnum daoType);

	/**
	 * 使用线索查询信息
	 * 
	 * @param <E>
	 *            实体类型
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param entryID
	 *            实体主键
	 * @param daoType
	 *            到类型
	 * @return 对应的实体数据
	 */
	<E extends IBaseEntry> E queryByEntryID(final TableMeta<E> tableMeta, final long entryID);

}
