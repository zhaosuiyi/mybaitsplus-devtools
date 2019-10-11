package com.mybaitsplus.devtools.core.support.cache.key;


import com.alibaba.fastjson.JSONObject;
import com.mybaitsplus.devtools.core.util.BeanHelper;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

public class SimpleKeyGenerator implements KeyGenerator {
	
	private final static int NO_PARAM_KEY = 0;
	
	@Override
	public Object generate(Object target, Method method, Object... params) {
		return generateKey(method,params);
	}

	/**
	 * Generate a key based on the specified parameters.
	 */
	public static Object generateKey(Method method,Object... params) {
		char sp = ':';
        StringBuilder strBuilder = new StringBuilder(30);
        // 方法名
        strBuilder.append(method.getName());
        strBuilder.append(sp);
        if (params.length > 0) {
            // 参数值
            for (Object object : params) {
                if (BeanHelper.isSimpleValueType(object.getClass())) {
                    strBuilder.append(object);
                } else {
                    strBuilder.append(JSONObject.toJSON(object).hashCode());
                }
            }
        } else {
            strBuilder.append(NO_PARAM_KEY);
        }
        return strBuilder.toString();
	}


}
