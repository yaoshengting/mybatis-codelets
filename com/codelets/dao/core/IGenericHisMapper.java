package com.codelets.dao.core;

import org.springframework.stereotype.Repository;

import com.codelets.dao.condition.QueryArgument;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月23日 下午4:20:02
 * 
 * 实现功能：历史表通用Mapper
 */
@Repository
public interface IGenericHisMapper {

	/**
	 * 查询最小版本号
	 * 
	 * @param queryArgument
	 *            查询参数
	 * @return 版本号
	 */
	int queryFirstVersion(final QueryArgument queryArgument);

	/**
	 * 查询最大版本号
	 * 
	 * @param queryArgument
	 *            查询参数
	 * @return 版本号
	 */
	int queryLastVersion(final QueryArgument queryArgument);

}
