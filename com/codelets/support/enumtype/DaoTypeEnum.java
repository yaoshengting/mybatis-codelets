/**
 * 
 */
package com.codelets.support.enumtype;

/**
 * dao类型信息
 * 
 * 
 */
public enum DaoTypeEnum {
	/**
	 * 历史时点信息
	 */
	HISTORY {
		@Override
		public String toString() {
			return "_HIS";
		}
	},
	/**
	 * 正本
	 */
	ORIGIN {
		@Override
		public String toString() {
			return "";
		}
	};
}
