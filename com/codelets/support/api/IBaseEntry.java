package com.codelets.support.api;

import static org.springframework.util.Assert.isTrue;

import java.lang.reflect.Field;
import java.sql.Timestamp;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import com.codelets.support.jackson.JsonTimeStampSerializer;
import com.codelets.support.util.PrimaryKeyFieldContainer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月15日 上午9:35:03
 * 
 * 实现功能：基础类，对应的是数据库中的一条记录，即DO
 */
@Data
public abstract class IBaseEntry implements IEntry {
	/** 建立时间 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = JsonTimeStampSerializer.class)
	private Timestamp createTime;
	/** 修改人 */
	private long modifyUser;
	/** 更新时间 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = JsonTimeStampSerializer.class)
	private Timestamp updateTime;
	/** 版本号 */
	private int version;

	/**
	 * 获取主键
	 * 
	 * @return 主键
	 */
	public long getPrimaryKey() {
		final Field primaryKeyField = getPrimaryKeyField();
		final boolean isAccessible = primaryKeyField.isAccessible();
		primaryKeyField.setAccessible(true);
		long primaryKey = -1;
		try {
			primaryKey = (Long) primaryKeyField.get(this);
		} catch (final IllegalAccessException e) {
			Assert.isTrue(false, "无法获取实体" + getClass().getName() + "主键");
		} finally {
			primaryKeyField.setAccessible(isAccessible);
		}
		return primaryKey;
	}

	/**
	 * 给Entry的主键字段赋值
	 * 
	 * @param primaryKey
	 */
	public void setPrimaryKey(final long primaryKey) {
		final Field primaryKeyField = getPrimaryKeyField();
		final boolean isAccessible = primaryKeyField.isAccessible();
		primaryKeyField.setAccessible(true);
		try {
			primaryKeyField.set(this, primaryKey);
		} catch (final IllegalAccessException e) {
			isTrue(false, "无法获取实体" + getClass().getName() + "主键");
		} finally {
			primaryKeyField.setAccessible(isAccessible);
		}
		return;
	}

	/**
	 * 获取主键Field
	 * 
	 * @return
	 */
	public Field getPrimaryKeyField() {
		return PrimaryKeyFieldContainer.extractPrimaryKeyField(this.getClass());
	}

	/**
	 * 判断Entry是否有主键，true表示有false表示无
	 * 
	 * @return
	 */
	public boolean hasPrimaryKeyField() {
		return PrimaryKeyFieldContainer.hasPrimaryKey(this.getClass());
	}

}
