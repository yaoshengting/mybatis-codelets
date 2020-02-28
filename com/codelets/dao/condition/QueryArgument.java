package com.codelets.dao.condition;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import com.codelets.dao.condition.where.IColumnCondition;
import com.codelets.support.enumtype.OrderByEnum;

/**
 * 查询条件
 * 
 * 
 */
public class QueryArgument extends AbstractSqlWhereArgument {
	/**
	 * 返回列集合
	 */
	private static Set<String> defaultColumns = new HashSet<String>();
	static {
		defaultColumns.add("*");
	}
	/**
	 * 结果列名
	 */
	private final Set<String> resultColumns = new HashSet<String>();
	/**
	 * 排序集合
	 */
	private Map<String, OrderByEnum> orderMap = null;

	/**
	 * 取结果TopN
	 */
	private Integer topN = null;

	/**
	 * 
	 * @param tableName表名
	 * @param whereColumns过滤条件
	 */
	public QueryArgument(final String tableName, final List<IColumnCondition> whereColumns) {
		super(tableName, whereColumns);
	}

	/**
	 * 
	 * @param tableName表名
	 * @param whereColumns过滤条件
	 * @param resultColumns返回列
	 */
	public QueryArgument(final String tableName, final List<IColumnCondition> whereColumns,
			final Set<String> resultColumns) {
		super(tableName, whereColumns);
		if (!CollectionUtils.isEmpty(resultColumns)) {
			this.resultColumns.addAll(resultColumns);
		}
	}

	/**
	 * add by yaoshengting 2019/11/14
	 * 
	 * @param tableName表名
	 * @param whereColumns过滤条件
	 * @param orderMap排序字段
	 */
	public QueryArgument(final String tableName, final List<IColumnCondition> whereColumns,
			final Set<String> resultColumns, final Map<String, OrderByEnum> orderMap) {
		super(tableName, whereColumns);
		this.orderMap = orderMap;
	}

	/**
	 * 
	 * @param tableName
	 * @param whereColumns
	 * @param resultColumns
	 * @param orderMap
	 * @param topN
	 */
	public QueryArgument(final String tableName, final List<IColumnCondition> whereColumns,
			final Set<String> resultColumns, final Map<String, OrderByEnum> orderMap, final Integer topN) {
		super(tableName, whereColumns);
		this.orderMap = orderMap;
		this.topN = topN;
	}

	/**
	 * @return {@link #resultColumns}
	 */
	public Set<String> getResultColumns() {
		if (CollectionUtils.isEmpty(resultColumns)) {
			return defaultColumns;
		}
		return resultColumns;
	}

	public Map<String, OrderByEnum> getOrderMap() {
		return orderMap;
	}

	public Integer getTopN() {
		return topN;
	}

}
