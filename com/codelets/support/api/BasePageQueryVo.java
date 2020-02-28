package com.codelets.support.api;

import java.sql.Timestamp;
import java.util.Date;

import com.codelets.support.condition.IPageable;
import com.codelets.support.condition.PageCond;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年12月27日 上午10:40:50
 * 
 * 实现功能：
 */
@Data
public abstract class BasePageQueryVo implements IPageable {
	private PageCond pageCond;

	@Override
	public PageCond extractPageCondition() {
		if (null == pageCond) {
			this.pageCond = new PageCond();
		}
		return pageCond;
	}

	public BasePageQueryVo() {
		super();
		pageCond = new PageCond();
	}

	/** 起始时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp beginTime;
	/** 截止时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp endTime;
	/** 起始日期 */
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date beginDate;
	/** 截止日期 */
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date endDate;
}
