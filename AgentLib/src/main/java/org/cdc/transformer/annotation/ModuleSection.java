package org.cdc.transformer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * e-mail: 3154934427@qq.com
 * 模块下的子模块
 *
 * @author cdc123
 * @classname ModuleSection
 * @date 2023/6/19 12:55
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleSection {
    String value();
}
