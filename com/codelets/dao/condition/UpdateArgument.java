package com.codelets.dao.condition;

import java.util.List;

import com.codelets.dao.pojo.ColNameValue;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年2月1日 下午2:32:31
 * 
 * 实现功能：更新时的条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateArgument extends AbstractSqlWhereArgument {
	//更新的列名
	private List<String> columnNameList;
	private List<ColNameValue> toUpdateColumns;
	//更新的列值
	private List<Object> valueList;

	/**
	 * @param tableName
	 *            {@link AbstractSqlWhereArgument#tableName}
	 */
	public UpdateArgument(final String tableName) {
		super(tableName);
	}
}
