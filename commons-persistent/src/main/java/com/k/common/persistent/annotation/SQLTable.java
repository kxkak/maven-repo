package com.k.common.persistent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SQLTable {
    /**
     * 表名别名，一般多表查询时用到
     */
    String alias();
    /**
     * 忽略的Java bean属性名称（大小写不敏感）
     */
    String[] ignoredFields() default {};
}
