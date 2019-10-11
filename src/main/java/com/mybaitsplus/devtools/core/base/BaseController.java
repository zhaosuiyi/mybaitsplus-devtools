package com.mybaitsplus.devtools.core.base;

import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 控制器基类
 *
 */
public abstract class BaseController<T extends Serializable> extends AbstractController {
	@Autowired
	protected BaseService<? ,T> service;

	public Object query(ModelMap modelMap, Map<String, Object> params) {
		Page<?> list = service.query(params);
		return setSuccessModelMap(modelMap, list);
	}

	public Object queryList(ModelMap modelMap, Map<String, Object> params) {
		List<?> list = service.queryList(params);
		return setSuccessModelMap(modelMap, list);
	}

	public Object get(ModelMap modelMap, Long id) {
		T result = service.queryById(id);
		return setSuccessModelMap(modelMap, result);
	}

	public Object update(ModelMap modelMap, T param,Long id) {
		service.update(param,id);
		return setSuccessModelMap(modelMap);
	}

	public Object delete(ModelMap modelMap, Long id) {
		service.delete(id);
		return setSuccessModelMap(modelMap);
	}

}
