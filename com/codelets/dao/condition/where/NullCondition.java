package com.codelets.dao.condition.where;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月20日 下午1:35:04
 * 
 * 实现功能： NULL过滤条件
 */
public class NullCondition extends AbstractWhereCondition {
	/**
	 * @param columnName
	 *            列名
	 */
	public NullCondition(final String columnName) {
		super(columnName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tintinloan.egif.dao.condition.Condition#getOperator()
	 */
	@Override
	public SqlOperand getOperand() {
		return SqlOperand.IS;
	}
}
