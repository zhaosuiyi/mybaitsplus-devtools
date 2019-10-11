package com.mybaitsplus.devtools.core.util;

import org.springframework.util.ClassUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

public class BeanHelper {
    /**
     * 判断是否是简单值类型.包括：基础数据类型、CharSequence、Number、Date、URL、URI、Locale、Class;
     *
     * @param clazz
     * @return
     */
    public static boolean isSimpleValueType(Class<?> clazz) {
        return (ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || URI.class == clazz
                || URL.class == clazz || Locale.class == clazz || Class.class == clazz);
    }
    /**
     * 深度复制
     * @param t
     */
    @SuppressWarnings("unchecked")
   	public static <T> T deepCopy(T t) throws Exception{
   	        ByteArrayOutputStream boStream=new ByteArrayOutputStream();
   	        ObjectOutputStream oos= new ObjectOutputStream(boStream);
   	        oos.writeObject(t);
   	        ByteArrayInputStream bis=new ByteArrayInputStream(boStream.toByteArray());
   	        ObjectInputStream ois=new ObjectInputStream(bis);
   	        return (T) ois.readObject();
    }
}
