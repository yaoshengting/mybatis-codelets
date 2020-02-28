package com.codelets.dao.condition;

import java.util.List;

import com.codelets.dao.condition.where.IColumnCondition;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年2月1日 下午2:07:06
 * 
 * 实现功能：抽象WHERE SQL参数对象
 */
public abstract class AbstractSqlWhereArgument extends AbstractSqlTableArgument {
	// SQL中where的过滤条件列
	private List<IColumnCondition> whereColumns;

	/**
	 * @param tableName
	 *            {@link #tableName}
	 */
	protected AbstractSqlWhereArgument(final String tableName) {
		super(tableName);
	}

	/**
	 * @param tableName
	 *            {@link #tableName}
	 * @param whereColumns
	 *            {@link #whereColumns}
	 */
	protected AbstractSqlWhereArgument(final String tableName, final List<IColumnCondition> whereColumns) {
		super(tableName);
		this.whereColumns = whereColumns;
	}

	/**
	 * @return {@link #whereColumns}
	 */
	public List<IColumnCondition> getWhereColumns() {
		return whereColumns;
	}

	/**
	 * @param whereColumns
	 *            {@link #whereColumns}
	 */
	public void setWhereColumns(final List<IColumnCondition> whereColumns) {
		this.whereColumns = whereColumns;
	}
}
