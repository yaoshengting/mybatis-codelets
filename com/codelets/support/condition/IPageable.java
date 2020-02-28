/*
 * create this file at 下午8:57:40  2013-12-7
 */
package com.codelets.support.condition;

/**
 * 用于分页查询的条件对象
 * 
 */
public interface IPageable {
	/**
	 * 获取分页条件
	 * 
	 * @return 分页条件
	 */
	PageCond extractPageCondition();
}
