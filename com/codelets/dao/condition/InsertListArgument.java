/**
 * 
 */
package com.codelets.dao.condition;

import java.util.List;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年2月1日 下午2:28:50
 * 
 * 实现功能： 无主键批量插入
 */
public class InsertListArgument extends AbstractSqlTableArgument {
	private final List<String> columnNameList;
	private final List<List<Object>> valueList;

	/**
	 * @param tableName
	 *            表名
	 * @param columnNameList
	 *            列名列表
	 * @param valueList
	 *            值列表
	 */
	public InsertListArgument(final String tableName, final List<String> columnNameList,
			final List<List<Object>> valueList) {
		super(tableName);
		this.columnNameList = columnNameList;
		this.valueList = valueList;
	}

	/**
	 * @return {@link #columnNameList}
	 */
	public List<String> getColumnNameList() {
		return columnNameList;
	}

	/**
	 * @return {@link #valueList}
	 */
	public List<List<Object>> getValueList() {
		return valueList;
	}
}
