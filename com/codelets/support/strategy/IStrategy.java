/**
 * 
 */
package com.codelets.support.strategy;

/**
 * 策略接口
 * 
 * @param <C>
 *            策略条件
 * 
 */
public interface IStrategy<C> {
	/**
	 * 获得策略条件
	 * 
	 * @return 用来注册的策略处理条件
	 */
	C getCondition();
}
