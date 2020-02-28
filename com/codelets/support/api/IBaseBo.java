package com.codelets.support.api;

import java.sql.Timestamp;

import lombok.Data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.format.annotation.DateTimeFormat;

import com.codelets.support.jackson.JsonTimeStampSerializer;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2018年12月27日 上午8:41:25
 * 
 * 实现功能：
 */
@Data
public abstract class IBaseBo {
	/** 创建时间 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = JsonTimeStampSerializer.class)
	private Timestamp createTime;
	/** 修改人 */
	private long modifyUser;
	/** 更新时间 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonSerialize(using = JsonTimeStampSerializer.class)
	private Timestamp updateTime;
	/** 版本信息 */
	private int version;
}
