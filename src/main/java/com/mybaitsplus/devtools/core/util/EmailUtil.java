package com.mybaitsplus.devtools.core.util;

import com.mybaitsplus.devtools.core.support.email.Email;
import com.mybaitsplus.devtools.core.support.email.EmailSender;

import java.io.InputStream;
import java.util.Map;

/**
 * 发送邮件辅助类
 */
public final class EmailUtil {

	private EmailUtil() {
	}

	/**
	 * 发送邮件
	 */
	public static final boolean sendEmail(Email email) {
		// 初始化邮件引擎
		EmailSender sender = new EmailSender(email.getHost(),email.getPort());
		sender.setNamePass(email.getName(), email.getPassword(), email.getKey());
		if (sender.setFrom(email.getFrom()) == false)
			return false;
		if (sender.setTo(email.getSendTo()) == false)
			return false;
		if (email.getCopyTo() != null && sender.setCopyTo(email.getCopyTo()) == false)
			return false;
		if (sender.setSubject(email.getTopic()) == false)
			return false;
		if (sender.setBody(email.getBody()) == false)
			return false;
		if (email.getFileAffix() != null) {
			for (int i = 0; i < email.getFileAffix().length; i++) {
				if (sender.addFileAffix(email.getFileAffix()[i]) == false)
					return false;
			}
		}
		if (email.getStreamAffix()!=null){
            for(Map.Entry<String, InputStream> entry : email.getStreamAffix().entrySet()){
                if(sender.addStreamAffix(entry.getValue(), entry.getKey())==false)
                    return false;
            }
        }
		// 发送
		return sender.sendout();
	}
	
	/**
	 * @param sendTo 接收人
	 * @param topic 主题
	 * @param body 内容
	 */
	public static final boolean sendEmail(String sendTo, String topic, String body) {
		return sendEmail(null, sendTo, null, topic, body, null);
	}

	/**
	 * @param sendTo 接收人
	 * @param topic 主题
	 * @param body 内容
	 * @param fileAffix 附件
	 */
	public static final boolean sendEmail(String sendTo, String topic, String body, String[] fileAffix) {
		return sendEmail(sendTo, null, topic, body, fileAffix);
	}

	/**
	 * @param sendTo 接收人
	 * @param copyTo 抄送人
	 * @param topic 主题
	 * @param body 内容
	 */
	public static final boolean sendEmail(String sendTo, String copyTo, String topic, String body) {
		return sendEmail(null, sendTo, copyTo, topic, body, null);
	}

	/**
	 * @param sendTo 接收人
	 * @param copyTo 抄送人
	 * @param topic 主题
	 * @param body 内容
	 * @param fileAffix 附件
	 */
	public static final boolean sendEmail(String sendTo, String copyTo, String topic, String body, String[] fileAffix) {
		return sendEmail(null, sendTo, copyTo, topic, body, fileAffix);
	}

	/**
	 * @param from 发送人
	 * @param sendTo 接收人
	 * @param copyTo 抄送人
	 * @param topic 主题
	 * @param body 内容
	 */
	public static final boolean sendEmail(String from, String sendTo, String copyTo, String topic, String body) {
		return sendEmail(from, sendTo, copyTo, topic, body, null);
	}

	/**
	 * @param from 发送人
	 * @param sendTo 接收人
	 * @param copyTo 抄送人
	 * @param topic 主题
	 * @param body 内容
	 * @param fileAffix 附件
	 */
	public static final boolean sendEmail(String from, String sendTo, String copyTo, String topic, String body, String[] fileAffix) {
		return sendEmail(from, null, null, null, sendTo, copyTo, topic, body, fileAffix);
	}

	/**
	 * @param from 发送人
	 * @param name 登录名
	 * @param password 登录密码
	 * @param sendTo 接收人
	 * @param copyTo 抄送人
	 * @param topic 主题
	 * @param body 内容
	 */
	public static final boolean sendEmail(String from, String name, String password, String key, String sendTo, String copyTo, String topic,
			String body) {
		return sendEmail(null, from, name, password, key, sendTo, copyTo, topic, body, null);
	}

	/**
	 * @param from 发送人
	 * @param name 登录名
	 * @param password 登录密码
	 * @param sendTo 接收人
	 * @param copyTo 抄送人
	 * @param topic 主题
	 * @param body 内容
	 * @param fileAffix 附件
	 */
	public static final boolean sendEmail(String from, String name, String password, String key, String sendTo, String copyTo, String topic,
			String body, String[] fileAffix) {
		return sendEmail(null, from, name, password, key, sendTo, copyTo, topic, body, fileAffix);
	}

	/**
	 * @param host 服务器地址
	 * @param from 发送人
	 * @param name 登录名
	 * @param password 登录密码
	 * @param sendTo 接收人
	 * @param copyTo 抄送人
	 * @param topic 主题
	 * @param body 内容
	 */
	public static final boolean sendEmail(String host, String from, String name, String password, String key, String sendTo, String copyTo,
			String topic, String body) {
		return sendEmail(host, from, name, password, key, sendTo, copyTo, topic, body, null);
	}
	
	/**
	 * 发送邮件
	 */
	public static final boolean sendEmail(String host, String from, String name, String password, String key, String sendTo, String copyTo,
			String topic, String body, String[] fileAffix) {
		Email email=new Email(host, from, name, password, key, sendTo, copyTo, topic, body, fileAffix);
		return sendEmail(email);
	}

    /**
     *流附件
     */
    public static final boolean sendEmail2(String sendTo, String topic, String body, String affixName,InputStream inputStream) {
        return sendEmail2(sendTo, null, topic, body, affixName,inputStream);
    }
    /**
     *流附件
     */
    public static final boolean sendEmail2(String sendTo, String copyTo, String topic, String body, String affixName,InputStream inputStream) {
        Map<String,InputStream> streamAffix=InstanceUtil.newHashMap();
        streamAffix.put(affixName, inputStream);
        return sendEmail2(sendTo, copyTo, topic, body, streamAffix);
    }
    /**
     *流附件
     */
    public static final boolean sendEmail2(String sendTo, String copyTo, String topic, String body, Map<String,InputStream> streamAffix) {
        Email email=new Email((String)null, (String)null, (String)null, (String)null, (String)null, sendTo, copyTo, topic, body, null);
        if(streamAffix!=null){
            email.setStreamAffix(streamAffix);
        }
        return sendEmail(email);
    }


}
