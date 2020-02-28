package com.codelets.dao.pojo;

import java.util.List;

import lombok.Data;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2018年7月13日 上午10:35:14
 * 
 * 实现功能：批量插入时的List信息，因为要返回主键保存到历史表中，因此对insertList进行了修改
 */
@Data
public class TableNameValue {
	/** 插入的列名 */
	private List<String> columnName;
	/** 插入的值 */
	private List<Object> columnValue;
	/** 表主键ID */
	private String tableId;
	/** 表名 */
	private String tableName;
}
