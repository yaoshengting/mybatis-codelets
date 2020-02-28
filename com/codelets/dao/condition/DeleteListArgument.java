/**
 * 
 */
package com.codelets.dao.condition;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年2月1日 下午2:25:50
 * 
 * 实现功能：根据主键List批量删除数据的参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteListArgument extends AbstractSqlTableArgument {
	/**
	 * 表主键名
	 */
	private final String idColumnName;
	/**
	 * 主键值集合
	 */
	private final List<Long> valueList;

	/**
	 * @param tableName
	 *            表名
	 * @param idColumnName
	 *            主键列名
	 * @param valueList
	 *            值列表
	 */
	public DeleteListArgument(final String tableName, final String idColumnName, final List<Long> valueList) {
		super(tableName);
		this.idColumnName = idColumnName;
		this.valueList = valueList;
	}

}
