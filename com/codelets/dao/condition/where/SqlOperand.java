package com.codelets.dao.condition.where;

/**
 * 过滤操作符
 */
public enum SqlOperand {
	/**
	 * 
	 */
	EQ {
		@Override
		public String toString() {
			return "=";
		}

	},
	NE {
		@Override
		public String toString() {
			return "!=";
		}

	},
	/**
	 * 
	 */
	IN,
	/**
	 * 
	 */
	NOT_IN {
		@Override
		public String toString() {
			return "NOT IN";
		}

	},
	/**
	 * 
	 */
	IS,
	/**
	 * 
	 */
	GT {
		@Override
		public String toString() {
			return ">";
		}

	},
	GE {

		@Override
		public String toString() {
			return ">=";
		}

	},
	LT {

		@Override
		public String toString() {
			return "<";
		}

	},
	LE {

		@Override
		public String toString() {
			return "<=";
		}

	},
	LIKE {
		@Override
		public String toString() {
			return "LIKE";
		}

	},;
}
