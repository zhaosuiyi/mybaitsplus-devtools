package com.mybaitsplus.devtools.core.datasource.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分表规则
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TableSplitRule {
    /**
     * 表名
     */
    public String tableName();

    /**
     * 暂时只支持单参数
     */
    public String paramName();

    /**
     * 分表策略
     */
    public String strategy();

    boolean interFale() default true;
}