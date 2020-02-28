package com.codelets.support.jackson;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 作者：yaoshengting
 * 
 * 创建时间：2015-6-23 上午11:09:03
 * 
 * 实现功能：在序列化时指定时间的格式，默认情况下时间是一时间戳的形式展示的
 */
public class JsonTimeStampSerializer extends JsonSerializer<Timestamp> {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public void serialize(final Timestamp timestamp, final JsonGenerator gen, final SerializerProvider provider) throws IOException,
			JsonProcessingException {
		String value = dateFormat.format(timestamp);
		gen.writeString(value);
	}
}
