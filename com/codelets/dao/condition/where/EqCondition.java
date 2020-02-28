/**
 * 
 */
package com.codelets.dao.condition.where;

/**
 * 等于过滤条件
 */
public class EqCondition extends AbstractWhereCondition {

	/**
	 * @param columnName
	 *            {@link AbstractWhereCondition#columnName}
	 * @param value
	 *            {@link AbstractWhereCondition#value}
	 */
	public EqCondition(final String columnName, final Object value) {
		super(columnName, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.tintinloan.egif.dao.condition.column.AbstractColumnCondition#getOperator
	 * ()
	 */
	@Override
	public SqlOperand getOperand() {
		return SqlOperand.EQ;
	}

}
