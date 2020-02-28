package com.codelets.dao.core.impl;

import static org.springframework.util.Assert.isTrue;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.codelets.dao.BaseDao;
import com.codelets.dao.annote.GenerateId;
import com.codelets.dao.condition.aggregate.IAggregateCondition;
import com.codelets.dao.condition.where.IColumnCondition;
import com.codelets.dao.core.IRawDao;
import com.codelets.dao.core.ISingleTableDao;
import com.codelets.dao.util.TableMeta;
import com.codelets.support.api.IBaseEntry;
import com.codelets.support.condition.PageCond;
import com.codelets.support.enumtype.OrderByEnum;

/**
 * 单表操作类，可以通过继承该类获取单表的多个通用操作方法，一般情况下，可以直接通过biz层访问public方法，
 * protected方法需要通过子类的包装再对外提供服务
 * 
 * @param <T>
 *            操作实体
 * 
 */
public abstract class SingleTableDao<T extends IBaseEntry> extends BaseDao implements ISingleTableDao<T> {

	private TableMeta<T> meta;
	@Autowired
	private IRawDao rawDao;

	/**
	 * 为降低初始化时间，首次使用时才初始化
	 * 
	 * @return {@link #meta}
	 */
	private TableMeta<T> getMeta() {
		if (meta == null) {
			@SuppressWarnings("unchecked")
			final Class<? extends SingleTableDao<T>> daoClass = (Class<? extends SingleTableDao<T>>) this.getClass();
			meta = TableMeta.extractMeta(daoClass);
		}
		return meta;
	}

	@Override
	public boolean deleteByEntry(final T entry) {
		Assert.notNull(entry, "待删除数据不能为空");
		Assert.notNull(entry.getVersion(), "待删除数据版本号不能为空");
		Assert.isTrue(entry.getClass() == getMeta().getEntryClassInfo(), "待删除数据实体entry与表的entry不一致!");
		isTrue(entry.getPrimaryKey() > 0, "用来待删除的数据实体" + entry.getClass().getName() + "主键必须大于0");
		T orgin = rawDao.queryByEntryID(getMeta(), entry.getPrimaryKey());
		Assert.notNull(orgin, "待删除数据已经不存在");
		if (entry.getVersion() != orgin.getVersion())
			isTrue(false, "数据无效，无法对数据进行操作");
		return rawDao.deleteByEntryID(getMeta(), entry.getPrimaryKey());
	}

	@Override
	public boolean deleteEntryById(final Long entryId) {
		Assert.notNull(entryId, "主键不能为空");
		return rawDao.deleteByEntryID(getMeta(), entryId);
	}

	@Override
	public long insert(final T entry) {
		Assert.notNull(entry, "待新增的数据不能为空");
		entry.setCreateTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		Assert.isTrue(entry.getClass() == getMeta().getEntryClassInfo(), "待插入的数据实体entry与表的entry不一致!");
		// 获取该Entry的GenerateId注解
		final GenerateId generateIdAnnote = entry.getClass().getAnnotation(GenerateId.class);
		if (generateIdAnnote == null) {
			// 没有主键
			return rawDao.insertWithoutPK(getMeta(), entry);
		} else {
			// 是有数据库自增主键或自带主键
			return rawDao.insert(getMeta(), entry);
		}
	}

	/**
	 * 查询表内所有记录
	 * 
	 * @return 对应的实体列表
	 */
	@Override
	public List<T> queryAll() {
		return rawDao.queryAll(getMeta());
	}

	/**
	 * 查询表内数据数量, 请让单表接口继承
	 * 
	 * 
	 * @return 总数
	 */
	@Override
	public long queryAllCount() {
		return rawDao.queryAllCount(getMeta());
	}

	@Override
	public T queryByID(final long entryID) {
		return rawDao.queryByEntryID(getMeta(), entryID);
	}

	/**
	 * 使用列名值映射删除数据
	 * 
	 * 对biz层直接暴露该接口需要说明为什么一定要使用该接口
	 * 
	 * @param columnValue
	 *            列名值映射
	 * @return 删除条数
	 */
	protected int deleteByColumn(final Map<String, Object> columnValue) {
		return rawDao.deleteByColumn(getMeta(), columnValue);
	}

	/**
	 * @param idList
	 *            实体主键list
	 * 
	 * @return 删除是否成功
	 */
	protected boolean deleteListByIdList(final List<Long> idList) {
		return rawDao.deleteListByIdList(getMeta(), idList);
	}

	/**
	 * 批量插入返回主键List
	 * 
	 * @param entryList
	 * @return
	 */
	@Override
	public List<String> insertList(final List<T> entryList) {
		return rawDao.insertList(getMeta(), entryList);
	}

	/**
	 * 批量插入返回条数
	 * 
	 * @param entryList
	 * @return
	 */
	@Override
	public int insertListWithoutPK(final List<T> entryList) {
		return rawDao.insertListWithoutPK(getMeta(), entryList);
	}

	/**
	 * 根据列查询一条记录，使用该方法的时候请确保传入的参数能够唯一确定一条记录。<br/>
	 * 如果查询的结果有多条，则抛出异常
	 * 
	 * @param columnValues
	 * @return
	 */
	protected T queryOneEntryByColumns(final Map<String, Object> columnValues) {
		return rawDao.queryByColumns(getMeta(), columnValues);
	}

	/**
	 * 根据条件查询表内数据数量
	 * 
	 * 对biz层直接暴露该接口需要说明为什么一定要使用该接口
	 * 
	 * @param columnValues
	 *            条件
	 * @return 符合条件的数据数量
	 */
	@Override
	public long queryCountByColumns(final Map<String, Object> columnValues) {
		return rawDao.queryCountByColumns(getMeta(), columnValues);
	}

	/**
	 * 使用列名和值查询实体列表
	 * 
	 * 对biz层直接暴露该接口需要说明为什么一定要使用该接口
	 * 
	 * @param columnValues
	 *            列名和值映射，当值为列表时，采用in做为操作符
	 * @return 对应的实体列表，如果不存在返回null
	 */
	@Override
	public List<T> queryListByColumns(final Map<String, Object> columnValues) {
		return rawDao.queryListByColumns(getMeta(), columnValues);
	}

	/**
	 * 按照指定列名，使用列名和值查询实体列表
	 * 
	 * 对biz层直接暴露该接口需要说明为什么一定要使用该接口
	 * 
	 * @param columnValues
	 *            列名和值映射，当值为列表时，采用in做为操作符
	 * @param resultColumnsName
	 *            指定的返回列
	 * @return 对应的实体列表，如果不存在返回空列表
	 */
	protected List<T> queryListByColumns(final Map<String, Object> columnValues, final Set<String> resultColumnsName) {
		return rawDao.queryListByColumns(getMeta(), columnValues, resultColumnsName);
	}

	/**
	 * 根据列查询数据并进行排序
	 * 
	 * @param columnValues
	 * @param orderColumns
	 * @return
	 */
	@Override
	public List<T> queryListByColumnsWithOrder(final Map<String, Object> columnValues,
			final Map<String, OrderByEnum> orderColumns) {
		return rawDao.queryListByColumnsAndOrder(getMeta(), columnValues, null, orderColumns);
	}

	/**
	 * 根据列查询并排序，取结果的topN条记录
	 * 
	 * @param columnValues
	 * @param orderColumns
	 * @param topN
	 * @return
	 */
	@Override
	public List<T> queryListByColumnsWithOrderTopN(final List<IColumnCondition> whereCondition,
			final Map<String, OrderByEnum> orderColumns, final int topN) {
		return rawDao.queryListByColumnsWithOrderTopN(getMeta(), whereCondition, null, orderColumns, topN);
	}

	/**
	 * 按照主键更新制定列值
	 * 
	 * 对biz层直接暴露该接口需要说明为什么一定要使用该接口
	 * 
	 * @param tobeUpdate
	 *            待更新的数据
	 * @param columnNameSet
	 *            列名集合
	 * @return 是否更新成功
	 */
	private boolean updateColumnsById(final T tobeUpdate, final Set<String> columnNameSet) {
		Assert.isTrue(tobeUpdate.getClass() == getMeta().getEntryClassInfo(), "待更新的数据实体entry与表的entry不一致!");
		isTrue(tobeUpdate.getPrimaryKey() > 0, "用来更新的数据实体" + tobeUpdate.getClass().getName() + "主键必须大于0");
		Assert.notNull(tobeUpdate.getVersion(), "待更新数据版本号不能为空");
		Assert.notEmpty(columnNameSet, "欲更新的列不能为空");
		T orgin = rawDao.queryByEntryID(getMeta(), tobeUpdate.getPrimaryKey());
		Assert.notNull(orgin, "待更新数据已经不存在");
		if (tobeUpdate.getVersion() != orgin.getVersion()) {
			isTrue(false, "数据无效，无法对数据进行操作");
		}
		return rawDao.updateColumnsById(getMeta(), tobeUpdate, columnNameSet);
	}

	/**
	 * 
	 * 按照主键更新制定列值 add by yaoshengting 2018/6/29
	 * 
	 */
	@Override
	public boolean updateEntryColumnsByID(final T tobeUpdate, final Set<String> columnNameSet) {
		return this.updateColumnsById(tobeUpdate, columnNameSet);
	}

	@Override
	public List<T> queryListByColumnsWithPageOrder(final Map<String, Object> columnValues, final PageCond pageCond,
			final Map<String, OrderByEnum> orderColumns) {
		return rawDao.queryListByColumnsAndPageAndOrder(getMeta(), columnValues, pageCond, orderColumns);
	}

	@Override
	public List<T> queryListByColumnsWithPageOrder(final List<IColumnCondition> whereCondition, final PageCond pageCond,
			final Map<String, OrderByEnum> orderColumns) {
		return rawDao.queryListByColumnsAndPageAndOrder(getMeta(), whereCondition, pageCond, orderColumns);
	}

	/**
	 * 使用列名和值分页查询实体列表
	 * 
	 * @param columnValues
	 *            列名和值映射，当值为列表时，采用in做为操作符
	 * @param pageCond
	 *            分页条件
	 * @return 对应的实体列表，如果不存在返回null
	 */
	@Override
	public List<T> queryListByColumnsWithPage(final Map<String, Object> columnValues, final PageCond pageCond) {
		return rawDao.queryListByColumnsAndPage(getMeta(), columnValues, pageCond);
	}

	/**
	 * 根据条件查询并汇总相应的列值
	 * 
	 * @param sumColumnSet
	 * @param columnValues
	 * @return
	 */
	@Override
	public T querySumByColumns(final Set<String> sumColumnSet, final List<IColumnCondition> whereCondition) {
		return rawDao.querySumByColumns(getMeta(), sumColumnSet, whereCondition);
	}

	@Override
	public T queryAggregateByColumns(final Set<IAggregateCondition> aggregateColumn,
			final List<IColumnCondition> whereCondition) {
		return rawDao.queryAggregateByColumns(getMeta(), aggregateColumn, whereCondition);
	}
}
