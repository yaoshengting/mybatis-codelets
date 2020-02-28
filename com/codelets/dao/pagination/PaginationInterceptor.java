/**
 * Copyright 2010 交通银行(tintinloan)
 * 
 * http://www.bankcomm.com
 */
package com.codelets.dao.pagination;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.codelets.support.condition.IPageable;
import com.codelets.support.condition.PageCond;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Intercepts(@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }))
public class PaginationInterceptor extends PageHelper {

	@Override
	public Object intercept(final Invocation invocation) throws Throwable {

		final Object[] args = invocation.getArgs();

		final Object parameter = args[1];
		PageCond pageCond = null;
		if (parameter instanceof IPageable) {
			pageCond = ((IPageable) parameter).extractPageCondition();
			if (null != pageCond) {
				int pageSize = pageCond.getPageSize();
				int curPage = pageCond.getStartRow() / pageSize + 1;
				PageHelper.startPage(curPage, pageSize);
			}
		}
		Object retObject = super.intercept(invocation);
		if (retObject instanceof Page) {
			if (pageCond != null) {
				pageCond.setCount(((Page) retObject).getTotal());
			}
		}
		return retObject;
	}
}
