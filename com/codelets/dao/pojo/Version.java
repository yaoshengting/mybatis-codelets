/**
 * 
 */
package com.codelets.dao.pojo;

/**
 * 
 */
public enum Version {
	/**
	 * 初始版本
	 */
	INIT(1),
	/**
	 * 版本跳跃步伐
	 */
	STEP(1);

	/**
	 * 获得初始版本
	 * 
	 * @return 初始版本数值
	 */
	public static int initVersion() {
		return INIT.num;
	}

	/**
	 * 获取下一版本
	 * 
	 * @param currentVersion
	 *            当前版本
	 * @return 下一版本
	 */
	public static int nextVersion(final int currentVersion) {
		return currentVersion + STEP.num;
	}

	/**
	 * 数据值
	 */
	private final int num;

	/**
	 * @param num
	 *            对应的数据值
	 */
	private Version(final int num) {
		this.num = num;
	}
}
