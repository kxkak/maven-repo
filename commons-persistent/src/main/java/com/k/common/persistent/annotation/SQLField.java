package com.k.common.persistent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface SQLField {
    /**
     * 别名映射，类似MyBatis的@Result
     */
    String value() default "";

    /**
     * 是否忽略该字段
     */
    boolean ignored() default false;
}