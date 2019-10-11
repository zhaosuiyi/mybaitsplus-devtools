package com.mybaitsplus.devtools.core.datasource.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author YI ZHAO
 * @Description 数据源切换的注解
 * @Date 2018-06-23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface TargetDataSource {
    String value();
}
