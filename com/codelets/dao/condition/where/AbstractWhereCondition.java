package com.codelets.dao.condition.where;

import org.springframework.util.Assert;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月23日 下午9:39:03
 * 
 * 实现功能：WHERE抽象类，该类只定义了列名和值，具体的运算符有实现类来实现
 */
public abstract class AbstractWhereCondition implements IColumnCondition {
	// 列名
	private final String columnName;
	// 值
	private final Object value;

	/**
	 * @param columnName
	 *            列名
	 */
	AbstractWhereCondition(final String columnName) {
		Assert.hasLength(columnName, "列名不能为空");
		this.columnName = columnName;
		value = null;
	}

	/**
	 * @param columnName
	 *            列名
	 * @param value
	 *            列值
	 */
	AbstractWhereCondition(final String columnName, final Object value) {
		Assert.hasLength(columnName, "列名不能为空");
		Assert.notNull(value, "列值不能为null");
		this.columnName = columnName;
		this.value = value;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tintinloan.egif.dao.condition.column.IColumnCondition#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}

}
