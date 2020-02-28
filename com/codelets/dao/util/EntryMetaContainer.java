package com.codelets.dao.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.codelets.support.api.IBaseEntry;
import com.codelets.support.enumtype.DaoTypeEnum;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年2月1日 下午4:55:59
 * 
 * 实现功能：Entry元数据信息容器
 */
@Component
public class EntryMetaContainer {
	// Entry实体类与主键名的映射
	private final Map<Class<? extends IBaseEntry>, String> pkNameBuffer = new Hashtable<Class<? extends IBaseEntry>, String>();
	// Entry实体类与数据库表名的映射
	private final Map<Class<? extends IBaseEntry>, Map<DaoTypeEnum, String>> tableNameBuffer = new Hashtable<Class<? extends IBaseEntry>, Map<DaoTypeEnum, String>>();

	/**
	 * 提取主键名
	 * 
	 * @param entryClass
	 *            实体类型信息
	 * @return 主键名
	 */
	public String extractPkName(final Class<? extends IBaseEntry> entryClass) {
		// 根据实体类获取主键字段名
		String pkName = pkNameBuffer.get(entryClass);
		if (pkName != null) {
			return pkName;
		}
		// 如果在map缓存中不存在，则根据实体类进行提取主键字段名
		pkName = DBTransfer.extractPkName(entryClass);
		Assert.hasLength(pkName, "主键名不能为空");
		pkNameBuffer.put(entryClass, pkName);
		return pkName;
	}

	/**
	 * 获取表名
	 * 
	 * @param tableMeta
	 *            表级别的元数据 类型信息
	 * @param daoType
	 *            dao类型
	 * @return 该数据实体类型和dao类型对应的表名
	 */
	public String extractTableName(final TableMeta<? extends IBaseEntry> tableMeta, final DaoTypeEnum daoType) {
		final Class<? extends IBaseEntry> classInfo = tableMeta.getEntryClassInfo();
		// 根据实体类获取该实体类对应的原表、历史表名称映射
		Map<DaoTypeEnum, String> daoTypeTableNameMap = tableNameBuffer.get(classInfo);
		if (daoTypeTableNameMap == null) {
			tableNameBuffer.put(classInfo, new HashMap<DaoTypeEnum, String>());
		}
		daoTypeTableNameMap = tableNameBuffer.get(classInfo);

		String tableName = daoTypeTableNameMap.get(daoType);
		if (tableName != null) {
			return tableName;
		}
		// 如果map缓存中不存在，则提取表名
		tableName = DBTransfer.extractTableName(tableMeta, daoType);
		Assert.hasLength(tableName, "表名不能为空");
		daoTypeTableNameMap.put(daoType, tableName);
		return tableName;
	}
}
