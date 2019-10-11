package com.mybaitsplus.devtools.core.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class HttpUtil {
	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class); 

	public static final String httpClientPost(String url) {
		String result = "";
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(url);
		try {
			client.executeMethod(getMethod);
			result = getMethod.getResponseBodyAsString();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			getMethod.releaseConnection();
		}
		return result;
	}

	public static final String httpClientPost(String url, ArrayList<NameValuePair> list) {
		String result = "";
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		try {
			NameValuePair[] params = new NameValuePair[list.size()];
			for (int i = 0; i < list.size(); i++) {
				params[i] = list.get(i);
			}
			postMethod.addParameters(params);
			client.executeMethod(postMethod);
			result = postMethod.getResponseBodyAsString();
		} catch (Exception e) {
			log.error("", e);
		} finally {
			postMethod.releaseConnection();
		}
		return result;
	}
	/**
     * 执行一个带参数的HTTP POST请求，返回请求响应的JSON字符串
     *
     * @param url 请求的URL地址
     * @param map 请求的map参数
     * @return 返回请求响应的JSON字符串
     */
	public static final String sendPost(String url, Map<String, String> parameters) throws Exception{
		String result = "";
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		try {
			
			if(!CollectionUtils.isEmpty(parameters)){
				for (Map.Entry<String, String> entry : parameters.entrySet()) { 
					if(log.isDebugEnabled()){
						log.debug("Post parameter [{} = {}].", new Object[]{entry.getKey(), entry.getValue()});
					}
					nvps.add(new NameValuePair(entry.getKey(),entry.getValue()));
				}
			}else{
				if(log.isDebugEnabled()){
					log.debug("Post parameter is empty.");
				}
			}
			
	        NameValuePair[] params = nvps.toArray(new NameValuePair[nvps.size()]);
	        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			postMethod.addParameters(params);
			client.executeMethod(postMethod);
			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
                return StreamUtils.copyToString(postMethod.getResponseBodyAsStream(), Charset.forName("utf-8"));
            }
		} catch (Exception e) {
			log.error("", e);
		} finally {
			postMethod.releaseConnection();
		}
		return result;
    }
	
}
