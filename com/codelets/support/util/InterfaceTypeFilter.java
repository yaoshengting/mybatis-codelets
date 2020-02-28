package com.codelets.support.util;

import static org.springframework.util.CollectionUtils.containsInstance;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * 定义 接口过滤器
 * 
 * 
 */
public final class InterfaceTypeFilter implements TypeFilter {
	private final Class<?> targetType;

	/**
	 * @param targetType
	 *            targetType
	 */
	public InterfaceTypeFilter(final Class<?> targetType) {
		this.targetType = targetType;
	}

	@Override
	public boolean match(final MetadataReader metadataReader, final MetadataReaderFactory metadataReaderFactory) throws IOException {
		final ClassMetadata metadata = metadataReader.getClassMetadata();
		final Class<?> cls = metadata.getClass();

		if (!metadata.isInterface()) {
			return false;
		}

		return containsInstance(Arrays.asList(cls.getInterfaces()), targetType);
	}

}
