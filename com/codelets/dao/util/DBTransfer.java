package com.codelets.dao.util;

import static org.apache.commons.lang.StringEscapeUtils.escapeSql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.converters.DateConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.codelets.support.annnotation.EntryPk;
import com.codelets.support.api.IBaseEntry;
import com.codelets.support.enumtype.DaoTypeEnum;
import com.codelets.support.util.NumericChecker;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年12月23日 下午2:01:51
 * 
 * 实现功能：Bean对象属性操作<br/>
 * 注意： java.util.Date是不被BeanUtils支持的，而它的子类java.sql.Date是被支持的。<br/>
 * 因此如果对象包含java.util.Date类型的属性，且希望被转换的时候， <br/>
 * 1.一定要使用java.sql.Date类型，否则会抛出异常。<br/>
 * 2.自定义并注册一个java.util.Date类型的转换器，这里注册的是dozer.jar中的日期转换器，如下在静态初始化方法中注册的转换器
 */
@Component
public final class DBTransfer {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String ERROR_ACCESS = "访问%s的属性%s出错";
	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// 注册一个日期类型的转换器
	static {
		ConvertUtils.register(new DateConverter(DATE_FORMAT), java.util.Date.class);
	}

	private DBTransfer() {
		super();
	}

	/**
	 * 收集列的值
	 * 
	 * @param value
	 *            需要转换的对象
	 * @return 该对象对应的列值
	 */
	public static Object assembleColumnValue(final Object value) {
		if (value == null) {
			return null;
		}
		// 判断值是否是数字类型的getClass()返回运行时的具体类的类型信息
		if (NumericChecker.isNumeric(value.getClass())) {
			return value;
		}
		// 判断值是否是Timestamp
		if (value instanceof java.sql.Timestamp) {
			return escapeSql(TIME_FORMAT.format(value));
		}
		if (value instanceof java.sql.Date) {
			return value;
		}
		// escapeSql 提供sql转移功能，防止sql注入攻击
		return escapeSql(String.valueOf(value));
	}

	/**
	 * 根据实体类提取列值
	 * 
	 * 
	 * @param entry
	 *            数据实体
	 * @param includePk
	 *            是否包含主键
	 * @return 列值
	 */
	public static List<Entry<String, Object>> extractColumnValue(final IBaseEntry entry, final boolean includePk) {
		final List<Entry<String, Object>> entryList = new ArrayList<Map.Entry<String, Object>>();
		// 从该类开始一级一级往上循环所继承的类，直到该类是IBaseEntry或Object类
		for (Class<?> current = entry.getClass(); (current != IBaseEntry.class)
				&& (current != Object.class); current = current.getSuperclass()) {
			// 获取当前类的所有字段属性
			for (final Field field : current.getDeclaredFields()) {
				// 如果插入的数据不包含主键&&该字段含有EntryPk注解(表示主键)，则continue，略过该字段
				if (!includePk && field.isAnnotationPresent(EntryPk.class)) {
					continue;
				}

				final boolean isAccessible = field.isAccessible();
				field.setAccessible(true);
				// 获取该字段的名称
				final String fieldName = field.getName();
				Assert.hasLength(fieldName, "属性名必须非空");
				// 当使用coverage插件时，会产生以$开头的成员变量
				if (fieldName.startsWith("$")) {
					continue;
				}
				/**
				 * add by yaoshengting 2019-10-25 start
				 */
				// 序列化属性名也忽略掉
				if (fieldName.equals("serialVersionUID")) {
					continue;
				}
				/**
				 * add by yaoshengting 2019-10-25 end
				 */
				// 将该字段转换成驼峰式结构的数据库表列名
				final String columnName = NamingUtils.transferCamelToUnderscoreFormat(fieldName);
				// 获取该字段的值
				final Object columnValue = extractColumnValue(entry, field);

				entryList.add(new SimpleEntry<String, Object>(columnName, columnValue));

				field.setAccessible(isAccessible);
			}
		}

		return entryList;
	}

	/**
	 * 提取主键名,使用 {@link EntryPk}标志主键
	 * 
	 * @param classInfo
	 *            类型信息
	 * @return 主键列名
	 */
	public static String extractPkName(final Class<? extends IBaseEntry> classInfo) {
		for (final Field field : classInfo.getDeclaredFields()) {
			if (field.isAnnotationPresent(EntryPk.class)) {
				Assert.hasText(field.getName());
				return NamingUtils.transferCamelToUnderscoreFormat(field.getName());
			}
		}
		Assert.isTrue(false, "无法提取主键名");
		return null;
	}

	/**
	 * 获取表名
	 * 
	 * @param daoType
	 *            dao类型
	 * @param tableMeta
	 *            表级别的元数据
	 * 
	 * @return 对应的表名
	 */
	public static String extractTableName(final TableMeta<? extends IBaseEntry> tableMeta, final DaoTypeEnum daoType) {
		final String normalName = extractTableName(tableMeta.getEntryClassInfo().getSimpleName());
		return daoType == null ? normalName : normalName + daoType.toString();
	}

	/**
	 * 将Map数据转换成相应的Entry类
	 * 
	 * @param <T>
	 *            实体类型
	 * @param resultMap
	 *            用来提取数据的映射
	 * @param entryClassInfo
	 *            实体信息
	 * @return 组装好的数据实体
	 */
	public static <T extends IBaseEntry> T transferToEntry(final Map<String, Object> resultMap,
			final Class<T> entryClassInfo) {
		final T result = newInstanse(entryClassInfo);

		for (final Entry<String, Object> entry : resultMap.entrySet()) {
			final String name = NamingUtils.transferUnderscoreToCamelFormat(entry.getKey());
			try {
				BeanUtils.setProperty(result, name, entry.getValue());
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(String.format(ERROR_ACCESS, entryClassInfo.getSimpleName(), name), e);
			} catch (final InvocationTargetException e) {
				throw new IllegalStateException(String.format(ERROR_ACCESS, entryClassInfo.getSimpleName(), name), e);
			} catch (final IllegalArgumentException e) {
				throw new IllegalStateException(String.format(ERROR_ACCESS, entryClassInfo.getSimpleName(), name), e);
			}
		}
		return result;
	}

	/**
	 * 将Map数据转换成相应的Vo类
	 * 
	 * @param resultMap
	 * @param entryClassInfo
	 * @return
	 */
	public static <T> T transferToVo(final Map<String, Object> resultMap, final Class<T> entryClassInfo) {
		final T result = newInstanse(entryClassInfo);

		for (final Entry<String, Object> entry : resultMap.entrySet()) {
			final String name = NamingUtils.transferUnderscoreToCamelFormat(entry.getKey());
			try {
				BeanUtils.setProperty(result, name, entry.getValue());
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(String.format(ERROR_ACCESS, entryClassInfo.getSimpleName(), name), e);
			} catch (final InvocationTargetException e) {
				throw new IllegalStateException(String.format(ERROR_ACCESS, entryClassInfo.getSimpleName(), name), e);
			} catch (final IllegalArgumentException e) {
				throw new IllegalStateException(String.format(ERROR_ACCESS, entryClassInfo.getSimpleName(), name), e);
			}
		}
		return result;
	}

	/**
	 * 提取属性值
	 * 
	 * @param entry
	 *            数据实体
	 * @param field
	 *            属性
	 * @return 该实体内该属性的值
	 */
	private static <T extends IBaseEntry> Object extractColumnValue(final T entry, final Field field) {
		Object value = null;
		try {
			value = field.get(entry);
		} catch (final IllegalArgumentException e) {
			throw new IllegalStateException(
					String.format(ERROR_ACCESS, entry.getClass().getSimpleName(), field.getName()), e);
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException(
					String.format(ERROR_ACCESS, entry.getClass().getSimpleName(), field.getName()), e);
		}

		return assembleColumnValue(value);
	}

	/**
	 * 在实体类型上提取表名，删除了Entry，并在除首字母外每个大写字母前加_
	 * 
	 * @param simpleName
	 *            类名
	 * 
	 * @return 实体对应的表名
	 */
	private static String extractTableName(final String simpleName) {
		final String className = simpleName.endsWith("Entry") ? simpleName.replaceAll("Entry", "") : simpleName;
		return NamingUtils.transferCamelToUnderscoreFormat(StringUtils.capitalize(className));
	}

	/**
	 * 实例化对象
	 * 
	 * @param entryClassInfo
	 * @return
	 */
	private static <T> T newInstanse(final Class<T> entryClassInfo) {
		try {
			return entryClassInfo.newInstance();
		} catch (final InstantiationException e) {
			throw new IllegalStateException("不能实例化" + entryClassInfo.getName(), e);
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException("不能实例化" + entryClassInfo.getName() + "可能默认构造函数私有", e);
		}
	}
}
