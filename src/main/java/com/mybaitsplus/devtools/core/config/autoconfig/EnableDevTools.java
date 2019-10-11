package com.mybaitsplus.devtools.core.config.autoconfig;

import com.mybaitsplus.devtools.core.config.EnableDevToolsAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自定义注解类，将某个包下的所有类自动加载到spring 容器中，不管有没有注解，并打印出
 * @since 2019/2/14 10:42
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableDevToolsAutoConfig.class)
public @interface EnableDevTools {
    /**
     * 传入包名
     */
    String[] packages() default "com.mybaitsplus.devtools";

}
