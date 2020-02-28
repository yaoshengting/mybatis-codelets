package com.codelets.dao.condition;

import java.util.List;
import java.util.Set;

import com.codelets.dao.condition.where.IColumnCondition;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月21日 下午5:39:27
 * 
 * 实现功能：汇总查询参数
 */
public class QuerySumArgument extends AbstractSqlWhereArgument {
	/**
	 * 汇总列
	 */
	private Set<String> sumColumnSet = null;

	/**
	 * 
	 * @param tableName表名
	 * @param whereColumns过滤条件
	 */
	public QuerySumArgument(final String tableName, final List<IColumnCondition> whereColumns,
			Set<String> sumColumnSet) {
		super(tableName, whereColumns);
		this.sumColumnSet = sumColumnSet;
	}

	public Set<String> getSumColumnSet() {
		return sumColumnSet;
	}

	public void setSumColumnSet(Set<String> sumColumnSet) {
		this.sumColumnSet = sumColumnSet;
	}

}
