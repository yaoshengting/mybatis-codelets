package com.codelets.dao.condition;

import java.util.List;
import java.util.Set;

import com.codelets.dao.condition.aggregate.IAggregateCondition;
import com.codelets.dao.condition.where.IColumnCondition;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月21日 下午5:39:27
 * 
 * 实现功能：聚合查询参数
 */
public class QueryAggregateArgument extends AbstractSqlWhereArgument {
	/**
	 * 聚合列
	 */
	private Set<IAggregateCondition> aggregateColumn = null;

	/**
	 * 
	 * @param tableName表名
	 * @param whereColumns过滤条件
	 */
	public QueryAggregateArgument(final String tableName, final List<IColumnCondition> whereColumns,
			Set<IAggregateCondition> aggregateColumn) {
		super(tableName, whereColumns);
		this.aggregateColumn = aggregateColumn;
	}

	public Set<IAggregateCondition> getAggregateColumn() {
		return aggregateColumn;
	}

	public void setAggregateColumn(Set<IAggregateCondition> aggregateColumn) {
		this.aggregateColumn = aggregateColumn;
	}
}
