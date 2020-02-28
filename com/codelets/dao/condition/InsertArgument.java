package com.codelets.dao.condition;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年2月1日 下午2:26:30
 * 
 * 实现功能：新增数据时的条件参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InsertArgument extends AbstractSqlTableArgument {
	//插入的列名
	private List<String> columnNameList;
	//返回的主键
	private long generateID;
	//列值
	private List<Object> valueList;

	/**
	 * @param tableName
	 *            {@link AbstractSqlWhereArgument#tableName}
	 */
	public InsertArgument(final String tableName) {
		super(tableName);
	}

}
