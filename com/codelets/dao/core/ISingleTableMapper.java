package com.codelets.dao.core;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import com.codelets.dao.condition.DeleteArgument;
import com.codelets.dao.condition.DeleteListArgument;
import com.codelets.dao.condition.InsertArgument;
import com.codelets.dao.condition.InsertListArgument;
import com.codelets.dao.condition.QueryAggregateArgument;
import com.codelets.dao.condition.QueryArgument;
import com.codelets.dao.condition.QueryPageArgument;
import com.codelets.dao.condition.QuerySumArgument;
import com.codelets.dao.condition.UpdateArgument;
import com.codelets.dao.pojo.TableNameValue;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月15日 上午9:32:12
 * 
 * 实现功能：
 */
@Repository
@Lazy
public interface ISingleTableMapper {

	/**
	 * 插入不含主键（主键自增），返回自增主键
	 * 
	 * @param insertArgument
	 *            插入参数
	 * @return 插入条数
	 */
	int insert(final InsertArgument insertArgument);

	/**
	 * 插入含主键，该主键是用户自带主键
	 * 
	 * @param insertArgument
	 *            插入参数
	 * @return 插入条数
	 */
	int insertWithPK(final InsertArgument insertArgument);

	/**
	 * 插入数据，数据库中无主键列
	 * 
	 * @param insertListArgument
	 * @return
	 */
	int insertWithoutPK(final InsertArgument insertArgument);

	/**
	 * 插入数据，返回插入的条数
	 * 
	 * @param insertListArgument
	 * @return
	 */
	int insertList(final InsertListArgument insertListArgument);

	/**
	 * 新的新增列表，批量插入
	 * 
	 * @param colNameValueIdList
	 * @return
	 */
	int newInsertList(final List<TableNameValue> colNameValueIdList);

	/**
	 * 删除
	 * 
	 * @param deleteArgument
	 *            删除参数
	 * @return 删除条数
	 */
	int delete(final DeleteArgument deleteArgument);

	/**
	 * 使用实体主键删除数据
	 * 
	 * 
	 * @param deleteListArgument
	 *            删除集合参数
	 * @return 删除条数
	 */
	int deleteListByIdList(final DeleteListArgument deleteListArgument);

	/**
	 * 更新
	 * 
	 * @param updateArgument
	 *            更新参数
	 * @return 更新条数
	 */
	int update(final UpdateArgument updateArgument);

	/**
	 * 查询条数
	 * 
	 * @param queryArgument
	 *            查询参数
	 * @return 总条数
	 */
	long queryCount(final QueryArgument queryArgument);

	/**
	 * 查询结果集
	 * 
	 * @param queryArgument
	 *            查询参数
	 * @return 结果集
	 */
	List<Map<String, Object>> queryForList(final QueryArgument queryArgument);

	/**
	 * 查询结果集
	 * 
	 * @param queryPageArgument
	 *            查询参数(带分页)
	 * @return 结果集
	 */
	List<Map<String, Object>> queryForListForPage(final QueryPageArgument queryPageArgument);

	/**
	 * 查询并根据指定列汇总
	 * 
	 * @param querySumArgument
	 * @return
	 */
	Map<String, Object> querySumByColumns(final QuerySumArgument querySumArgument);

	/**
	 * 查询并聚合
	 * 
	 * @param queryAggregateArgument
	 * @return
	 */
	Map<String, Object> queryAggregateByColumns(final QueryAggregateArgument queryAggregateArgument);

}
