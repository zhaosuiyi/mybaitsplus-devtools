package com.mybaitsplus.devtools.core.datasource.framework.strategy.impl;

import com.mybaitsplus.devtools.core.datasource.framework.strategy.Strategy;
import com.mybaitsplus.devtools.core.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

/**
 * @description：按月分表策略
 * @date ：2019/6/13 10:29
 */
@Slf4j
public class YYYYMMStrategy implements Strategy {

    @Override
    public String returnTableName(String tableName, String param,Object val) {
        try {
            String paramValue=getParamValue(val,param);
            if(paramValue!=null){
                Date date = DateUtil.getDateByStr(paramValue);
                if(date==null){
                    return tableName+"_"+paramValue;
                }
                return tableName+"_"+ DateUtil.getDateYearMonthStr(date);
            }
            return tableName;
        } catch (Exception e) {
            e.printStackTrace();
            return tableName;
        }
    }

    public String getParamValue(Object val,String paramName) {
        if(val==null){ return null ;}
        if (val.toString().contains(paramName)) {
            if(val instanceof Map) {
                for (Map.Entry<String, Object> e : ((Map<String, Object>) val).entrySet()) {
                    if (e.getValue() instanceof Map) {
                        for (Map.Entry<String, Object> e1 : ((Map<String, Object>) e.getValue()).entrySet()) {
                            if (e1.getValue() instanceof String && e1.getKey().contains(paramName)) {
                                return (String) e1.getValue();
                            }
                            if (e1.getValue() instanceof Date && e1.getKey().contains(paramName)) {
                                return DateUtil.getDateYearMonthStr((Date) e1.getValue());
                            }
                        }
                    }
                    if (e.getValue() instanceof String && e.getKey().contains(paramName)) {
                        return (String) e.getValue();
                    }
                    if (e.getValue() instanceof Date && e.getKey().contains(paramName)) {
                        return DateUtil.getDateYearMonthStr((Date) e.getValue());
                    }
                }
            }
        }

        Field[] fields = val.getClass().getDeclaredFields();
        for(Field field : fields) {
            field.setAccessible(true);
            if(field.getName().equalsIgnoreCase(paramName)) {
                try {
                    return (String) field.get(val);
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
