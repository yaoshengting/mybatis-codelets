package com.codelets.dao.core.impl;

import static com.codelets.support.enumtype.DaoTypeEnum.HISTORY;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.sql.Timestamp;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.codelets.dao.BaseDao;
import com.codelets.dao.condition.InsertArgument;
import com.codelets.dao.condition.InsertListArgument;
import com.codelets.dao.core.IGenericHisDao;
import com.codelets.dao.core.IGenericHisMapper;
import com.codelets.dao.core.ISingleTableMapper;
import com.codelets.dao.util.DBTransfer;
import com.codelets.dao.util.EntryMetaContainer;
import com.codelets.dao.util.TableMeta;
import com.codelets.support.annnotation.DaoType;
import com.codelets.support.annnotation.ModifyUserEntry;
import com.codelets.support.annnotation.VersionEntry;
import com.codelets.support.api.IBaseEntry;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月23日 下午4:15:40
 * 
 * 实现功能：历史表通用DAO实现类
 */
@Repository
@DaoType(type = HISTORY)
public class GenericHisDaoImpl extends BaseDao implements IGenericHisDao {
	// 修改人列
	private static final String MODIFY_USER_COLUMN = "MODIFY_USER";
	// 版本列
	private static final String VERSION_COLUMN = "VERSION";
	@Autowired
	private IGenericHisMapper iGenericHisMapper;
	@Autowired
	private ISingleTableMapper iSingleTableMapper;
	@Autowired
	private EntryMetaContainer tableNameBuffer;

	@Override
	public <E extends IBaseEntry> long insert(final TableMeta<E> tableMeta, final IBaseEntry entry) {
		notNull(entry, "待新增的数据不能为空");
		// 实体类有版本号
		if (entry.getClass().isAnnotationPresent(VersionEntry.class)) {
			isTrue(entry.getVersion() > 0, "有版本信息的实体在做历史记录时版本号必须大于0");
		}
		// 判断该Entry是否含有主键域
		if (entry.hasPrimaryKeyField()) {
			isTrue(entry.getPrimaryKey() > 0, "带主键自增时实体主键必须大于0");
		}

		// 提取要插入的列名和值
		final List<Entry<String, Object>> columnValue = DBTransfer.extractColumnValue(entry, true);
		// 增加创建时间列
		columnValue.add(new SimpleEntry<String, Object>("CREATE_TIME", entry.getCreateTime()));
		// 如果Entry中有版本号，则增加版本号数据列和值
		if (entry.getClass().isAnnotationPresent(VersionEntry.class)) {
			int nextVersion = entry.getVersion();
			final Entry<String, Object> versionColumn = new SimpleEntry<String, Object>(VERSION_COLUMN,
					String.valueOf(nextVersion));
			columnValue.add(versionColumn);
		}

		// 增加修改人列
		if (entry.getClass().isAnnotationPresent(ModifyUserEntry.class)) {
			final long modifyUser = entry.getModifyUser();
			final Entry<String, Object> modifyColumn = new SimpleEntry<String, Object>(MODIFY_USER_COLUMN,
					String.valueOf(modifyUser));
			columnValue.add(modifyColumn);
		}
		// 组装插入的数据库列和值
		final List<String> columnNameList = new ArrayList<String>();
		final List<Object> valueList = new ArrayList<Object>();
		for (final Entry<String, Object> valueEntry : columnValue) {
			columnNameList.add(valueEntry.getKey());
			valueList.add(valueEntry.getValue());
		}

		// 提取数据库表名并组装插入参数
		final InsertArgument condition = new InsertArgument(tableNameBuffer.extractTableName(tableMeta, HISTORY));
		condition.setColumnNameList(columnNameList);
		condition.setValueList(valueList);

		// 执行SQL
		if (iSingleTableMapper.insertWithPK(condition) != 1) {
			return -1;
		}
		// 判断该Entry是否含有主键域
		if (entry.hasPrimaryKeyField()) {
			return entry.getPrimaryKey();
		} else {
			return 0L;
		}
	}

	@Override
	public <E extends IBaseEntry> int insertList(final TableMeta<E> tableMeta, final List<IBaseEntry> entryList) {
		notEmpty(entryList, "待新增的数据不能为空");

		final InsertListArgument condition = assembleInsertCondition(tableMeta, entryList);

		return iSingleTableMapper.insertList(condition);
	}

	/**
	 * 提取插入条件
	 * 
	 * @param tableMeta
	 * @param entryList
	 * @return
	 */
	private <E extends IBaseEntry> InsertListArgument assembleInsertCondition(final TableMeta<E> tableMeta,
			final List<IBaseEntry> entryList) {
		final List<String> columnNameList = new ArrayList<String>();
		final List<List<Object>> insertList = new ArrayList<List<Object>>();
		Timestamp createTime = new Timestamp(System.currentTimeMillis());
		for (final IBaseEntry entry : entryList) {
			isTrue(entry.getPrimaryKey() > 0, "主键必须大于0");
			notNull(entry, "待新增的数据不能为空");
			if (entry.getClass().isAnnotationPresent(VersionEntry.class)) {
				isTrue(entry.getVersion() > 0, "有版本信息的实体在做历史记录时版本号必须大于0");
			}
			isTrue(entry.getPrimaryKey() > 0, "带主键自增时实体主键必须大于0");

			final List<Entry<String, Object>> columnValue = DBTransfer.extractColumnValue(entry, true);

			int nextVersion = 0;
			if (entry.getClass().isAnnotationPresent(VersionEntry.class)) {
				nextVersion = entry.getVersion();
			}
			final Entry<String, Object> versionColumn = new SimpleEntry<String, Object>(VERSION_COLUMN,
					String.valueOf(nextVersion));
			columnValue.add(versionColumn);
			// 增加创建时间
			columnValue.add(new SimpleEntry<String, Object>("CREATE_TIME", createTime));
			if (entry.getClass().isAnnotationPresent(ModifyUserEntry.class)) {
				final long modifyUser = entry.getModifyUser();
				final Entry<String, Object> modifyColumn = new SimpleEntry<String, Object>(MODIFY_USER_COLUMN,
						String.valueOf(modifyUser));
				columnValue.add(modifyColumn);
			}

			final List<Object> valueList = new ArrayList<Object>();
			for (final Entry<String, Object> valueEntry : columnValue) {
				valueList.add(valueEntry.getValue());
			}
			insertList.add(valueList);

			if (CollectionUtils.isEmpty(columnNameList)) {
				for (final Entry<String, Object> valueEntry : columnValue) {
					columnNameList.add(valueEntry.getKey());
				}
			}
		}

		return new InsertListArgument(tableNameBuffer.extractTableName(tableMeta, HISTORY), columnNameList, insertList);
	}
}
