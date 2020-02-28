package com.codelets.dao.condition.where;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.Assert;

import com.codelets.dao.util.DBTransfer;

/**
 * 根据传入的过滤参数Map<String, Object> columnsMap<br/>
 * 抽取出来SQL形式的过滤条件
 */
public final class ConditionFactory {
	/**
	 * 抽取条件列表
	 * 
	 * @param columnsMap
	 *            列名和值映射
	 * @return 对应的实体列表
	 */
	public static List<IColumnCondition> extractList(final Map<String, Object> columnsMap) {
		notEmpty(columnsMap, "待解析的数据不能为空");
		isTrue(!columnsMap.containsKey(null), "待解析的集合内不能包含空键值");

		final List<IColumnCondition> columnsList = new ArrayList<IColumnCondition>();
		for (final Entry<String, Object> entry : columnsMap.entrySet()) {
			final IColumnCondition condition = createCondition(entry);

			if (condition != null) {
				columnsList.add(condition);
			}
		}
		notEmpty(columnsList, "待解析的数据不能为空");
		return columnsList;
	}

	/**
	 * @param valueList
	 * @return
	 */
	private static Collection<?> assembleSqlValue(final Collection<?> valueList) {
		Assert.notEmpty(valueList, "不能处理空的值列表");
		final ArrayList<Object> resultList = new ArrayList<Object>();

		for (final Object value : valueList) {
			resultList.add(DBTransfer.assembleColumnValue(value));
		}

		return resultList;
	}

	/**
	 * 在根据列query、update、delete的时候，where条件如下面的形式 Map<String, Object> map = new
	 * HashMap<String, Object>(); map.put("XXX_YYY", value);
	 * 通过这个方法来根据值是NULL、Collection或其他情况来生成相应的过滤条件
	 * 
	 * @param entry
	 * @return
	 */
	private static IColumnCondition createCondition(final Entry<String, Object> entry) {
		if (entry.getValue() == null) {
			return new NullCondition(entry.getKey());
		} else if (entry.getValue() instanceof Collection<?>) {
			final Collection<?> values = (Collection<?>) entry.getValue();
			notEmpty(values, "放在in里的条件不允许为空");
			return new InCondition(entry.getKey(), assembleSqlValue(values));
		} else {
			return new EqCondition(entry.getKey(), DBTransfer.assembleColumnValue(entry.getValue()));
		}
	}

	/** */
	private ConditionFactory() {
		super();
	}
}
