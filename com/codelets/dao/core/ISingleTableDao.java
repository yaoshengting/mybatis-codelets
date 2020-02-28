
package com.codelets.dao.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.codelets.dao.condition.aggregate.IAggregateCondition;
import com.codelets.dao.condition.where.IColumnCondition;
import com.codelets.support.api.IBaseEntry;
import com.codelets.support.condition.PageCond;
import com.codelets.support.enumtype.OrderByEnum;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月23日 下午4:21:00
 * 
 * 实现功能：单表操作接口
 */
public interface ISingleTableDao<T extends IBaseEntry> {

	/**
	 * 新增数据
	 * 
	 * @param entry
	 *            待新增的数据实体
	 * 
	 * @return 主键
	 */
	long insert(final T entry);

	/**
	 * 无主键批量插入，返回条数
	 * 
	 * @param entryList
	 * @return
	 */
	int insertListWithoutPK(final List<T> entryList);

	/**
	 * 批量插入--返回主键List
	 * 
	 * @param entryList
	 * @return
	 */
	List<String> insertList(final List<T> entryList);

	/**
	 * 删除数据实体主键
	 * 
	 * @param entry
	 *            通过数据实体
	 * 
	 * @return 主键
	 */
	public boolean deleteByEntry(final T entry);

	/**
	 * 根据主键删除实体数据
	 * 
	 * @param entryId
	 *            实体类主键
	 * @return
	 */
	public boolean deleteEntryById(final Long entryId);

	/**
	 * add by yaoshengting<br/>
	 * 2018/6/29 根据主键更新该entry
	 * 
	 * @param tobeUpdate
	 * @param columnNameSet
	 * @return
	 */
	boolean updateEntryColumnsByID(final T tobeUpdate, final Set<String> columnNameSet);

	/**
	 * 使用主键查询实体
	 * 
	 * @param entryID
	 *            实体主键
	 * @return 对应的实体，如果不存在返回null
	 */
	T queryByID(final long entryID);

	/**
	 * 查询表中所有记录
	 * 
	 * @return
	 */
	List<T> queryAll();

	/**
	 * 查询表中记录条数
	 * 
	 * @return
	 */
	long queryAllCount();

	/**
	 * add by yaoshengting<br/>
	 * 2019/11/4 根据列汇总查询条数
	 * 
	 * @param columnValues
	 * @return
	 */
	long queryCountByColumns(final Map<String, Object> columnValues);

	/**
	 * 根据列进行查询
	 * 
	 * @param columnValues
	 *            过滤条件
	 * @return
	 */
	List<T> queryListByColumns(final Map<String, Object> columnValues);

	/**
	 * 根据列查询并排序
	 * 
	 * @param columnValues
	 *            过滤条件
	 * @param orderColumns
	 *            排序列
	 * @return
	 */
	List<T> queryListByColumnsWithOrder(final Map<String, Object> columnValues,
			final Map<String, OrderByEnum> orderColumns);

	/**
	 * 根据列查询并排序，取结果的topN条记录
	 * 
	 * @param columnValues
	 *            过滤条件
	 * @param orderColumns
	 *            排序列
	 * @param topN
	 *            结果的前N条
	 * @return
	 */
	List<T> queryListByColumnsWithOrderTopN(final List<IColumnCondition> whereCondition,
			final Map<String, OrderByEnum> orderColumns, final int topN);

	/**
	 * 分页查询并进行排序
	 * 
	 * @param columnValues
	 *            过滤条件
	 * @param pageCond
	 *            分页对象
	 * @param orderColumns
	 *            排序列
	 * @return
	 */
	public List<T> queryListByColumnsWithPageOrder(final Map<String, Object> columnValues, final PageCond pageCond,
			final Map<String, OrderByEnum> orderColumns);

	/**
	 * 分页查询并进行排序
	 * 
	 * @param columnValues
	 *            过滤条件
	 * @param pageCond
	 *            分页对象
	 * @param orderColumns
	 *            排序列
	 * @return
	 */
	public List<T> queryListByColumnsWithPageOrder(final List<IColumnCondition> whereCondition, final PageCond pageCond,
			final Map<String, OrderByEnum> orderColumns);

	/**
	 * 分页查询
	 * 
	 * @param columnValues
	 *            过滤条件
	 * @param pageCond
	 *            分页对象
	 * @return
	 */
	public List<T> queryListByColumnsWithPage(final Map<String, Object> columnValues, final PageCond pageCond);

	/**
	 * 根据条件查询并汇总相应的列值
	 * 
	 * @param sumColumnSet
	 *            汇总列
	 * @param columnValues
	 *            过滤条件
	 * @return
	 */
	T querySumByColumns(final Set<String> sumColumnSet, final List<IColumnCondition> whereCondition);

	/**
	 * 查询并聚合
	 * 
	 * @param aggregateColumn
	 *            聚合列
	 * @param whereCondition
	 *            过滤条件
	 * @return
	 */
	T queryAggregateByColumns(final Set<IAggregateCondition> aggregateColumn,
			final List<IColumnCondition> whereCondition);
}
