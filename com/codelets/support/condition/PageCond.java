package com.codelets.support.condition;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年12月1日 下午2:42:08
 * 
 * 实现功能：分页类
 */
public class PageCond implements java.io.Serializable {
	private static final long serialVersionUID = 2335513381253400473L;
	private static final long DEFAULT_COUNT = 0;
	private static final int DEFAULT_PAGENUM = 1;
	private static final int DEFAULT_PAGESIZE = 10;
	private static final int DEFAULT_START = 0;

	/** 表示查询结果的总条数 */
	private long count = DEFAULT_COUNT;
	/** 当前页 */
	private int currPage = DEFAULT_PAGENUM;
	/** 每页大小 */
	private int pageSize = DEFAULT_PAGESIZE;

	/** 表示当前页中的数据在结果集中的起始位置，缺省为0，因为SQL中分页的LIMIT 中记录行的偏移量是从0开始的 */
	private int startRow = DEFAULT_START;
	/** 表示每页显示数据的条数，缺省为10 */
	// private int limit = DEFAULT_LIMIT;
	/** = start + limit-1 */
	private int endRow = startRow + DEFAULT_PAGESIZE - 1;

	/** 总行数 */
	// private long totalRows;

	/** 默认设置，START=0,LIMIT=10,END=10 */
	public PageCond() {
		this.currPage = DEFAULT_PAGENUM;
		this.pageSize = DEFAULT_PAGESIZE;
		this.startRow = DEFAULT_START;
		this.endRow = startRow + DEFAULT_PAGESIZE - 1;
	}

	/**
	 * 当前页和页大小参数页面构造器
	 * 
	 * @param curPage
	 * @param pageSize
	 */
	public PageCond(final int currPage, final int pageSize) {
		this.currPage = currPage;
		this.pageSize = pageSize;
		this.startRow = pageSize * (currPage - 1);
		this.endRow = startRow + pageSize - 1;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
		recalculatePageInfo();
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		recalculatePageInfo();
	}

	public int getStartRow() {
		return startRow;
	}

	public int getEndRow() {
		return endRow;
	}

	/**
	 * 重新计算页面信息
	 */
	private void recalculatePageInfo() {
		Integer startRow = pageSize * (currPage - 1);
		Integer endRow = startRow + pageSize - 1;
		this.startRow = startRow;
		this.endRow = endRow;
	}
	/**
	 * @param start
	 *            {@link #start}
	 * @param limit
	 *            {@link #limit}
	 */
	// public PageCond(final int start, final int limit) {
	// this.start = start;
	// this.limit = limit;
	// this.end = start + limit - 1;
	// }

	/**
	 * @param limit
	 *            {@link #limit}
	 */
	// public void setLimit(final int limit) {
	// this.limit = limit;
	// this.setEnd(start + limit - 1);
	// }

	/**
	 * @param start
	 *            {@link #start}
	 */
	// public void setStart(final int start) {
	// this.start = start;
	// this.setEnd(start + limit - 1);
	// }

}
