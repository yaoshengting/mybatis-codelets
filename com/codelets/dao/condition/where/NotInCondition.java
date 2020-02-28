/**
 * 
 */
package com.codelets.dao.condition.where;

import java.util.Collection;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月20日 下午1:34:51
 * 
 * 实现功能：NOT IN过滤条件
 */
public class NotInCondition extends AbstractWhereCondition {

	/**
	 * @param columnName
	 *            列名
	 * @param valueList
	 *            列值
	 */
	public NotInCondition(final String columnName, final Collection<?> valueList) {
		super(columnName, valueList);
		org.springframework.util.Assert.notEmpty(valueList, "不能处理空的值列表");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tintinloan.egif.dao.condition.Condition#getOperator()
	 */
	@Override
	public SqlOperand getOperand() {
		return SqlOperand.NOT_IN;
	}
}
