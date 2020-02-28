package com.codelets.support.annnotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.codelets.support.enumtype.DaoTypeEnum;

/**
 * 副本dao操作器<br/>
 * 如在GenericHisDaoImpl类中有@DaoType(type = HISTORY)<br/>
 * 这样该Dao就是处理历史表的相关操作
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DaoType {
	/**
	 * 指定dao的类型
	 * 
	 * @return dao类型
	 */
	DaoTypeEnum type();
}
