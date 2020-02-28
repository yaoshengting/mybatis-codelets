package com.codelets.dao.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年12月23日 下午2:54:33
 * 
 * 实现功能：字段名与数据库列名互相转换<br/>
 * BiMap是Guava.jar包提供了一种新的集合类型，它提供了key和value的双向关联的数据结构<br/>
 */
public final class NamingUtils {
	/**
	 * 字段名和数据库表列名双向映射的数据结构<br/>
	 * 正向:字段名->数据库表列名 逆向:数据库表列名->字段名
	 */
	public static final BiMap<String, String> CAMEL_TO_UNDERSCORE = Maps
			.synchronizedBiMap(HashBiMap.<String, String>create());
	private static final String UNDER_LINE = "_";

	/**
	 * 将字符串从数据库下划线形式转换到驼峰形式
	 * 
	 * @param dbFormat
	 *            驼峰字符串
	 * @return 对应的数据库字符串
	 */
	public static String transferUnderscoreToCamelFormat(final String dbFormat) {
		Assert.isTrue(StringUtils.isNotBlank(dbFormat), "不能处理空字符串");

		final String result = CAMEL_TO_UNDERSCORE.inverse().get(dbFormat);
		if (result != null) {
			return result;
		}

		final String afterTransfer = assembleCamelFormat(dbFormat);

		if (afterTransfer != null) {
			CAMEL_TO_UNDERSCORE.inverse().put(dbFormat, afterTransfer);
		}
		return afterTransfer;
	}

	/**
	 * 将驼峰风格字符串转化为数据库下划线风格
	 * 
	 * @param camelFormat
	 *            驼峰风格的字符串
	 * @return 数据库风格
	 */
	public static String transferCamelToUnderscoreFormat(final String camelFormat) {
		Assert.isTrue(StringUtils.isNotBlank(camelFormat), "不能处理空字符串");

		// 先从BiMap<String, String>中根据key获取值
		final String result = CAMEL_TO_UNDERSCORE.get(camelFormat);
		if (result != null) {
			return result;
		}
		// 如果获取不到，则进行转换，并将转换后的值放到BiMap中
		final String afterTransfer = assembleUnderscoreFormat(camelFormat);

		if (afterTransfer != null) {
			CAMEL_TO_UNDERSCORE.put(camelFormat, afterTransfer);
		}
		return afterTransfer;
	}

	/**
	 * 提取驼峰式格式
	 * 
	 * @param dbFormat
	 * @return
	 */
	private static String assembleCamelFormat(final String dbFormat) {
		final StringBuffer camelFormatBuffer = new StringBuffer();
		// 通过_将字符串分割成字符串数组
		for (final String word : StringUtils.split(dbFormat, UNDER_LINE)) {
			if (StringUtils.isEmpty(word)) {
				continue;
			}
			final String lowerWord = word.toLowerCase();
			// 如果是开始字符串，则直接全是小写
			if (StringUtils.isEmpty(camelFormatBuffer)) {
				camelFormatBuffer.append(lowerWord);
			} else {
				// 否则将该字符串头一个字符大写，其他小写
				camelFormatBuffer.append(lowerWord.substring(0, 1).toUpperCase());
				camelFormatBuffer.append(lowerWord.substring(1));
			}
		}

		return camelFormatBuffer.toString();
	}

	/**
	 * 提取下划线格式
	 * 
	 * @param camelFormat
	 * @return
	 */
	private static String assembleUnderscoreFormat(final String camelFormat) {
		final StringBuffer dbFormatBuffer = new StringBuffer();

		// 循环字符串，除首字母之外，遇见大写字母则使用_分割
		for (int i = 0; i < camelFormat.length(); i++) {
			final char c = camelFormat.charAt(i);
			if (i > 0) {
				final boolean isUpperCase = (c >= 'A') && (c <= 'Z');
				if (isUpperCase) {
					dbFormatBuffer.append(UNDER_LINE);
				}
			}
			dbFormatBuffer.append(c);
		}
		return dbFormatBuffer.toString().toUpperCase();
	}

	/**  */
	private NamingUtils() {
		super();
	}

}
