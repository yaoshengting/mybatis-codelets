/**
 * 
 */
package com.codelets.dao.util;

import com.codelets.dao.core.impl.SingleTableDao;
import com.codelets.support.api.IBaseEntry;
import com.codelets.support.enumtype.DaoTypeEnum;

import lombok.Data;

/**
 * 表级别的元数据
 * 
 * @param <T>
 *            具体的entry类型
 * 
 */
@Data
public class TableMeta<T extends IBaseEntry> {

	/** dao类型 */
	private final DaoTypeEnum daoType;
	/** entry 类信息 */
	private final Class<T> entryClassInfo;
	/** 表名 */
	private final String tableName;

	/**
	 * 从dao中抽取元数据
	 * 
	 * @param <E>
	 *            表对象类型
	 * @param daoClass
	 *            dao类型
	 * @return 该dao中的源数据
	 */
	public static <E extends IBaseEntry> TableMeta<E> extractMeta(final Class<? extends SingleTableDao<E>> daoClass) {
		final Class<E> entryClassInfo = MetaDataExtractor.extractEntryClassInfo(daoClass);
		final DaoTypeEnum daoType = DaoTypeProcessor.extractDaoType(daoClass);
		final String tableName = DBTransfer.extractTableName(new TableMeta<E>(null, daoType, entryClassInfo), daoType);
		return new TableMeta<E>(tableName, daoType, entryClassInfo);
	}

	/**
	 * @param dbSchema
	 *            {@link #dbSchema}
	 * @param tableName
	 *            {@link #tableName}
	 * @param daoType
	 *            {@link #daoType}
	 * @param entryClassInfo
	 *            {@link #entryClassInfo}
	 */
	private TableMeta(final String tableName, final DaoTypeEnum daoType, final Class<T> entryClassInfo) {
		super();
		this.daoType = daoType;
		this.entryClassInfo = entryClassInfo;
		this.tableName = tableName;
	}
}
