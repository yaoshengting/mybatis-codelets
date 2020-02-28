/**
 * 
 */
package com.codelets.support.annnotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 作者： yaoshengting
 *
 * 创建时间：2019年10月25日 下午1:11:00
 * 
 * 实现功能：在进行Entry属性转换成数据库表列的时候忽略该注解标记的属性的转换
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transient {
	//
}
