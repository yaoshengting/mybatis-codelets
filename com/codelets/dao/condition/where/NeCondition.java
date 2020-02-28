package com.codelets.dao.condition.where;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月20日 下午1:34:39
 * 
 * 实现功能：不等于过滤条件
 */
public class NeCondition extends AbstractWhereCondition {

	/**
	 * 构造函数
	 * 
	 * @param columnName
	 * @param value
	 */
	public NeCondition(final String columnName, final Object value) {
		super(columnName, value);
	}

	/**
	 * 过滤操作符
	 */
	@Override
	public SqlOperand getOperand() {
		return SqlOperand.NE;
	}

}
