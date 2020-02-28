/**
 * 
 */
package com.codelets.dao.condition.where;

/**
 * 查询条件
 */
public interface IColumnCondition {

	/**
	 * @return 列名
	 */
	String getColumnName();

	/**
	 * 获得操作符
	 * 
	 * @return 操作符
	 */
	SqlOperand getOperand();

	/**
	 * @return 列值
	 */
	Object getValue();
}
