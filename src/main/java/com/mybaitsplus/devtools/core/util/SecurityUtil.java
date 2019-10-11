package com.mybaitsplus.devtools.core.util;

import com.mybaitsplus.devtools.core.support.security.BASE64Encoder;
import com.mybaitsplus.devtools.core.support.security.coder.*;

import java.util.Map;


/**
 * 数据加密辅助类(默认编码UTF-8)
 * 
 */
public final class SecurityUtil {
	private SecurityUtil() {
	}

	/**
	 * 默认算法密钥
	 */
	private static final byte[] ENCRYPT_KEY = { -81, 0, 105, 7, -32, 26, -49, 88 };

	public static final String CHARSET = "UTF-8";

	/**
	 * BASE64解码
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static final byte[] decryptBASE64(String key) {
		try {
			return new BASE64Encoder().decode(key);
		} catch (Exception e) {
			throw new RuntimeException("解密错误，错误信息：", e);
		}
	}

	/**
	 * BASE64编码
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static final String encryptBASE64(byte[] key) {
		try {
			return new BASE64Encoder().encode(key);
		} catch (Exception e) {
			throw new RuntimeException("加密错误，错误信息：", e);
		}
	}

	/**
	 * 数据解密，算法（DES）
	 * 
	 * @param cryptData
	 *            加密数据
	 * @return 解密后的数据
	 */
	public static final String decryptDes(String cryptData) {
		return decryptDes(cryptData, ENCRYPT_KEY);
	}

	/**
	 * 数据加密，算法（DES）
	 * 
	 * @param data
	 *            要进行加密的数据
	 * @return 加密后的数据
	 */
	public static final String encryptDes(String data) {
		return encryptDes(data, ENCRYPT_KEY);
	}

	/**
	 * 基于MD5算法的单向加密
	 * 
	 * @param strSrc
	 *            明文
	 * @return 返回密文
	 */
	public static final String encryptMd5(String strSrc) {
		String outString = null;
		try {
			outString = encryptBASE64(MDCoder.encodeMD5(strSrc.getBytes(CHARSET)));
		} catch (Exception e) {
			throw new RuntimeException("加密错误，错误信息：", e);
		}
		return outString;
	}

	/**
	 * SHA加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static final String encryptSHA(String data) {
		try {
			return encryptBASE64(SHACoder.encodeSHA256(data.getBytes(CHARSET)));
		} catch (Exception e) {
			throw new RuntimeException("加密错误，错误信息：", e);
		}
	}

	/**
	 * HMAC加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static final String encryptHMAC(String data) {
		return encryptHMAC(data, ENCRYPT_KEY);
	}

	/**
	 * 数据解密，算法（DES）
	 * 
	 * @param cryptData
	 *            加密数据
	 * @return 解密后的数据
	 */
	public static final String decryptDes(String cryptData, byte[] key) {
		String decryptedData = null;
		try {
			// 把字符串解码为字节数组，并解密
			decryptedData = new String(DESCoder.decrypt(decryptBASE64(cryptData), key));
		} catch (Exception e) {
			throw new RuntimeException("解密错误，错误信息：", e);
		}
		return decryptedData;
	}

	/**
	 * 数据加密，算法（DES）
	 * 
	 * @param data
	 *            要进行加密的数据
	 * @return 加密后的数据
	 */
	public static final String encryptDes(String data, byte[] key) {
		String encryptedData = null;
		try {
			// 加密，并把字节数组编码成字符串
			encryptedData = encryptBASE64(DESCoder.encrypt(data.getBytes(), key));
		} catch (Exception e) {
			throw new RuntimeException("加密错误，错误信息：", e);
		}
		return encryptedData;
	}

	/**
	 * HMAC加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static final String encryptHMAC(String data, byte[] key) {
		try {
			return encryptBASE64(HmacCoder.encodeHmacSHA512(data.getBytes(CHARSET), key));
		} catch (Exception e) {
			throw new RuntimeException("加密错误，错误信息：", e);
		}
	}

	/**
	 * RSA签名
	 * 
	 * @param data
	 *            原数据
	 * @return
	 */
	public static final String signRSA(String data, String privateKey) {
		try {
			return encryptBASE64(RSACoder.sign(data.getBytes(CHARSET), decryptBASE64(privateKey)));
		} catch (Exception e) {
			throw new RuntimeException("签名错误，错误信息：", e);
		}
	}

	/**
	 * RSA验签
	 * 
	 * @param data
	 *            原数据
	 * @return
	 */
	public static final boolean verifyRSA(String data, String publicKey, String sign) {
		try {
			return RSACoder.verify(data.getBytes(CHARSET), decryptBASE64(publicKey), decryptBASE64(sign));
		} catch (Exception e) {
			throw new RuntimeException("验签错误，错误信息：", e);
		}
	}

	/**
	 * 数据加密，算法（RSA）
	 * 
	 * @param data
	 *            数据
	 * @return 加密后的数据
	 */
	public static final String encryptRSAPrivate(String data, String privateKey) {
		try {
			return encryptBASE64(RSACoder.encryptByPrivateKey(data.getBytes(CHARSET), decryptBASE64(privateKey)));
		} catch (Exception e) {
			throw new RuntimeException("解密错误，错误信息：", e);
		}
	}
	
	/**
	 * 数据加密，算法（RSA）
	 * 
	 * @param data
	 *            数据
	 * @return 加密后的数据
	 */
	public static final String encryptRSAPublic(String data, String publicKey) {
		try {
			return encryptBASE64(RSACoder.encryptByPublicKey(data.getBytes(CHARSET), decryptBASE64(publicKey)));
		} catch (Exception e) {
			throw new RuntimeException("解密错误，错误信息：", e);
		}
	}

	/**
	 * 数据解密，算法（RSA）
	 * 
	 * @param cryptData
	 *            加密数据
	 * @return 解密后的数据
	 */
	public static final String decryptRSAPrivate(String cryptData, String privateKey) {
		try {
			// 把字符串解码为字节数组，并解密
			return new String(RSACoder.decryptByPrivateKey(decryptBASE64(cryptData), decryptBASE64(privateKey)));
		} catch (Exception e) {
			throw new RuntimeException("解密错误，错误信息：", e);
		}
	}
	
	/**
	 * 数据解密，算法（RSA）
	 * 
	 * @param cryptData
	 *            加密数据
	 * @return 解密后的数据
	 */
	public static final String decryptRSAPublic(String cryptData, String publicKey) {
		try {
			// 把字符串解码为字节数组，并解密
			return new String(RSACoder.decryptByPublicKey(decryptBASE64(cryptData), decryptBASE64(publicKey)));
		} catch (Exception e) {
			throw new RuntimeException("解密错误，错误信息：", e);
		}
	}

	public static String encryptPassword(String password) {
		return encryptMd5(SecurityUtil.encryptSHA(password));
	}

	public static void main(String[] args) throws Exception {
		/*byte[] key = {9, -1, 0, 5, 39, 8, 6, 19};
		System.out.println("图片db:"+encryptDes("testdbbiz`!@", key));
		System.out.println("fdfs:"+encryptDes("FastDFS1234567890", key));
		System.out.println("email:"+encryptDes("sxcw@51Shaoxi", key));
		System.out.println("redis:"+encryptDes("g1Q*Y%d32Uv27x^R", key));*/
		/*System.out.println(decryptDes("INzvw/3Qc4q="));
		System.out.println(encryptMd5("SHJR"));
		System.out.println(encryptSHA("1"));
		Map<String, Object> key = RSACoder.initKey();
		String privateKey = encryptBASE64(RSACoder.getPrivateKey(key));
		String publicKey = encryptBASE64(RSACoder.getPublicKey(key));
		System.out.println(privateKey);
		System.out.println(publicKey);
		String sign = signRSA("132", privateKey);
		System.out.println(sign);
		String encrypt = encryptRSAPrivate("132", privateKey);
		System.out.println(encrypt);
		String org = decryptRSAPublic(encrypt, publicKey);
		System.out.println(org);
		System.out.println(verifyRSA(org, publicKey, sign));

		// System.out.println("-------列出加密服务提供者-----");
		// Provider[] pro = Security.getProviders();
		// for (Provider p : pro) {
		// System.out.println("Provider:" + p.getName() + " - version:" +
		// p.getVersion());
		// System.out.println(p.getInfo());
		// }
		// System.out.println("");
		// System.out.println("-------列出系统支持的消息摘要算法：");
		// for (String s : Security.getAlgorithms("MessageDigest")) {
		// System.out.println(s);
		// }
		// System.out.println("-------列出系统支持的生成公钥和私钥对的算法：");
		// for (String s : Security.getAlgorithms("KeyPairGenerator")) {
		// System.out.println(s);
		// }
		System.out.println("===============================================================");
		System.out.println(encryptPassword("shaoxi2016"));*/
		
		Map<String, Object> key = RSACoder.initKey();
		String privateKey = encryptBASE64(RSACoder.getPrivateKey(key));
		String publicKey = encryptBASE64(RSACoder.getPublicKey(key));
		System.out.println("privateKey:"+privateKey);
		System.out.println("publicKey:"+publicKey);
		System.out.println("--------------------私钥签名------------");
		String sign = signRSA("132", privateKey);
		System.out.println("sign:"+sign);
		System.out.println("--------------------私钥加密------------");
		String encrypt = encryptRSAPrivate("132", privateKey);
		System.out.println(encrypt);
		System.out.println("--------------------公钥解密------------");
		String org = decryptRSAPublic(encrypt, publicKey);
		System.out.println(org);
		System.out.println("--------------------公钥验证签名------------");
		System.out.println(verifyRSA(org, publicKey, sign));
	}
}
