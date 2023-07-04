package org.cdc.agentlib.transformer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * e-mail: 3154934427@qq.com
 * 过滤用
 *
 * @author cdc123
 * @classname FilteredClass
 * @date 2022/12/22 21:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FilteredClass {
    String value() default "";

    /**
     * head full end
     *
     */
    String mode() default "full";
}
