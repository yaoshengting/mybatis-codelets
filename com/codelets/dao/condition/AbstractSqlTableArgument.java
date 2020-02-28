/**
 * 
 */
package com.codelets.dao.condition;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年2月1日 下午2:04:10
 * 
 * 实现功能：抽象TABLE SQL参数对象
 */
public abstract class AbstractSqlTableArgument {

	/**
	 * 表名
	 */
	private final String tableName;

	/**
	 * @param tableName
	 *            {@link #tableName}
	 * 
	 */
	protected AbstractSqlTableArgument(final String tableName) {
		super();
		this.tableName = tableName;
	}

	/**
	 * @return {@link #tableName}
	 */
	public String getTableName() {
		return tableName;
	}

}
