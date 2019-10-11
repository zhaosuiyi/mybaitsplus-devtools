package com.mybaitsplus.devtools.core.datasource.database;

/**
 * 数据库上下文切换
 * Created by yu on 2017/3/8.
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /**
     * 设置当前数据库。
     *
     * @param dbType
     */
    public static void setDataSourceType(String dbType) {
        contextHolder.set(dbType);
    }

    /**
     * 取得当前数据源。
     *
     * @return
     */
    public static String getDataSourceType() {
        return contextHolder.get();
    }

    /**
     * 清除上下文数据
     */
    public static void clearDataSourceType() {
        contextHolder.remove();
    }

}
