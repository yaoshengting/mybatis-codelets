/**
 * 
 */
package com.codelets.dao.condition.aggregate;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月20日 下午1:33:53
 * 
 * 实现功能：SUM聚合
 */
public class SumCondition extends AbstractAggregateCondition {

	/**
	 * @param columnName
	 *            列名
	 */
	public SumCondition(final String columnName) {
		super(columnName);
	}

	@Override
	public AggregateOperand getOperand() {
		return AggregateOperand.SUM;
	}
}
