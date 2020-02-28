package com.codelets.dao.annote;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年11月15日 上午11:28:15
 * 
 * 实现功能：数据库主键策略<br/>
 * true表示使用数据库自增主键<br/>
 * false表示使用自己生成的主键<br/>
 * 如果没有加该注解，表示对应的表没有主键<br/>
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GenerateId {
	/**
	 * @return 是否主键自增
	 */
	boolean value() default true;
}
