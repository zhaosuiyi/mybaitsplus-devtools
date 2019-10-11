package com.mybaitsplus.devtools.core.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Parsing The Configuration File
 * @author YI ZHAO
 */
public final class PropertiesUtil extends PropertyPlaceholderConfigurer {
    private static final byte[] KEY = {9, -1, 0, 5, 39, 8, 6, 19};
    private static Map<String, String> ctxPropertiesMap;
    private List<String> decryptProperties;

    static {
        try {
            String  resourceName="application.properties";
            Properties properties = PropertiesLoaderUtils.loadAllProperties(resourceName);
            processProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadProperties(Properties props) throws IOException {
        super.loadProperties(props);
        ctxPropertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            if (decryptProperties != null && decryptProperties.contains(keyStr)) {
                value = SecurityUtil.decryptDes(value, KEY);
                props.setProperty(keyStr, value);
            }
            ctxPropertiesMap.put(keyStr, value);
        }
    }
    
    public static void processProperties(Properties props) throws IOException {
    	   ctxPropertiesMap = new HashMap<String, String>();
           for (Object key : props.keySet()) {
               String keyStr = key.toString();
               String value = props.getProperty(keyStr);
               ctxPropertiesMap.put(keyStr, value);
           }
    }
    
    /**
     * spring-boot 通过 监听加载ApplicationListener
     */
    public static void loadAllProperties(String propertyFileName){
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties(propertyFileName);
            processProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String fileName,String key) {
        String value = null;
        try {
            value = PropertiesLoaderUtils.loadAllProperties(fileName).getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static Map<String, String> getAllProperty() { 
    	return ctxPropertiesMap; 
	}

    /**
     * @param decryptPropertiesMap the decryptPropertiesMap to set
     */
    public void setDecryptProperties(List<String> decryptProperties) {
        this.decryptProperties = decryptProperties;
    }

    /**
     * Get a value based on key , if key does not exist , null is returned
     * 
     * @param key
     * @return
     */
    public static String getString(String key) {
        try {
            return ctxPropertiesMap.get(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }
    
    public static String getString(String key, String defaultValue) {
        String value = ctxPropertiesMap.get(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 根据key获取值
     * 
     * @param key
     * @return
     */
    public static int getInt(String key) {
        return Integer.parseInt(ctxPropertiesMap.get(key));
    }
    
    public static double getDouble(String key) {
        return Double.parseDouble(ctxPropertiesMap.get(key));
    }

    /**
     * 根据key获取值
     * 
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getInt(String key, int defaultValue) {
        String value = ctxPropertiesMap.get(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    /**
     * 根据key获取值
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = ctxPropertiesMap.get(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return new Boolean(value);
    }

    public double getDouble(String key, double defaultValue) {
    	 String value = ctxPropertiesMap.get(key);
         if (StringUtils.isBlank(value)) {
             return defaultValue;
         }
         return new Double(value);
    }
    
    public static void main(String[] args) {
        String encrypt = SecurityUtil.encryptDes("buzhidao", KEY);
        System.out.println(encrypt);
        System.out.println(SecurityUtil.decryptDes(encrypt, KEY));
    }
}
