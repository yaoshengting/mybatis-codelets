package com.codelets.support.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.codelets.support.annnotation.EntryPk;
import com.codelets.support.api.IBaseEntry;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月15日 上午9:19:49
 * 
 * 实现功能：记录各个表的主键容器
 */
@Component
public class PrimaryKeyFieldContainer implements IPrimaryKeyFieldContainer {
	// Entry与主键映射容器
	private static final Map<Class<? extends IBaseEntry>, Field> PK_FIELDS = new HashMap<Class<? extends IBaseEntry>, Field>();

	/**
	 * 获取主键域
	 * 
	 * @param entryClass
	 *            实体类型
	 * @return 该实体类型被标注过annotation的字段
	 */
	public static Field extractPrimaryKeyField(final Class<? extends IBaseEntry> entryClass) {
		if (PK_FIELDS.containsKey(entryClass)) {
			return PK_FIELDS.get(entryClass);
		}
		for (final Field field : entryClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(EntryPk.class)) {
				PK_FIELDS.put(entryClass, field);
				return field;
			}
		}
		Assert.isTrue(false, "无法获取实体" + entryClass.getName() + "主键");
		return null;
	}

	/**
	 * 判断Entry是否有主键
	 * 
	 * @param entryClass
	 * @return
	 */
	public static boolean hasPrimaryKey(Class<? extends IBaseEntry> entryClass) {
		boolean hasPrimaryKey = false;
		if (PK_FIELDS.containsKey(entryClass)) {
			hasPrimaryKey = true;
		}
		for (final Field field : entryClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(EntryPk.class)) {
				PK_FIELDS.put(entryClass, field);
				hasPrimaryKey = true;
			}
		}
		return hasPrimaryKey;
	}

	@Override
	public String getPrimaryKeyFieldName(final Class<? extends IBaseEntry> entryClass) {
		return extractPrimaryKeyField(entryClass).getName();
	}
}
