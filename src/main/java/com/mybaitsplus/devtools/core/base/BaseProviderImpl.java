package com.mybaitsplus.devtools.core.base;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.mybaitsplus.devtools.core.Constants;
import com.mybaitsplus.devtools.core.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseProviderImpl implements ApplicationContextAware, BaseProvider {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Parameter execute(Parameter parameter) {
        String no = parameter.getNo();
        log.info("{} request：{}", no, JSON.toJSONString(parameter));
        Object service = applicationContext.getBean(parameter.getService());
        try {
            Long id = parameter.getId();
            BaseModel model = parameter.getModel();
            List<?> list = parameter.getList();
            Map<?, ?> map = parameter.getMap();
            Object result = null;
            MethodAccess methodAccess = MethodAccess.get(service.getClass());
            if (id != null) {
                result = methodAccess.invoke(service, parameter.getMethod(), parameter.getId());
            } else if (model != null) {
                result = methodAccess.invoke(service, parameter.getMethod(), parameter.getModel());
            } else if (list != null) {
                result = methodAccess.invoke(service, parameter.getMethod(), parameter.getList());
            } else if (map != null) {
                result = methodAccess.invoke(service, parameter.getMethod(), parameter.getMap());
            } else {
                result = methodAccess.invoke(service, parameter.getMethod());
            }
            if (result != null) {
                Parameter response = new Parameter(result);
                log.info("{} response：{}", no, JSON.toJSONString(response));
                return response;
            }
            log.info("{} response empty.", no);
            return null;
        } catch (Exception e) {
            String msg = ExceptionUtil.getStackTraceAsString(e);
            log.error(no + " " + Constants.Exception_Head + msg, e);
            throw e;
        }
    }
}
