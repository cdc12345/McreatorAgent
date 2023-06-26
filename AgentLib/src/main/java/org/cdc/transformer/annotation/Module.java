package org.cdc.transformer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * e-mail: 3154934427@qq.com
 * 模块注解
 *
 * @author cdc123
 * @classname Module
 * @date 2023/6/19 12:53
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {
    String value() default "";
}
