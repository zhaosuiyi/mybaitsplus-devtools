package com.mybaitsplus.devtools.core.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SMSUtil {
	
	private static final Logger log = LoggerFactory.getLogger(SMSUtil.class); 
	
	/**
	 * 接口地址，和上面的一样，都可以使用
	 */
	private static String url= PropertiesUtil2.getString("msm.url");
	/**
	 * 用户标识字符串，由接口方提供
	 */
	private static String secretKey= PropertiesUtil2.getString("msm.secretKey");
	/**
	 * //MD5加密时使用的字符串，由接口方提供
	 */
	private static String md5= PropertiesUtil2.getString("msm.md5");

	/**
	 * 发送短信接口（立刻发送）
	 * @param msg	发送信息内容
	 * @param phone	接受手机号
	 * @return
	 */
	public static boolean send(String msg,String phone){
		try {

			Map<String,String> param=new HashMap<String, String>();
			param.put("secret_key", secretKey);
			param.put("sms_type", "3");
			param.put("mobile",phone);
			param.put("msg",msg);

			if(StringUtils.isNotEmpty(msg)&& StringUtils.isNotEmpty(phone)){
				String signaTure= SignUtil.getSign(param, md5);
				param.put("signaTure", signaTure);
				String result = HttpUtil.sendPost(url, param);
				log.info(result);
				if(result.contains("SUCCESS")){
					return true;
				}
				return false;
			}else{
				log.warn("发送信息或手机号不能为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("访问短信程序发生异常:"+e.getMessage());
		}
		return false;
	}
}
