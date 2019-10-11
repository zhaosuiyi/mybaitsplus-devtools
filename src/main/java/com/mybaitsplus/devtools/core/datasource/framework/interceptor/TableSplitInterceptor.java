package com.mybaitsplus.devtools.core.datasource.framework.interceptor;

import com.baomidou.mybatisplus.toolkit.PluginUtils;
import com.mybaitsplus.devtools.core.datasource.framework.annotation.TableSplitRule;
import com.mybaitsplus.devtools.core.datasource.framework.strategy.Strategy;
import com.mybaitsplus.devtools.core.datasource.framework.strategy.StrategyManager;
import com.mybaitsplus.devtools.core.util.BeanFactoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

/**
 */
@Slf4j(topic="策略分表拦截器【TableSplitInterceptor】")
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class,Integer.class }) })
public class TableSplitInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        Object parameterObject=metaObject.getValue("delegate.boundSql.parameterObject");
        doSplitTable(metaObject,parameterObject);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object arg0) {
        if (arg0 instanceof StatementHandler) {
            return Plugin.wrap(arg0, this);
        } else {
            return arg0;
        }
    }

    @Override
    public void setProperties(Properties arg0) {
    }

    private void doSplitTable(MetaObject metaObject,Object param) throws ClassNotFoundException{
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        String originalSql = boundSql.getSql();
        if (originalSql != null && !originalSql.equals("")) {
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
            String id = mappedStatement.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            Class<?> classObj = Class.forName(className);
            TableSplitRule rule = classObj.getAnnotation(TableSplitRule.class);
            if(rule==null||!rule.interFale()) {
                return ;
            }
            String convertedSql= null;
            Strategy strategy = null;

            if(rule.strategy()!=null&&!rule.strategy().isEmpty()) {
                StrategyManager strategyManager=(StrategyManager)BeanFactoryUtil.getBean("strategyManager");
                strategy = strategyManager.getStrategy(rule.strategy());
            }
            if(!rule.paramName().isEmpty()&&!rule.tableName().isEmpty()) {
                String newTableName = strategy.returnTableName(rule.tableName(), rule.paramName(),param);
                try {
                    convertedSql = originalSql.replaceAll(rule.tableName(),newTableName );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            metaObject.setValue("delegate.boundSql.sql",convertedSql);
        }
    }

    public String getParamValue(Object obj,String paramName) {
        if(obj instanceof Map) {
            return (String) ((Map) obj).get(paramName);
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field field : fields) {
            field.setAccessible(true);
            if(field.getName().equalsIgnoreCase(paramName)) {
                try {
                    return (String) field.get(obj);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

}