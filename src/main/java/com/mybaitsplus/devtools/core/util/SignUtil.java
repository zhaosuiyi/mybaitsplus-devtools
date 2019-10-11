package com.mybaitsplus.devtools.core.util;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SignUtil {	
	/**
	 * 得到签名串
	 * @param params(map类型)
	 */
	@SuppressWarnings("unchecked")
	public static <T> String getSign(Map<String, String> params,String secret_value){
		String signaTure = null;		
		try {			 
			 deleteMapNull(params);			 
			 if(params != null && params.size() > 0){
				 params =mapCompare(params);
			 }		 
			 signaTure = md5Sign(params.toString() + "&key=" + secret_value);
		} catch (Exception e) {
			System.out.println("签名串（signaTure）转换错误！");
		}
		return signaTure;
	}
	
	/**
	 * 去除map中空值字段
	 * @param map
	 */
	public static <T> void deleteMapNull(Map<T, String> map){
		if(map != null && map.size() > 0){
			map.remove("class");
			Iterator<Entry<T, String>> iterator = map.entrySet().iterator();		
			while(iterator.hasNext()){
				Entry<T, String> entry = iterator.next();			
				if(null == entry.getValue() || "".equals(entry.getValue())){
					iterator.remove();
				}
			}
		}
	}
	
	/**
	 * @Method描述: Map排序	 
	 * @param map Map
	 * @return String
	 */
	@SuppressWarnings({"rawtypes", "serial", "unchecked"})
	public static Map mapCompare(Map map) {
		Map treeMap = new TreeMap() {
			public String toString() {
				Iterator iterator = this.entrySet().iterator();
				StringBuffer sb = new StringBuffer();
				while (iterator.hasNext()) {
					Entry entry = (Entry) iterator.next();
					String key = entry.getKey().toString();
					Object value = entry.getValue();
					sb.append(key + '=' + value + '&');
				}
				return sb.substring(0, sb.length() - 1).toString();
			}
		};
		treeMap.putAll(map);
		return treeMap;
	}
	
	/**
	 * @Method描述: MD5签名算法
	 * @param input 签名数据
	 * @return String
	 * @throws Exception
	 */
	public static String md5Sign(String input) throws Exception {
		String result = "";
		MessageDigest md5 = MessageDigest.getInstance("md5");
		byte[] temp = md5.digest(input.getBytes("utf8"));
		for (byte b : temp) {
			result += Integer.toHexString((0x000000ff & b) | 0xffffff00).substring(6);
		}
		return result;
	}
}
