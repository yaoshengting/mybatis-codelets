package com.codelets.dao.core.impl;

import static com.codelets.dao.condition.where.ConditionFactory.extractList;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.codelets.dao.BaseDao;
import com.codelets.dao.annote.GenerateId;
import com.codelets.dao.condition.DeleteArgument;
import com.codelets.dao.condition.DeleteListArgument;
import com.codelets.dao.condition.InsertArgument;
import com.codelets.dao.condition.InsertListArgument;
import com.codelets.dao.condition.NewInsertListArgument;
import com.codelets.dao.condition.QueryAggregateArgument;
import com.codelets.dao.condition.QueryArgument;
import com.codelets.dao.condition.QueryPageArgument;
import com.codelets.dao.condition.QuerySumArgument;
import com.codelets.dao.condition.UpdateArgument;
import com.codelets.dao.condition.aggregate.IAggregateCondition;
import com.codelets.dao.condition.where.IColumnCondition;
import com.codelets.dao.core.IGenericHisDao;
import com.codelets.dao.core.IRawDao;
import com.codelets.dao.core.ISingleTableMapper;
import com.codelets.dao.pojo.ColNameValue;
import com.codelets.dao.pojo.TableNameValue;
import com.codelets.dao.util.DBTransfer;
import com.codelets.dao.util.EntryMetaContainer;
import com.codelets.dao.util.TableMeta;
import com.codelets.dao.util.VersionDecorator;
import com.codelets.support.annnotation.History;
import com.codelets.support.annnotation.ModifyUserEntry;
import com.codelets.support.api.IBaseEntry;
import com.codelets.support.condition.PageCond;
import com.codelets.support.enumtype.DaoTypeEnum;
import com.codelets.support.enumtype.OrderByEnum;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月15日 上午9:02:41
 * 
 * 实现功能：RawDao
 */
@Repository
class RawDao extends BaseDao implements IRawDao {
	// 修改人列
	private static final String MODIFY_USER_COLUMN = "MODIFY_USER";
	// 更新的数据列条数
	private static final int UPDATED_SIZE = 1;
	// 版本列
	private static final String VERSION_COLUMN = "VERSION";
	@Autowired
	// @Lazy
	private IGenericHisDao iGenericHisDao;
	@Autowired
	private ISingleTableMapper iSingleTableMapper;
	@Autowired
	private EntryMetaContainer tableNameContainer;
	@Autowired
	private VersionDecorator versionDecorator;

	@Override
	public <E extends IBaseEntry> int deleteByColumn(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValue) {
		notEmpty(columnValue, "用来删除的映射不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		if (daoType == DaoTypeEnum.HISTORY) {
			throw new UnsupportedOperationException("历史表不支持删除操作");
		}
		return iSingleTableMapper
				.delete(new DeleteArgument(extractTableName(tableMeta, daoType), extractList(columnValue)));
	}

	@Override
	public <E extends IBaseEntry> boolean deleteByEntryID(final TableMeta<E> tableMeta, final long entryID) {
		Assert.isTrue(entryID > 0, "实体主键必须大于0");

		final Map<String, Object> columnValues = new HashMap<String, Object>();
		columnValues.put(extractPkName(tableMeta.getEntryClassInfo()), entryID);
		// 这里的Delete没有对删除的条数为0的时候抛出异常
		// 而是通过返回true和false让业务层进行判断
		// 删除的条数>0，则返回true，否则返回false
		final int deleteRecord = deleteByColumn(tableMeta, columnValues);
		if (deleteRecord > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public <E extends IBaseEntry> boolean deleteListByIdList(final TableMeta<E> tableMeta, final List<Long> idList) {
		notEmpty(idList, "待删除的数据不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		if (daoType == DaoTypeEnum.HISTORY) {
			throw new UnsupportedOperationException("历史表不支持删除操作");
		}
		return idList.size() == iSingleTableMapper.deleteListByIdList(new DeleteListArgument(
				extractTableName(tableMeta, daoType), extractPkName(tableMeta.getEntryClassInfo()), idList));
	}

	@Override
	public <E extends IBaseEntry> long insert(final TableMeta<E> tableMeta, final IBaseEntry entry) {
		notNull(entry, "待新增的数据不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		final GenerateId generateIdAnnote = entry.getClass().getAnnotation(GenerateId.class);
		notNull(generateIdAnnote, "含有主键的数据插入必须在Entry类上使用GenerateId注解");
		Assert.isTrue(entry.hasPrimaryKeyField(), "含有主键的数据插入必须在Entry中使用EntryPk注解主键字段");
		if (entry.getClass().isAnnotationPresent(History.class)) {
			return this.insertWithHistory(tableMeta, entry, daoType);
		} else {
			return this.insertWithoutHistory(tableMeta, entry, daoType);
		}
	}

	/**
	 * 插入数据，包含历史表
	 * 
	 * @param tableMeta
	 * @param entry
	 * @param daoType
	 * @return
	 */
	private <E extends IBaseEntry> long insertWithHistory(final TableMeta<E> tableMeta, final IBaseEntry entry,
			final DaoTypeEnum daoType) {
		final GenerateId generateIdAnnote = entry.getClass().getAnnotation(GenerateId.class);
		final boolean isGenerate = generateIdAnnote.value();
		long primaryKey = 0L;

		if (isGenerate) {// 使用数据库自增主键
			final InsertArgument condition = assembleInsertCondition(tableMeta, entry, daoType, false);
			// 执行INSERT语句
			iSingleTableMapper.insert(condition);
			// 给Entry的主键字段赋值，该值从mybatis返回
			entry.setPrimaryKey(condition.getGenerateID());
			// 保存历史表
			saveHistory(tableMeta, entry, daoType);
			// 返回主键
			primaryKey = entry.getPrimaryKey();
		} else {// 使用自带主键
			final InsertArgument condition = assembleInsertCondition(tableMeta, entry, daoType, true);
			// 执行INSERT语句
			iSingleTableMapper.insertWithPK(condition);
			// 保存历史表
			saveHistory(tableMeta, entry, daoType);
		}
		return primaryKey;

	}

	/**
	 * 插入数据，不包含历史表
	 * 
	 * @param tableMeta
	 * @param entry
	 * @param daoType
	 * @return
	 */
	private <E extends IBaseEntry> long insertWithoutHistory(final TableMeta<E> tableMeta, final IBaseEntry entry,
			final DaoTypeEnum daoType) {
		final GenerateId generateIdAnnote = entry.getClass().getAnnotation(GenerateId.class);
		final boolean isGenerate = generateIdAnnote.value();
		if (isGenerate) {// 使用数据库自增主键
			// 组装插入参数
			final InsertArgument condition = assembleInsertCondition(tableMeta, entry, daoType, false);
			if (iSingleTableMapper.insert(condition) != 1) {
				return -1;
			}
			entry.setPrimaryKey(condition.getGenerateID());
			return entry.getPrimaryKey();
		} else {
			// 组装插入参数
			final InsertArgument condition = assembleInsertCondition(tableMeta, entry, daoType, true);
			// 执行INSERT语句
			if (iSingleTableMapper.insertWithPK(condition) != 1) {
				return -1;
			}
			// 返回主键
			return 0L;
		}
	}

	@Override
	public <E extends IBaseEntry> int insertListWithoutPK(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList) {
		notEmpty(entryList, "待新增的数据不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		if (entryList.get(0).getClass().isAnnotationPresent(History.class)) {
			return this.insertListWithoutPKWithHistory(tableMeta, entryList, daoType);
		} else {
			return this.insertListWithoutPKWithoutHistory(tableMeta, entryList, daoType);
		}
	}

	@Override
	public <E extends IBaseEntry> List<String> insertList(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList) {
		notEmpty(entryList, "待新增的数据不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		if (entryList.get(0).getClass().isAnnotationPresent(History.class)) {
			return this.insertListWithHistory(tableMeta, entryList, daoType);
		} else {
			return this.insertListWithoutHistory(tableMeta, entryList, daoType);
		}
	}

	/**
	 * 批量插入数据，不包含历史表，无主键，返回插入条数
	 * 
	 * @param tableMeta
	 * @param entryList
	 * @param daoType
	 * @param includePK
	 * @return
	 */
	private <E extends IBaseEntry> int insertListWithoutPKWithoutHistory(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList, final DaoTypeEnum daoType) {
		final InsertListArgument insertListArgument = assembleInsertCondition(tableMeta, entryList, daoType);
		final int insertNum = iSingleTableMapper.insertList(insertListArgument);
		return insertNum;
	}

	/**
	 * 批量插入-不包含历史表，返回主键List
	 * 
	 * @param tableMeta
	 * @param entryList
	 * @param daoType
	 * @return
	 */
	private <E extends IBaseEntry> List<String> insertListWithoutHistory(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList, final DaoTypeEnum daoType) {
		final NewInsertListArgument condition = newAssembleInsertCondition(tableMeta, entryList, daoType, false);

		final List<TableNameValue> colNameIdList = condition.getColNameValueId();
		if (colNameIdList != null && colNameIdList.size() > 0) {
			colNameIdList.get(0).setTableName(condition.getTableName());
		}
		final int insertNum = iSingleTableMapper.newInsertList(colNameIdList);
		List<String> pkList = new ArrayList<String>(insertNum);
		for (int i = 0; i < colNameIdList.size(); i++) {
			try {
				org.apache.commons.beanutils.BeanUtils.setProperty(entryList.get(i),
						entryList.get(i).getPrimaryKeyField().getName(), colNameIdList.get(i).getTableId());
				pkList.add(colNameIdList.get(i).getTableId());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				isTrue(false, "访问字段异常" + e.getMessage());
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				isTrue(false, "调用目标异常" + e.getMessage());
			}
		}
		return pkList;
	}

	/**
	 * 批量插入，包含历史表，无主键返回插入条数
	 * 
	 * @param tableMeta
	 * @param entryList
	 * @param daoType
	 * @param includePK
	 * @return
	 */
	private <E extends IBaseEntry> int insertListWithoutPKWithHistory(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList, final DaoTypeEnum daoType) {
		final InsertListArgument insertListArgument = assembleInsertCondition(tableMeta, entryList, daoType);
		final int insertNum = iSingleTableMapper.insertList(insertListArgument);
		saveListHistory(tableMeta, entryList, daoType);
		return insertNum;
	}

	/**
	 * 批量插入-包含历史表，返回主键List
	 * 
	 * @param tableMeta
	 * @param entryList
	 * @param daoType
	 * @return
	 */
	private <E extends IBaseEntry> List<String> insertListWithHistory(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList, final DaoTypeEnum daoType) {
		final NewInsertListArgument condition = newAssembleInsertCondition(tableMeta, entryList, daoType, false);
		final List<TableNameValue> colNameIdList = condition.getColNameValueId();
		if (colNameIdList != null && colNameIdList.size() > 0) {
			colNameIdList.get(0).setTableName(condition.getTableName());
		}
		final int insertNum = iSingleTableMapper.newInsertList(colNameIdList);
		List<String> pkList = new ArrayList<String>(insertNum);
		for (int i = 0; i < colNameIdList.size(); i++) {
			try {
				org.apache.commons.beanutils.BeanUtils.setProperty(entryList.get(i),
						entryList.get(i).getPrimaryKeyField().getName(), colNameIdList.get(i).getTableId());
				pkList.add(colNameIdList.get(i).getTableId());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				isTrue(false, "访问字段异常" + e.getMessage());
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				isTrue(false, "调用目标异常" + e.getMessage());
			}
		}
		saveListHistory(tableMeta, entryList, daoType);
		return pkList;

	}

	@Override
	public <E extends IBaseEntry> long insertWithoutPK(final TableMeta<E> tableMeta, final IBaseEntry entry) {
		notNull(entry, "待新增的数据不能为空");
		if (entry.getClass().isAnnotationPresent(History.class)) {
			return this.insertWithoutPKWithHistory(tableMeta, entry);
		} else {
			return this.insertWithoutPKWithoutHistory(tableMeta, entry);
		}
	}

	/**
	 * 无主键插入数据，包含历史表
	 * 
	 * @param tableMeta
	 * @param entry
	 * @param daoType
	 * @return
	 */
	private <E extends IBaseEntry> long insertWithoutPKWithHistory(final TableMeta<E> tableMeta,
			final IBaseEntry entry) {
		notNull(entry, "待新增的数据不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		final InsertArgument condition = assembleInsertCondition(tableMeta, entry, daoType, true);
		if (iSingleTableMapper.insertWithoutPK(condition) != 1) {
			return -1;
		}
		saveHistory(tableMeta, entry, daoType);
		return 0L;
	}

	/**
	 * 包含主键插入数据，不包含历史表
	 * 
	 * @param tableMeta
	 * @param entry
	 * @param daoType
	 * @return
	 */
	private <E extends IBaseEntry> long insertWithoutPKWithoutHistory(final TableMeta<E> tableMeta,
			final IBaseEntry entry) {
		notNull(entry, "待新增的数据不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		final InsertArgument condition = assembleInsertCondition(tableMeta, entry, daoType, true);
		if (iSingleTableMapper.insertWithoutPK(condition) != 1) {
			return -1;
		}
		return 0L;
	}

	@Override
	public <E extends IBaseEntry> E queryByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues) {
		return queryEntryByColumns(tableMeta, columnValues, null);
	}

	@Override
	public <E extends IBaseEntry> E queryEntryByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues, final Set<String> resultColumnsName) {
		notEmpty(columnValues, "用来查询的映射不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		final QueryArgument condition = new QueryArgument(extractTableName(tableMeta, daoType),
				extractList(columnValues), resultColumnsName);
		final List<Map<String, Object>> queryList = iSingleTableMapper.queryForList(condition);
		if (queryList.isEmpty()) {
			return null;
		} else if (queryList.size() == 1) {
			return DBTransfer.transferToEntry(queryList.get(0), tableMeta.getEntryClassInfo());
		} else {
			throw new IllegalArgumentException("查询应该返回一个对象,但是返回" + queryList.size() + "个");
		}
	}

	@Override
	public <E extends IBaseEntry> E queryByEntryID(final TableMeta<E> tableMeta, final long entryID) {
		Assert.isTrue(entryID > 0, "主键必须大于0");
		final Map<String, Object> columns = new HashMap<String, Object>();
		columns.put(extractPkName(tableMeta.getEntryClassInfo()), entryID);
		return queryByColumns(tableMeta, columns);
	}

	@Override
	public <E extends IBaseEntry> long queryEntryIdByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues) {
		final Set<String> resultColumnsName = new HashSet<String>(1);
		resultColumnsName.add(extractPkName(tableMeta.getEntryClassInfo()));
		final E queryResult = queryEntryByColumns(tableMeta, columnValues, resultColumnsName);
		return queryResult == null ? 0 : queryResult.getPrimaryKey();
	}

	@Override
	public <E extends IBaseEntry> List<Long> queryEntryIdListByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues) {
		final Set<String> resultColumnsName = new HashSet<String>(1);
		resultColumnsName.add(extractPkName(tableMeta.getEntryClassInfo()));
		final List<E> queryResult = queryListByColumns(tableMeta, columnValues, resultColumnsName);
		if (isEmpty(queryResult)) {
			return new ArrayList<Long>();
		}

		final ArrayList<Long> idResult = new ArrayList<Long>();
		for (final E e : queryResult) {
			idResult.add(e.getPrimaryKey());
		}
		return idResult;
	}

	@Override
	public <E extends IBaseEntry> List<E> queryListByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues) {
		return queryListByColumns(tableMeta, columnValues, null);
	}

	@Override
	public <E extends IBaseEntry> List<E> queryListByColumns(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues, final Set<String> resultColumnsName) {
		notEmpty(columnValues, "用来查询的映射不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		final QueryArgument condition = new QueryArgument(extractTableName(tableMeta, daoType),
				extractList(columnValues), resultColumnsName);

		final List<Map<String, Object>> resultMap = iSingleTableMapper.queryForList(condition);

		final List<E> resultList = new ArrayList<E>();
		for (final Map<String, Object> map : resultMap) {
			resultList.add(DBTransfer.transferToEntry(map, tableMeta.getEntryClassInfo()));
		}

		return resultList;
	}

	@Override
	public <E extends IBaseEntry> List<E> queryListByColumnsAndOrder(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues, final Set<String> resultColumnsName,
			final Map<String, OrderByEnum> orderColumns) {
		notEmpty(columnValues, "用来查询的映射不能为空");
		notEmpty(orderColumns, "用来排序的列不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		// 提取要查询的表名
		final String tableName = extractTableName(tableMeta, daoType);
		// 提取过滤条件
		final List<IColumnCondition> whereCondition = (null == columnValues ? null : extractList(columnValues));
		// 组装查询参数
		final QueryArgument condition = new QueryArgument(tableName, whereCondition, resultColumnsName, orderColumns);
		// 执行SQL
		final List<Map<String, Object>> resultMap = iSingleTableMapper.queryForList(condition);
		// 对查询数据进行转换
		if (resultMap != null) {
			final List<E> resultList = new ArrayList<E>();
			for (final Map<String, Object> map : resultMap) {
				resultList.add(DBTransfer.transferToEntry(map, tableMeta.getEntryClassInfo()));
			}
			return resultList;
		}
		return null;
	}

	@Override
	public <E extends IBaseEntry> List<E> queryListByColumnsWithOrderTopN(TableMeta<E> tableMeta,
			List<IColumnCondition> whereCondition, Set<String> resultColumnsName, Map<String, OrderByEnum> orderColumns,
			int topN) {
		notEmpty(whereCondition, "用来查询的映射不能为空");
		notEmpty(orderColumns, "用来排序的列不能为空");
		isTrue(topN > 0, "topN参数必须大于0");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		// 提取要查询的表名
		final String tableName = extractTableName(tableMeta, daoType);
		// 组装查询参数
		final QueryArgument condition = new QueryArgument(tableName, whereCondition, resultColumnsName, orderColumns,
				topN);
		// 执行SQL
		final List<Map<String, Object>> resultMap = iSingleTableMapper.queryForList(condition);
		// 对查询数据进行转换
		if (resultMap != null) {
			final List<E> resultList = new ArrayList<E>();
			for (final Map<String, Object> map : resultMap) {
				resultList.add(DBTransfer.transferToEntry(map, tableMeta.getEntryClassInfo()));
			}
			return resultList;
		}
		return null;
	}

	@Override
	public <E extends IBaseEntry> List<E> queryListByColumnsAndPage(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues, final PageCond pageCond) {
		notEmpty(columnValues, "用来查询的映射不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		final String tableName = extractTableName(tableMeta, daoType);
		final List<IColumnCondition> whereCondition = (null == columnValues ? null : extractList(columnValues));
		final QueryPageArgument condition = new QueryPageArgument(tableName, whereCondition, null, pageCond);

		final long resultCount = iSingleTableMapper.queryCount(condition);
		if (resultCount < 1) {
			return null;
		}
		pageCond.setCount(resultCount);

		final List<Map<String, Object>> resultMap = iSingleTableMapper.queryForListForPage(condition);
		final List<E> resultList = new ArrayList<E>();
		for (final Map<String, Object> map : resultMap) {
			resultList.add(DBTransfer.transferToEntry(map, tableMeta.getEntryClassInfo()));
		}

		return resultList;
	}

	@Override
	public <E extends IBaseEntry> List<E> queryListByColumnsAndPageAndOrder(final TableMeta<E> tableMeta,
			final Map<String, Object> columnValues, final PageCond pageCond,
			final Map<String, OrderByEnum> orderColumns) {
		notEmpty(orderColumns, "用来排序的列不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		final String tableName = extractTableName(tableMeta, daoType);
		final List<IColumnCondition> whereCondition = (null == columnValues ? null : extractList(columnValues));
		final QueryPageArgument condition = new QueryPageArgument(tableName, whereCondition, orderColumns, pageCond);
		final long resultCount = iSingleTableMapper.queryCount(condition);
		if (resultCount < 1) {
			return null;
		}
		pageCond.setCount(resultCount);
		final List<Map<String, Object>> resultMap = iSingleTableMapper.queryForListForPage(condition);
		final List<E> resultList = new ArrayList<E>();
		for (final Map<String, Object> map : resultMap) {
			resultList.add(DBTransfer.transferToEntry(map, tableMeta.getEntryClassInfo()));
		}

		return resultList;
	}

	@Override
	public <E extends IBaseEntry> List<E> queryListByColumnsAndPageAndOrder(final TableMeta<E> tableMeta,
			final List<IColumnCondition> whereCondition, final PageCond pageCond,
			final Map<String, OrderByEnum> orderColumns) {
		notEmpty(orderColumns, "用来排序的列不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		final String tableName = extractTableName(tableMeta, daoType);
		final QueryPageArgument condition = new QueryPageArgument(tableName, whereCondition, orderColumns, pageCond);
		final long resultCount = iSingleTableMapper.queryCount(condition);
		if (resultCount < 1) {
			return null;
		}
		pageCond.setCount(resultCount);
		final List<Map<String, Object>> resultMap = iSingleTableMapper.queryForListForPage(condition);
		final List<E> resultList = new ArrayList<E>();
		for (final Map<String, Object> map : resultMap) {
			resultList.add(DBTransfer.transferToEntry(map, tableMeta.getEntryClassInfo()));
		}

		return resultList;
	}

	@Override
	public <E extends IBaseEntry> boolean update(final TableMeta<E> tableMeta, final IBaseEntry tobeUpdate,
			final DaoTypeEnum daoType) {
		notNull(tobeUpdate, "待更新数据不能为空");
		isTrue(tobeUpdate.getPrimaryKey() > 0, "用来更新的数据实体" + tobeUpdate.getClass().getName() + "主键必须大于0");

		final Map<String, Object> columnValues = new HashMap<String, Object>();
		columnValues.put(extractPkName(tobeUpdate.getClass()), tobeUpdate.getPrimaryKey());
		return updateByColumns(tableMeta, tobeUpdate, columnValues, daoType);
	}

	private <E extends IBaseEntry> boolean updateByColumns(final TableMeta<E> tableMeta, final IBaseEntry tobeUpdate,
			final Map<String, Object> whereCondition, final DaoTypeEnum daoType) {

		notNull(tobeUpdate, "待更新数据不能为空");
		notEmpty(whereCondition, "更新条件不能为空");
		if (daoType == DaoTypeEnum.HISTORY) {
			throw new UnsupportedOperationException("历史表不支持更新操作");
		}

		final List<Entry<String, Object>> toUpdateValue = DBTransfer.extractColumnValue(tobeUpdate, false);

		final int nextVersion = versionDecorator.nextVersion(tobeUpdate, daoType);
		if (nextVersion > 0) {
			toUpdateValue.add(new SimpleEntry<String, Object>(VERSION_COLUMN, nextVersion));
		}

		final SimpleEntry<String, Object> modifyUserColumn = extractModifyUser(tobeUpdate);
		if (modifyUserColumn != null) {
			toUpdateValue.add(modifyUserColumn);
		}

		final List<String> columnNameList = new ArrayList<String>();
		final List<Object> valueList = new ArrayList<Object>();
		// add by yaoshengting ，使用ColNameValue对象代替Entry<String,Object>对象
		final List<ColNameValue> colNameValueList = new ArrayList<ColNameValue>();
		for (final Entry<String, Object> valueEntry : toUpdateValue) {
			columnNameList.add(valueEntry.getKey());
			valueList.add(valueEntry.getValue());
			// add by yaoshengting
			ColNameValue colNameValue = new ColNameValue();
			colNameValue.setColumnName(valueEntry.getKey());
			colNameValue.setColumnValue(valueEntry.getValue());
			colNameValueList.add(colNameValue);
		}

		final String tableName = extractTableName(tableMeta, daoType);
		final UpdateArgument condition = new UpdateArgument(tableName);
		condition.setColumnNameList(columnNameList);
		condition.setValueList(valueList);
		// modify by yaoshengting
		condition.setToUpdateColumns(colNameValueList);
		final int whereVersion = versionDecorator.extractWhereVersion(tobeUpdate, daoType);
		if (whereVersion > 0) {
			whereCondition.put(VERSION_COLUMN, whereVersion);
		}
		condition.setWhereColumns(extractList(whereCondition));

		if (iSingleTableMapper.update(condition) != UPDATED_SIZE) {
			return false;
		}

		tobeUpdate.setVersion(versionDecorator.nextVersion(tobeUpdate, daoType));
		return saveHistory(tableMeta, tobeUpdate, daoType);
	}

	@Override
	public <E extends IBaseEntry> boolean updateColumnsById(final TableMeta<E> tableMeta, final IBaseEntry tobeUpdate,
			final Set<String> columnNameSet) {
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		if (daoType == DaoTypeEnum.HISTORY) {
			throw new UnsupportedOperationException("历史表不支持更新操作");
		}
		notNull(tobeUpdate, "待更新数据不能为空");
		isTrue(tobeUpdate.getPrimaryKey() > 0, "用来更新的数据实体" + tobeUpdate.getClass().getName() + "主键必须大于0");
		notEmpty(columnNameSet, "至少需要制定一个待更新列");

		if (tobeUpdate.getClass().isAnnotationPresent(History.class)) {
			return this.updateColumnsByIdWithHistory(tableMeta, tobeUpdate, columnNameSet, daoType);
		} else {
			return this.updateColumnsByIdWithoutHistory(tableMeta, tobeUpdate, columnNameSet, daoType);
		}

	}

	/**
	 * 更新表指定列不插入历史表<br/>
	 * add by yaoshengting 2018/7/10
	 * 
	 * @param tableMeta
	 * @param tobeUpdate
	 * @param colNameValueList
	 * @param daoType
	 * @return
	 */
	private <E extends IBaseEntry> boolean updateColumnsByIdWithoutHistory(final TableMeta<E> tableMeta,
			final IBaseEntry tobeUpdate, final Set<String> columnNameSet, final DaoTypeEnum daoType) {
		final List<Entry<String, Object>> toUpdateValue = extractUpdateValueList(tobeUpdate, columnNameSet, daoType);
		final List<String> columnNameList = new ArrayList<String>();
		final List<Object> valueList = new ArrayList<Object>();
		// add by yaoshengting ，使用ColNameValue对象代替Entry<String,Object>对象
		final List<ColNameValue> colNameValueList = new ArrayList<ColNameValue>();
		for (final Entry<String, Object> valueEntry : toUpdateValue) {
			columnNameList.add(valueEntry.getKey());
			valueList.add(valueEntry.getValue());
			// add by yaoshengting
			ColNameValue colNameValue = new ColNameValue();
			colNameValue.setColumnName(valueEntry.getKey());
			colNameValue.setColumnValue(valueEntry.getValue());
			colNameValueList.add(colNameValue);
		}

		final String tableName = extractTableName(tableMeta, daoType);
		final UpdateArgument condition = new UpdateArgument(tableName);
		condition.setColumnNameList(columnNameList);
		condition.setValueList(valueList);
		// modify by yaoshengting
		condition.setToUpdateColumns(colNameValueList);
		final Map<String, Object> whereCondition = assembleWhereCondition(tobeUpdate, daoType);
		condition.setWhereColumns(extractList(whereCondition));
		final int updateRecord = iSingleTableMapper.update(condition);
		Assert.isTrue(updateRecord == UPDATED_SIZE, "更新记录条数为0");
		return true;
	}

	/**
	 * 更新数据库表并插入历史表<br/>
	 * add by yaoshengting 2018/7/10
	 * 
	 * @param tableMeta
	 * @param tobeUpdate
	 * @param columnNameSet
	 * @param daoType
	 * @return
	 */
	private <E extends IBaseEntry> boolean updateColumnsByIdWithHistory(final TableMeta<E> tableMeta,
			final IBaseEntry tobeUpdate, final Set<String> columnNameSet, final DaoTypeEnum daoType) {
		final List<Entry<String, Object>> toUpdateValue = extractUpdateValueList(tobeUpdate, columnNameSet, daoType);
		final List<String> columnNameList = new ArrayList<String>();
		final List<Object> valueList = new ArrayList<Object>();
		// add by yaoshengting ，使用ColNameValue对象代替Entry<String,Object>对象
		final List<ColNameValue> colNameValueList = new ArrayList<ColNameValue>();
		for (final Entry<String, Object> valueEntry : toUpdateValue) {
			columnNameList.add(valueEntry.getKey());
			valueList.add(valueEntry.getValue());
			// add by yaoshengting
			ColNameValue colNameValue = new ColNameValue();
			colNameValue.setColumnName(valueEntry.getKey());
			colNameValue.setColumnValue(valueEntry.getValue());
			colNameValueList.add(colNameValue);
		}

		final String tableName = extractTableName(tableMeta, daoType);
		final UpdateArgument condition = new UpdateArgument(tableName);
		condition.setColumnNameList(columnNameList);
		condition.setValueList(valueList);
		// modify by yaoshengting
		condition.setToUpdateColumns(colNameValueList);
		final Map<String, Object> whereCondition = assembleWhereCondition(tobeUpdate, daoType);
		condition.setWhereColumns(extractList(whereCondition));

		final int updateRecord = iSingleTableMapper.update(condition);
		Assert.isTrue(updateRecord == UPDATED_SIZE, "更新记录条数为0");

		tobeUpdate.setVersion(versionDecorator.nextVersion(tobeUpdate, daoType));
		return saveHistory(tableMeta, tobeUpdate, daoType);
	}

	/**
	 * 组装插入参数值
	 * 
	 * @param tableMeta
	 * @param entry
	 * @param daoType
	 * @param includePK
	 * @return
	 */
	private <E extends IBaseEntry> InsertArgument assembleInsertCondition(final TableMeta<E> tableMeta,
			final IBaseEntry entry, final DaoTypeEnum daoType, final boolean includePK) {
		final List<Entry<String, Object>> columnValue = DBTransfer.extractColumnValue(entry, includePK);
		// 增加创建时间列
		columnValue.add(new SimpleEntry<String, Object>("CREATE_TIME", entry.getCreateTime()));
		// 增加版本列
		final SimpleEntry<String, Object> versionColumn = versionDecorator.extractInsertVersion(entry, daoType);
		if (versionColumn != null) {
			columnValue.add(versionColumn);
		}
		// 增加修改人列
		final SimpleEntry<String, Object> modifyUserColumn = extractModifyUser(entry);
		if (modifyUserColumn != null) {
			columnValue.add(modifyUserColumn);
		}
		// 组装数据库列与值数组
		int capacitySize = columnValue.size();
		final List<String> columnNameList = new ArrayList<String>(capacitySize);
		final List<Object> valueList = new ArrayList<Object>(capacitySize);
		for (final Entry<String, Object> valueEntry : columnValue) {
			columnNameList.add(valueEntry.getKey());
			valueList.add(valueEntry.getValue());
		}

		// 封装插入参数
		final InsertArgument insertArgument = new InsertArgument(extractTableName(tableMeta, daoType));
		insertArgument.setColumnNameList(columnNameList);
		insertArgument.setValueList(valueList);

		return insertArgument;
	}

	/**
	 * @param entryList
	 * @param daoType
	 * @param includePK
	 * 
	 * @return
	 */
	private <E extends IBaseEntry> InsertListArgument assembleInsertCondition(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList, final DaoTypeEnum daoType) {
		final List<String> columnNameList = new ArrayList<String>();
		final List<List<Object>> insertList = new ArrayList<List<Object>>();
		for (final IBaseEntry baseEntry : entryList) {
			final List<Entry<String, Object>> columnValue = DBTransfer.extractColumnValue(baseEntry, false);
			// 增加创建时间
			columnValue.add(new SimpleEntry<String, Object>("CREATE_TIME", baseEntry.getCreateTime()));
			// 增加版本号
			final SimpleEntry<String, Object> versionColumn = versionDecorator.extractInsertVersion(baseEntry, daoType);
			if (versionColumn != null) {
				columnValue.add(versionColumn);
			}
			// 增加修改人
			final SimpleEntry<String, Object> modifyUserColumn = extractModifyUser(baseEntry);
			if (modifyUserColumn != null) {
				columnValue.add(modifyUserColumn);
			}
			// 插入的值
			final List<Object> valueList = new ArrayList<Object>();
			for (final Entry<String, Object> valueEntry : columnValue) {
				valueList.add(valueEntry.getValue());
			}
			insertList.add(valueList);
			// 如果插入的列名集合为空，则生成插入的列名
			if (CollectionUtils.isEmpty(columnNameList)) {
				for (final Entry<String, Object> valueEntry : columnValue) {
					columnNameList.add(valueEntry.getKey());
				}
			}
		}

		return new InsertListArgument(extractTableName(tableMeta, daoType), columnNameList, insertList);
	}

	private <E extends IBaseEntry> NewInsertListArgument newAssembleInsertCondition(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList, final DaoTypeEnum daoType, final boolean includePK) {
		final List<String> columnNameList = new ArrayList<String>();
		final List<TableNameValue> insertList = new ArrayList<TableNameValue>();
		Timestamp createTime = new Timestamp(System.currentTimeMillis());
		for (final IBaseEntry baseEntry : entryList) {
			final List<Entry<String, Object>> columnValue = DBTransfer.extractColumnValue(baseEntry, includePK);
			// modify yao wangjiapeng start
			baseEntry.setCreateTime(createTime);
			columnValue.add(new SimpleEntry<String, Object>("CREATE_TIME", createTime));
			// modify yao wangjiapeng end
			final SimpleEntry<String, Object> versionColumn = versionDecorator.extractInsertVersion(baseEntry, daoType);
			if (versionColumn != null) {
				columnValue.add(versionColumn);
			}

			final SimpleEntry<String, Object> modifyUserColumn = extractModifyUser(baseEntry);
			if (modifyUserColumn != null) {
				columnValue.add(modifyUserColumn);
			}

			final List<Object> valueList = new ArrayList<Object>();
			for (final Entry<String, Object> valueEntry : columnValue) {
				valueList.add(valueEntry.getValue());
			}
			TableNameValue colNameValueId = new TableNameValue();
			colNameValueId.setColumnValue(valueList);
			if (CollectionUtils.isEmpty(columnNameList)) {
				for (final Entry<String, Object> valueEntry : columnValue) {
					columnNameList.add(valueEntry.getKey());
				}
			}
			colNameValueId.setColumnName(columnNameList);
			insertList.add(colNameValueId);
		}

		return new NewInsertListArgument(extractTableName(tableMeta, daoType), insertList);
	}

	/**
	 * 提取Where条件，该方法被用于Update时提取where条件<br/>
	 * 在update的时候，会根据该Entry是否有版本号控制而增加Version条件<br/>
	 * 这样就是乐观锁的机制，通过版本号来控制更新
	 * 
	 * @param tobeUpdate
	 * @param daoType
	 * @return
	 */
	private Map<String, Object> assembleWhereCondition(final IBaseEntry tobeUpdate, final DaoTypeEnum daoType) {
		final Map<String, Object> whereCondition = new HashMap<String, Object>();
		whereCondition.put(extractPkName(tobeUpdate.getClass()), tobeUpdate.getPrimaryKey());

		final int whereVersion = versionDecorator.extractWhereVersion(tobeUpdate, daoType);
		// 如果whereVersion=0，表示该Entry不含版本号，whereVersion>0，表示该Entry含版本号
		if (whereVersion > 0) {
			whereCondition.put(VERSION_COLUMN, whereVersion);
		}
		return whereCondition;
	}

	/**
	 * @param entry
	 * @param daoType
	 * @return
	 */
	private SimpleEntry<String, Object> extractModifyUser(final IBaseEntry entry) {
		if (!entry.getClass().isAnnotationPresent(ModifyUserEntry.class)) {
			return null;
		}
		final long modifyUser = entry.getModifyUser();
		return new SimpleEntry<String, Object>(MODIFY_USER_COLUMN, String.valueOf(modifyUser));
	}

	/**
	 * @param entryClass
	 * @return
	 */
	private String extractPkName(final Class<? extends IBaseEntry> entryClass) {
		return tableNameContainer.extractPkName(entryClass);
	}

	/**
	 * @param classInfo
	 * @param daoType
	 * @return
	 */
	private <E extends IBaseEntry> String extractTableName(final TableMeta<E> tableMeta, final DaoTypeEnum daoType) {
		return tableNameContainer.extractTableName(tableMeta, daoType);
	}

	/**
	 * @param tobeUpdate
	 * @param columnNameSet
	 * @param daoType
	 * @return
	 */
	private List<Entry<String, Object>> extractUpdateValueList(final IBaseEntry tobeUpdate,
			final Set<String> columnNameSet, final DaoTypeEnum daoType) {
		final List<Entry<String, Object>> toUpdateValue = new ArrayList<Map.Entry<String, Object>>();
		for (final Entry<String, Object> entry : DBTransfer.extractColumnValue(tobeUpdate, false)) {
			if (columnNameSet.contains(entry.getKey())) {
				toUpdateValue.add(entry);
			}
		}
		notEmpty(toUpdateValue, "待更新的值不能为空");

		final int nextVersion = versionDecorator.nextVersion(tobeUpdate, daoType);
		if (nextVersion > 0) {
			toUpdateValue.add(new SimpleEntry<String, Object>(VERSION_COLUMN, nextVersion));
		}

		final SimpleEntry<String, Object> modifyUserColumn = extractModifyUser(tobeUpdate);
		if (modifyUserColumn != null) {
			toUpdateValue.add(modifyUserColumn);
		}
		return toUpdateValue;
	}

	/**
	 * @param tableMeta
	 * @param entry
	 * @param daoType
	 * @return
	 */
	public <E extends IBaseEntry> boolean saveHistory(final TableMeta<E> tableMeta, final IBaseEntry entry,
			final DaoTypeEnum daoType) {
		// 判断实体类是否有History主键
		if (!entry.getClass().isAnnotationPresent(History.class)) {
			return true;
		}

		if (daoType == DaoTypeEnum.HISTORY) {
			return true;
		}
		if (daoType == DaoTypeEnum.ORIGIN) {
			return true;
		}

		return iGenericHisDao.insert(tableMeta, entry) > 0;
	}

	/**
	 * @param tableMeta
	 * @param entryList
	 * @param daoType
	 * @return
	 */
	private <E extends IBaseEntry> boolean saveListHistory(final TableMeta<E> tableMeta,
			final List<? extends IBaseEntry> entryList, final DaoTypeEnum daoType) {
		if (CollectionUtils.isEmpty(entryList) || !entryList.get(0).getClass().isAnnotationPresent(History.class)) {
			return true;
		}

		@SuppressWarnings("unchecked")
		final List<IBaseEntry> insertEntryList = (List<IBaseEntry>) entryList;
		return iGenericHisDao.insertList(tableMeta, insertEntryList) > 0;
	}

	@Override
	public <E extends IBaseEntry> E querySumByColumns(TableMeta<E> tableMeta, Set<String> sumColumnSet,
			List<IColumnCondition> whereCondition) {
		notEmpty(sumColumnSet, "汇总列不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		final String tableName = extractTableName(tableMeta, daoType);
		final QuerySumArgument condition = new QuerySumArgument(tableName, whereCondition, sumColumnSet);
		final Map<String, Object> resultMap = iSingleTableMapper.querySumByColumns(condition);
		if (resultMap.isEmpty()) {
			return null;
		} else {
			return DBTransfer.transferToEntry(resultMap, tableMeta.getEntryClassInfo());
		}
	}

	@Override
	public <E extends IBaseEntry> E queryAggregateByColumns(TableMeta<E> tableMeta,
			final Set<IAggregateCondition> aggregateColumn, final List<IColumnCondition> whereCondition) {
		notEmpty(aggregateColumn, "汇总列不能为空");
		notEmpty(whereCondition, "条件列不能为空");
		final DaoTypeEnum daoType = tableMeta.getDaoType();
		final String tableName = extractTableName(tableMeta, daoType);
		final QueryAggregateArgument condition = new QueryAggregateArgument(tableName, whereCondition, aggregateColumn);
		final Map<String, Object> resultMap = iSingleTableMapper.queryAggregateByColumns(condition);
		if (resultMap.isEmpty()) {
			return null;
		} else {
			return DBTransfer.transferToEntry(resultMap, tableMeta.getEntryClassInfo());
		}
	}

	@Override
	public <E extends IBaseEntry> List<E> queryAll(TableMeta<E> tableMeta) {
		final QueryArgument condition = new QueryArgument(tableMeta.getTableName(), new ArrayList<IColumnCondition>());

		final List<Map<String, Object>> resultMap = iSingleTableMapper.queryForList(condition);

		final List<E> resultList = new ArrayList<E>();
		for (final Map<String, Object> map : resultMap) {
			resultList.add(DBTransfer.transferToEntry(map, tableMeta.getEntryClassInfo()));
		}
		return resultList;
	}

	@Override
	public <E extends IBaseEntry> long queryAllCount(TableMeta<E> tableMeta) {
		final QueryArgument condition = new QueryArgument(tableMeta.getTableName(), new ArrayList<IColumnCondition>());

		return iSingleTableMapper.queryCount(condition);
	}

	@Override
	public <E extends IBaseEntry> long queryCountByColumns(TableMeta<E> tableMeta, Map<String, Object> columnValues) {
		final QueryArgument condition = new QueryArgument(extractTableName(tableMeta, tableMeta.getDaoType()),
				extractList(columnValues));

		return iSingleTableMapper.queryCount(condition);
	}

}
