/**
 * 
 */
package com.codelets.dao.condition.aggregate;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月22日 上午9:41:11
 * 
 * 实现功能：聚合条件
 */
public interface IAggregateCondition {

	/**
	 * @return 列名
	 */
	String getColumnName();

	/**
	 * 获得操作符
	 * 
	 * @return 操作符
	 */
	AggregateOperand getOperand();

}
