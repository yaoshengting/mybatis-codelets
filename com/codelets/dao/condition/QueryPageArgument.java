package com.codelets.dao.condition;

import java.util.List;
import java.util.Map;

import com.codelets.dao.condition.where.IColumnCondition;
import com.codelets.support.condition.PageCond;
import com.codelets.support.enumtype.OrderByEnum;

/**
 * 用于组装分页查询条件
 * 
 */
public class QueryPageArgument extends QueryArgument {

	/**
	 * 分页条件
	 */
	private PageCond pageCond;

	/**
	 * 
	 * @param tableName表名
	 * @param whereColumns过滤条件
	 * @param orderColumns排序条件
	 * @param pageCond分页
	 */
	public QueryPageArgument(final String tableName, final List<IColumnCondition> whereColumns,
			final Map<String, OrderByEnum> orderColumns, final PageCond pageCond) {
		super(tableName, whereColumns, null, orderColumns);
		this.setPageCond(pageCond);
	}

	/**
	 * @return the pageCond
	 */
	public PageCond getPageCond() {
		if (null == this.pageCond) {
			return new PageCond();
		}
		return pageCond;
	}

	/**
	 * @param pageCond
	 *            the pageCond to set
	 */
	public void setPageCond(final PageCond pageCond) {
		this.pageCond = pageCond;
	}
}
