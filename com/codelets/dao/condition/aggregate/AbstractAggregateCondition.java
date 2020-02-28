package com.codelets.dao.condition.aggregate;

import org.springframework.util.Assert;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月22日 上午9:46:18
 * 
 * 实现功能：聚合条件抽象类
 */
public abstract class AbstractAggregateCondition implements IAggregateCondition {
	// 列名
	private final String columnName;

	/**
	 * @param columnName
	 *            列名
	 */
	AbstractAggregateCondition(final String columnName) {
		Assert.hasLength(columnName, "列名不能为空");
		this.columnName = columnName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tintinloan.egif.dao.condition.column.IColumnCondition#getColumnName()
	 */
	@Override
	public String getColumnName() {
		return columnName;
	}
}
