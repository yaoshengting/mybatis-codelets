/**
 * 
 */
package com.codelets.dao.util;

import static com.codelets.dao.pojo.Version.initVersion;
import static org.springframework.util.Assert.isTrue;

import java.util.AbstractMap.SimpleEntry;

import org.springframework.stereotype.Component;

import com.codelets.dao.pojo.Version;
import com.codelets.support.annnotation.VersionEntry;
import com.codelets.support.api.IBaseEntry;
import com.codelets.support.enumtype.DaoTypeEnum;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年2月1日 下午5:08:21
 * 
 * 实现功能：版本号装饰器
 */
@Component
public class VersionDecorator {
	private static final String VERSION_COLUMN = "VERSION";

	/**
	 * @param entry
	 *            实体
	 * @param daoType
	 *            dao类型
	 * @return 添加版本信息
	 */
	public SimpleEntry<String, Object> extractInsertVersion(final IBaseEntry entry, final DaoTypeEnum daoType) {
		// 如果该实体类含有VersionEntry注解，表示有版本号
		if (isVersionedEntry(entry.getClass())) {
			// 如果Dao类型是正表(原表)或者版本号是0
			if ((daoType == null) || (entry.getVersion() == 0)) {
				// 赋值初始化版本号，值为1
				entry.setVersion(initVersion());
			}
			return new SimpleEntry<String, Object>(VERSION_COLUMN, entry.getVersion());
		}
		return null;
	}

	/**
	 * 提取where条件内的版本信息
	 * 
	 * @param tobeUpdate
	 *            待更新数据
	 * @param daoType
	 *            dao类型
	 * @return 需要在where内做版本判断的版本号，如果不需要判断则返回0,
	 */
	public int extractWhereVersion(final IBaseEntry tobeUpdate, final DaoTypeEnum daoType) {
		// 如果该实体类不含有VersionEntry注解，表示无版本号，直接返回0
		if (!isVersionedEntry(tobeUpdate.getClass())) {
			return 0;
		}
		/**
		 * daoType值为null表示是原表，更新原表时版本号必须>0 值为COPY表示是复制表，我们废弃不使用了
		 */
		// if ((daoType == null) || (daoType == COPY)) {
		if (daoType == null) {
			isTrue(tobeUpdate.getVersion() > 0, "版本化" + tobeUpdate.getClass() + "数据版本号必须大于0");
			return tobeUpdate.getVersion();
		}

		return 0;
	}

	/**
	 * 获取待更新数据的下一个版本号
	 * 
	 * @param tobeUpdate
	 *            待更新数据
	 * @param daoType
	 *            dao类型
	 * @return 下一版本号
	 */
	public int nextVersion(final IBaseEntry tobeUpdate, final DaoTypeEnum daoType) {
		// 如果该Entry是版本号控制的Entry
		if (isVersionedEntry(tobeUpdate.getClass())) {
			// 根据当前版本号+版本号跳跃步值来得到下一个版本号
			return Version.nextVersion(tobeUpdate.getVersion());
		}
		return 0;
	}

	/**
	 * 是否是带有版本号的Entry
	 * 
	 * @param entryClass
	 *            实体类型
	 * @return
	 */
	private boolean isVersionedEntry(final Class<? extends IBaseEntry> entryClass) {
		return entryClass.isAnnotationPresent(VersionEntry.class);
	}
}
