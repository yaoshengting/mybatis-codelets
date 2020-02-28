package com.codelets.dao.condition;

import java.util.List;

import com.codelets.dao.condition.where.IColumnCondition;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月23日 下午9:30:34
 * 
 * 实现功能：删除SQL参数对象
 */
public class DeleteArgument extends AbstractSqlWhereArgument {
	/**
	 * @param tableName
	 *            {@link AbstractSqlTableArgument#tableName}
	 * @param whereColumns
	 *            {@link AbstractSqlWhereArgument#whereColumns}
	 */
	public DeleteArgument(final String tableName, final List<IColumnCondition> whereColumns) {
		super(tableName, whereColumns);
	}
}
