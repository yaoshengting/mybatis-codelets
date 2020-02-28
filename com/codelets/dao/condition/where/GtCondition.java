package com.codelets.dao.condition.where;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月20日 下午1:34:04
 * 
 * 实现功能：大于过滤条件
 */
public class GtCondition extends AbstractWhereCondition {

	/**
	 * 构造函数
	 * 
	 * @param columnName
	 * @param value
	 */
	public GtCondition(final String columnName, final Object value) {
		super(columnName, value);
	}

	/**
	 * 过滤操作符
	 */
	@Override
	public SqlOperand getOperand() {
		return SqlOperand.GT;
	}

}
