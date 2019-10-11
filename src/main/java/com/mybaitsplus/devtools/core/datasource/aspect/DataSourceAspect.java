package com.mybaitsplus.devtools.core.datasource.aspect;

import com.mybaitsplus.devtools.core.datasource.annotations.TargetDataSource;
import com.mybaitsplus.devtools.core.datasource.database.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

@Slf4j
@Aspect
// 保证该AOP在@Transactional之前执行
@Order(-100)
@Configuration
public class DataSourceAspect {

    protected static final ThreadLocal<String> preDatasourceHolder = new ThreadLocal<>();

    @Pointcut("@within(com.mybaitsplus.devtools.core.datasource.annotations.TargetDataSource) || @annotation(com.mybaitsplus.devtools.core.datasource.annotations.TargetDataSource)")
    public void doPointCut() { }

    @Before("doPointCut()")
    public void changeDataSourceBeforeMethodExecution(JoinPoint jp) {
        String key = determineDatasource1(jp);
        if (key == null) {
            DataSourceContextHolder.setDataSourceType(null);
            return;
        }
        preDatasourceHolder.set(DataSourceContextHolder.getDataSourceType());
        DataSourceContextHolder.setDataSourceType(key);
    }

    @After("doPointCut()")
    public void restoreDataSourceAfterMethodExecution() {
        DataSourceContextHolder.setDataSourceType(preDatasourceHolder.get());
        preDatasourceHolder.remove();
    }

    /**
     *
     * @param jp
     * @return
     */
    public String determineDatasource(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        Class targetClass = jp.getSignature().getDeclaringType();
        String dataSourceForTargetClass = resolveDataSourceFromClass(targetClass);
        String dataSourceForTargetMethod = resolveDataSourceFromMethod(targetClass, methodName);
        String resultDS = determinateDataSource(dataSourceForTargetClass, dataSourceForTargetMethod);
        return resultDS;
    }

    /**
     *
     * @param targetClass
     * @param methodName
     * @return
     */
    private String resolveDataSourceFromMethod(Class targetClass, String methodName) {
        Method m = findUniqueMethod(targetClass, methodName);
        if (m != null) {
            TargetDataSource choDs = m.getAnnotation(TargetDataSource.class);
            return resolveDataSourceName(choDs);
        }
        return null;
    }

    /**
     *
     * @param classDS
     * @param methodDS
     * @return
     */
    private String determinateDataSource(String classDS, String methodDS) {
        return methodDS == null ? classDS : methodDS;
    }

    /**
     *
     * @param targetClass
     * @return
     */
    private String resolveDataSourceFromClass(Class targetClass) {
        TargetDataSource classAnnotation = (TargetDataSource) targetClass.getAnnotation(TargetDataSource.class);
        return null != classAnnotation ? resolveDataSourceName(classAnnotation) : null;
    }

    /**
     *
     * @param ds
     * @return
     */
    private String resolveDataSourceName(TargetDataSource ds) {
        return ds == null ? null : ds.value();
    }

    /**
     *
     * @param clazz
     * @param name
     * @return
     */
    private static Method findUniqueMethod(Class<?> clazz, String name) {
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (Method method : methods) {
                if (name.equals(method.getName())) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public String determineDatasource1(JoinPoint jp) {
        String resultDScls=null,resultDStar=null;
        Class<?> target = jp.getTarget().getClass();
        MethodSignature signature = (MethodSignature) jp.getSignature();
        // 默认使用目标类型的注解，如果没有则使用其实现接口的注解类
        for (Class<?> cls : target.getInterfaces()) {
            resultDScls=resetDataSource(cls, signature.getMethod());
        }
        resultDStar=resetDataSource(target, signature.getMethod());
        return resultDStar!=null?resultDStar:resultDScls;
    }

    private String resetDataSource(Class<?> cls, Method method) {
        String resultDS=null;
        try {
            Class<?>[] types = method.getParameterTypes();
            // 默认使用类注解
            if (cls.isAnnotationPresent(TargetDataSource.class)) {
                TargetDataSource source = cls.getAnnotation(TargetDataSource.class);
                resultDS= source.value();
            }
            // 方法注解可以覆盖类注解
            Method m = cls.getMethod(method.getName(), types);
            if (m != null && m.isAnnotationPresent(TargetDataSource.class)) {
                TargetDataSource source = m.getAnnotation(TargetDataSource.class);
                resultDS= source.value();
            }
        } catch (Exception e) {
            log.warn(cls + ":{}" , e.getMessage());
        }
        return resultDS;
    }
}
