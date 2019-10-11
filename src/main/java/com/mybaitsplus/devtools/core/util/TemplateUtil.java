package com.mybaitsplus.devtools.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板类文件，用户定义模板信息
 * @author Administrator
 *
 */
public class TemplateUtil {

    /**
     * 扩展类，重新定义模板信息 增加用于处理数据类型的参数
     * @param templateId
     * @return
     */
    public static List<Map<String,Object>> getTemplates(String templateId){
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        Map<String,Object> map = new HashMap<String,Object>();
        if (templateId == "1") {
            map.put("path", "D:\\jy1.xls");
            map.put("col", 127);
            map.put("row", 4);
        }
        return listMap;
    }

    /**
     * 模板类型,用于定义模板数据  可改成从配置文件读取信息
     * @param templateId
     * @return
     */
    public static Map<String, Object> getTemplate(String templateId) {
        Map<String, Object> map = new HashMap<String, Object>();

        //定义一个编号为“1”的模板文件
        if (templateId == "1") {
            map.put("path", "D:\\jy1.xls");
            map.put("col", 127);
            map.put("row", 4);
        }

        if (templateId == "2") {
            map.put("path", "D:\\moban.xls");
            map.put("col", 6);
            map.put("row", 1);
        }
        return map;
    }
}

