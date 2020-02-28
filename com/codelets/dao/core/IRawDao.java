package com.codelets.dao.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.codelets.dao.condition.aggregate.IAggregateCondition;
import com.codelets.dao.condition.where.IColumnCondition;
import com.codelets.dao.util.TableMeta;
import com.codelets.support.api.IBaseEntry;
import com.codelets.support.condition.PageCond;
import com.codelets.support.enumtype.OrderByEnum;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月23日 下午4:20:27
 * 
 * 实现功能：
 */
public interface IRawDao extends ISimpleDao {

	/**
	 * 使用实体主键删除数据
	 * 
	 * @param <E>
	 *            表对象类型
	 * @param tableMeta
	 *            表级别的元数据
	 * @param idList
	 *            实体主键list
	 * @param daoType
	 *            dao类型
	 * @return 是否成功删除
	 */
	<E extends IBaseEntry> boolean deleteListByIdList(final TableMeta<E> tableMeta, final List<Long> idList);

	/**
	 * 使用列值进行删除，可删除多行数据
	 * 
	 * @param tableMeta
	 *            表级别的元数据
	 * @param columnValue
	 *            列条件
	 * @param <E>
	 *            类型信息
	 * @return 删除条数
	 * 
	 */
	<E extends IBaseEntry> int deleteByColumn(final TableMeta<E> tableMeta, final Map<String, Object> columnValue);

	/**
	 * 查询对应于columnValues的对应数据实体
	 * 
	 * @param tableMeta
	 *            表级别的元数据
	 * @param <E>
	 *            类型信息
	 * @param columnValues
	 *            查询线索
	 * @return 查询获得的数据
	 */
	<E extends IBaseEntry> E queryByColumns(final TableMeta<E> tableMeta, final Map<String, Object> columnValues);

	/**
	 * 按照指定列名，取得对应于columnsValues值的对应列值
	 * 
	 * @param <E>
	 *            表对象类型
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param columnValues
	 *            查询线索
	 * @param resultColumnsName
	 *            结果列名
	 * @return 查询获得的数据
	 */
	<E extends IBaseEntry> E queryEntryByColumns(final TableMeta<E> tableMeta, final Map<String, Object> columnValues,
			final Set<String> resultColumnsName);

	/**
	 * 查询对应于columnValues的对应数据实体主键
	 * 
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param <E>
	 *            类型信息
	 * @param columnValues
	 *            查询线索
	 * @return 对应的数据实体主键
	 */
	<E extends IBaseEntry> long queryEntryIdByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues);

	/**
	 * 查询对应于columnValues的对应数据实体主键列表
	 * 
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param <E>
	 *            类型信息
	 * @param columnValues
	 *            查询线索
	 * @return 对应的数据实体主键
	 */
	<E extends IBaseEntry> List<Long> queryEntryIdListByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues);

	/**
	 * 使用列名和值查询实体列表
	 * 
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param <E>
	 *            实体类型
	 * @param columnValues
	 *            列名和值映射，当值为列表时，采用in做为操作符
	 * @param daoType
	 *            用来操作的dao类型
	 * @return 对应的实体列表，如果不存在返回空列表
	 */
	<E extends IBaseEntry> List<E> queryListByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues);

	/**
	 * 使用列名和值分页查询实体列表
	 * 
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param <E>
	 *            实体类型
	 * @param columnValues
	 *            列名和值映射，当值为列表时，采用in做为操作符
	 * @param pageCond
	 *            分页条件
	 * @return 对应的实体列表，如果不存在返回空列表
	 */
	<E extends IBaseEntry> List<E> queryListByColumnsAndPage(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues, final PageCond pageCond);

	/**
	 * 使用列名和值分页查询实体列表
	 * 
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param <E>
	 *            实体类型
	 * @param columnValues
	 *            列名和值映射，当值为列表时，采用in做为操作符
	 * @param daoType
	 *            用来操作的dao类型
	 * @param pageCond
	 *            分页条件
	 * @param orderColumns
	 *            排序列
	 * @param forward
	 *            排序顺序
	 * @return 对应的实体列表，如果不存在返回空列表
	 */
	<E extends IBaseEntry> List<E> queryListByColumnsAndPageAndOrder(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues, final PageCond pageCond,
			final Map<String, OrderByEnum> orderColumns);

	/**
	 * 使用列名和值分页查询实体列表
	 * 
	 * @param tableMeta
	 * @param whereCondition
	 * @param daoType
	 * @param pageCond
	 * @param orderColumns
	 * @return
	 */
	<E extends IBaseEntry> List<E> queryListByColumnsAndPageAndOrder(final TableMeta<E> tableMeta,
			final List<IColumnCondition> whereCondition, final PageCond pageCond,
			final Map<String, OrderByEnum> orderColumns);

	/**
	 * 按照指定列名，使用列名和值查询实体列表
	 * 
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param <E>
	 *            实体类型
	 * @param columnValues
	 *            列名和值映射，当值为列表时，采用in做为操作符
	 * @param resultColumnsName
	 *            指定的返回列
	 * @return 对应的实体列表，如果不存在返回空列表
	 */
	<E extends IBaseEntry> List<E> queryListByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues, final Set<String> resultColumnsName);

	/**
	 * 按照指定列名，使用列名和值查询实体列表
	 * 
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param <E>
	 *            实体类型
	 * @param columnValues
	 *            列名和值映射，当值为列表时，采用in做为操作符
	 * @param resultColumnsName
	 *            指定的返回列
	 * @param orderColumn
	 * @return 对应的实体列表，如果不存在返回空列表
	 */
	<E extends IBaseEntry> List<E> queryListByColumnsAndOrder(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues, final Set<String> resultColumnsName,
			final Map<String, OrderByEnum> orderColumns);

	/**
	 * 根据列查询并排序，取结果的topN条记录
	 * 
	 * @param tableMeta
	 * @param columnValues
	 * @param daoType
	 * @param resultColumnsName
	 * @param orderColumns
	 * @param topN
	 * @return
	 */
	<E extends IBaseEntry> List<E> queryListByColumnsWithOrderTopN(final TableMeta<E> tableMeta,
			final List<IColumnCondition> whereCondition, final Set<String> resultColumnsName,
			final Map<String, OrderByEnum> orderColumns, final int topN);

	/**
	 * 使用主键更新指定列的信息
	 * 
	 * @param <E>
	 *            表对象类型
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param tobeUpdate
	 *            待更新的数据，含主键
	 * @param columnNameSet
	 *            指定的列名集合
	 * @return 是否更新成功
	 */
	<E extends IBaseEntry> boolean updateColumnsById(final TableMeta<E> tableMeta, final IBaseEntry tobeUpdate,
			final Set<String> columnNameSet);

	/**
	 * 根据条件查询并汇总相应的列值
	 * 
	 * @param sumColumnSet
	 * @param columnValues
	 * @return
	 */
	<E extends IBaseEntry> E querySumByColumns(final TableMeta<E> tableMeta, final Set<String> sumColumnSet,
			final List<IColumnCondition> whereCondition);

	/**
	 * 查询并聚合
	 * 
	 * @param tableMeta
	 * @param daoType
	 * @param aggregateColumn
	 * @param whereCondition
	 * @return
	 */
	<E extends IBaseEntry> E queryAggregateByColumns(final TableMeta<E> tableMeta,
			final Set<IAggregateCondition> aggregateColumn, final List<IColumnCondition> whereCondition);

	/**
	 * 查询表中所有对象
	 * 
	 * @return
	 */
	<E extends IBaseEntry> List<E> queryAll(final TableMeta<E> tableMeta);

	/**
	 * 查询表中数据条数
	 * 
	 * @param tableMeta
	 * @return
	 */
	<E extends IBaseEntry> long queryAllCount(final TableMeta<E> tableMeta);

	/**
	 * 根据条件查询表中数据条数
	 * 
	 * @param tableMeta
	 * @param columnValues
	 * @return
	 */
	<E extends IBaseEntry> long queryCountByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues);
}
