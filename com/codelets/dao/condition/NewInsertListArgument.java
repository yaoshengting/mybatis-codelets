/**
 * 
 */
package com.codelets.dao.condition;

import java.util.List;

import com.codelets.dao.pojo.TableNameValue;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2018年7月13日 下午2:53:59
 * 
 * 实现功能：新的insertList参数类型
 */
public class NewInsertListArgument extends AbstractSqlTableArgument {
	private List<TableNameValue> colNameValueId;
    private String tableId;
    
	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public List<TableNameValue> getColNameValueId() {
		return colNameValueId;
	}

	public void setColNameValueId(List<TableNameValue> colNameValueId) {
		this.colNameValueId = colNameValueId;
	}

	/**
	 * @param tableName
	 *            表名
	 * @param columnNameList
	 *            列名列表
	 * @param valueList
	 *            值列表
	 */
	public NewInsertListArgument(final String tableName,final List<TableNameValue> colNameValueId) {
		super(tableName);
		this.colNameValueId = colNameValueId;
	}

	
}
