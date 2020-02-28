package com.codelets.dao.pojo;

import lombok.Data;

/**
 * 
 * 作者： yaoshengting
 * 
 * 创建时间：2018-4-2 下午5:57:49
 * 
 * 实现功能：该Vo是代替单表操作中的update方法中UpdateArgument参数中更新的列和值的List的<br/>
 * 因为在新的mybatis.jar中(目前是3.4)执行这样的更新有问题，因此建了一个Vo代替Map&Entry这个内部类
 */
@Data
public class ColNameValue {
	/** 欲更新的列名 */
	private String columnName;
	/** 欲更新的值 */
	private Object columnValue;
}
