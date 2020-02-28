package com.codelets.support.jackson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 作者：yaoshengting
 * 
 * 创建时间：2015-6-23 上午11:06:19
 * 
 * 实现功能：在序列化时指定日期的格式，默认情况下日期是以时间戳的形式展示的
 */
public class JsonDateSerializer extends JsonSerializer<Date> {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void serialize(final Date date, final JsonGenerator gen, final SerializerProvider provider) throws IOException, JsonProcessingException {
		String value = dateFormat.format(date);
		gen.writeString(value);
	}
}
