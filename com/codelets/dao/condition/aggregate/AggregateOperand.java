package com.codelets.dao.condition.aggregate;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月22日 上午9:42:06
 * 
 * 实现功能：聚合操作
 */
public enum AggregateOperand {

	/**
	 * 汇总
	 */
	SUM,
	/**
	 * 计算平均值
	 */
	AVG,
	/**
	 * 获取最大值
	 */
	MAX,
	/**
	 * 获取最小值
	 */
	MIN;
}
