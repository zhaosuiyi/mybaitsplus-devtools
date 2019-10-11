package com.mybaitsplus.devtools.core.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import java.util.*;

/**
 * 
 * <p>
 * 	Redis客户端访问
 * </p>
 * 支持单台Redis的非切片链接池
 */  
public class RedisUtil  {
	
	private static final Logger log = LoggerFactory.getLogger(RedisUtil.class); 
	
	public  static  JedisPool jedisPool; // 池化管理jedis链接池
	
	static {
		//读取相关的配置
		String host = PropertiesUtil2.getString("spring.redis.host");
		int port = PropertiesUtil2.getInt("spring.redis.port");
		String password = PropertiesUtil2.getString("spring.redis.password");
		int timeout = PropertiesUtil2.getInt("spring.redis.timeout");
		int minIdle= PropertiesUtil2.getInt("spring.redis.jedis.pool.min-idle");
		int maxIdle = PropertiesUtil2.getInt("spring.redis.jedis.pool.max-idle");
		long maxWaitMillis= PropertiesUtil2.getLong("spring.redis.jedis.pool.max-wait");
		int maxActive = PropertiesUtil2.getInt("spring.redis.jedis.pool.max-active");

		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		jedisPoolConfig.setMaxTotal(maxActive);
		jedisPoolConfig.setMinIdle(minIdle);
		jedisPool = new JedisPool(jedisPoolConfig,host,port,timeout,password);

		log.info("JedisPool注入成功！");
		log.info("redis地址：" + host + ":" + port);
	}
	/**
	 * 向缓存中设置字符串内容
	 * @param  key
	 * @param  value
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean  set(String key,String value)  {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			jedis.close();
		}
	}
	/**
	 * 该方法是原子的，如果key不存在，则设置当前key成功，返回1；
	 * 如果当前key已经存在，则设置当前key失败，返回0。
	 * 但是要注意的是setnx命令不能设置key的超时时间，只能通过expire()来对key设置
	 * @param  key
	 * @param  value
	 * @return boolean
	 * @throws Exception
	 */
	public static long setnx(String key,String value)  {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.setnx(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}finally{
			jedis.close();
		}
	}
	
	/**
	 * 向缓存中设置字符串内容
	 * @param  key
	 * @param  value
	 * @param  expiration 时效 单位秒
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean  set(String key,String value,int expiration)  {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.set(key, value);
			jedis.expire(key, expiration);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			jedis.close();
		}
	}
	
	/**
	 * 向缓存中设置对象
	 * @param key 
	 * @param value 对象
	 * @param  expiration 时效 单位秒
	 * @return boolean
	 */
	public static boolean  set(String key,Object value,int expiration){
		Jedis jedis = null;
		try {
			String objectJson = JSON.toJSONString(value);
			jedis = jedisPool.getResource();
			jedis.set(key, objectJson);
			jedis.expire(key, expiration);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			jedis.close();
		}
	}
	
	/**
	 * 向缓存中设置对象
	 * @param key 
	 * @param value
	 * @return boolean
	 */
	public static boolean  set(String key,Object value){
		Jedis jedis = null;
		try {
			String objectJson = JSON.toJSONString(value);
			jedis = jedisPool.getResource();
			jedis.set(key, objectJson);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			jedis.close();
		}
	}
	
	
	/**
	 * 根据key 获取内容
	 * @param key
	 * @return Object
	 */
	public static Object getObject(String key){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Object value = jedis.get(key);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			jedis.close();
		}
	}
	/**
	 * 根据key 获取内容
	 * @param key
	 * @return String
	 */
	public static String get(String key){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			jedis.close();
		}
	}
	/**
	 * 该方法是原子的，对key设置newValue这个值，并且返回key原来的旧值
	 * @param key
	 * @return String
	 */
	public static String getSet(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.getSet(key,value);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			jedis.close();
		}
	}
	/**
	 * 根据key 获取对象
	 * @param key
	 * @return <T> T
	 */
	public static <T> T get(String key,Class<T> clazz){
		Jedis jedis = null;
		try {
			jedis =jedisPool.getResource();
			String value = jedis.get(key);
			return JSON.parseObject(value, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			jedis.close();
		}
	}
    
	// Redis Hash Operations
	
	/**
	 * 向缓存中设置hash
	 * @param key
	 * @param hash
	 * @return boolean
	 */
	public static boolean  hmset(String key,  Map<String,String> hash) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result=jedis.hmset(key, hash);
            return ("OK".equals(result))?true:false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
        	jedis.close();
        }
    }
	
	/**
	 * 向缓存中设置hash
	 * @param key
	 * @param Map<String,String> hash
	 * @param expiration 单位秒
	 * @return boolean
	 */
    public static  boolean hmset(String key,  Map<String,String> hash,int expiration){
    	 Jedis jedis = null;
         try {
             jedis = jedisPool.getResource();
             String result=jedis.hmset(key, hash);
             jedis.expire(key, expiration);
             return ("OK".equals(result))?true:false;
         } catch (Exception e) {
             e.printStackTrace();
             return false;
         } finally {
         	jedis.close();
         }
    }
    
	/**
	 * 向缓存中设置hash
	 * @param key
	 * @param Map<String,String> hash
	 * @param isEmpty 是否 先清空之前的数据 重新设置
	 * @return boolean
	 */
	public static boolean hmsetMe(String key,  Map<String,String> hash,boolean isEmpty) {
		if(isEmpty)del(key);
        return hmset(key,hash);
    }
	
	/**
	 * 向缓存中重新设置hash 清空之前的数据 
	 * @param key
	 * @param hash
	 * @param expiration 单位秒
	 * @return boolean
	 */
    public static  boolean hmsetMe(String key,  Map<String,String> hash,boolean isEmpty,int expiration){
    	if(isEmpty)del(key);
        return hmset(key,hash,expiration);
    }
    
    /**
	 * 向缓存中设置hash
	 * @param key
	 * @param Map<Object, Object> map
	 * @return boolean
	 */
    public static boolean hmsetMe(String key, Map<Object, Object> map) {
		if (map.isEmpty()) {
			log.info("hmsetMe param is empty, param = map");
			return false;
		}
		Map<String, String> hash = new HashMap<String, String>();
		for (Object obj : map.keySet()) {
			hash.put(String.valueOf(obj), String.valueOf(map.get(obj)));
		}
		return hmset(key,hash);
	}
    
    /**
	 * 向缓存中设置多个hash
	 * @param key
	 * @param List<Map<String,Object>> list
	 * @param field 字段
	 * @return int 执行结果
	 */
    public static int hmsetMe(String key, List<Map<String,Object>> list,String field) {
    	Jedis jedis = null;
    	int count=0;
        try {
			if (list.isEmpty()) {
				log.info("hmsetMe param is empty, param = map");
				return -1;
			}
			jedis = jedisPool.getResource();
			Pipeline p = jedis.pipelined();
			Map<String, String> hash = new HashMap<String, String>();
			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> map=list.get(i);
				if(map!=null&&!map.isEmpty()){
					for (Object obj : map.keySet()) {
						hash.clear();
						hash.put(String.valueOf(obj), String.valueOf(map.get(obj)));
						p.hmset(key + map.get(field), hash);
					}
					count++;
				}
			}
			p.sync();
			return count;
        } catch (JedisException e) {
            e.printStackTrace();
            return -1;
        } finally {
        	jedis.close();
        }
	}
    
    
    /**
	 * 从缓存中获取hash
	 * @param key
	 * @return Map<String,String>
	 */
    public static Map<String,String> hgetAll(String key) {
    	Map<String,String> map=null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            map=jedis.hgetAll(key);
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
        	jedis.close();
        }
        return map;
    }
    /**
	 * 向缓存中的hash设置一个字段的
	 * @param key
	 * @param field
	 * @param value 
	 * @return long 0 如果字段已经存在于哈希并且值被更新  1 如果字段是哈希值和一个新字段被设置 -1 出错
	 */
    public static long hset(String key,String field,String value){
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            long result=jedis.hset(key, field, value);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
        	jedis.close();
        }
    }
    /**
	 * 从hash获取其中一个字段的值
	 * @param key
	 * @param field
	 * @return String
	 */
    public static String hget(String key,String field){
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hget(key, field);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        	jedis.close();
        }
    }
    /**
	 * 从hash获取 多个字段的值
	 * @param key
	 * @param fields 字符串数组
	 * @return List<String>
	 */
    public static List<String>  hmget(String key,String... fields){
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hmget(key, fields);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	jedis.close();
        }
        return null;
    }
    /**
     * 判断hash 字段是否存在
     * @param key
     * @param field
     * @return boolean
     */
    public static boolean hexists(String key,String field){
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hexists(key, field);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
        	jedis.close();
        }
    }
    /**
     * 获取hash  包含字段的数量
     * @param  key
     * @return long
     */
    public static long hlen(String key){
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hlen(key);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
        	jedis.close();
        }
    }
    /**
     * 删除hash  多个字段
     * @param  key
     * @param fields 字符串数组
     * @return long
     */
    public static long hdel(String key,String... fields){
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hdel(key, fields);
        } catch (JedisException e) {
            e.printStackTrace();
            return -1;
        } finally {
        	jedis.close();
        }
    }
    
    public static Map<String, Map<String, String>> hkeys(String key) {
		Jedis jedis = null;
		Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>();
        try {
            jedis = jedisPool.getResource();
            Set<String> keys=jedis.keys(key);
            for(String key_ : keys) {
              result.put(key_, jedis.hgetAll(key_));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	jedis.close();
        }
        return result;
	}
	/**
     * 判断 key  是否存在
     * @param  key
     * @return Map<String, Map<String, String>> 
     */
	public static Map<String, Map<String, String>> hkeysPipeLine(String key) {
		Jedis jedis = null;
		Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>();
        try {
            jedis = jedisPool.getResource();
            Pipeline p = jedis.pipelined();
            Set<String> keys=jedis.keys(key);
            //使用pipeline hgetall
            Map<String,Response<Map<String,String>>> responses = new HashMap<String,Response<Map<String,String>>>(keys.size());
            for(String key_ : keys) {
            	responses.put(key_, p.hgetAll(key));
            }
            p.sync(); 
            for(String k : responses.keySet()) {
                result.put(k, responses.get(k).get());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        	jedis.close();
        }
	}
	
	public static Map<String, String> keys(String key) {
		Jedis jedis = null;
		Map<String,String> result = new HashMap<String,String>();
        try {
            jedis = jedisPool.getResource();
            Set<String> keys=jedis.keys(key);
            for(String key_ : keys) {
              result.put(key_, jedis.get(key_));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        	jedis.close();
        }
	}
	//list
	/**
     * 存储REDIS队列 顺序存储
     * @param byte[] key reids键名
     * @param byte[] value 键值
     */
    public static void lpush(byte[] key, byte[] value) {
 
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	jedis.close();
 
        }
    }
 
    /**
     * 存储REDIS队列 反向存储
     * @param byte[] key reids键名
     * @param byte[] value 键值
     */
    public static void rpush(byte[] key, byte[] value) {
 
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.rpush(key, value);
        } catch (Exception e) {
            e.printStackTrace();
 
        } finally {
        	jedis.close();
        }
    }
    
    /**
     * 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端
     * @param byte[] key reids键名
     * @param byte[] value 键值
     */
    public static void rpoplpush(byte[] key, byte[] destination) {
 
        Jedis jedis = null;
        try {
 
            jedis = jedisPool.getResource();
            jedis.rpoplpush(key, destination);
 
        } catch (Exception e) {
            e.printStackTrace();
 
        } finally {
        	jedis.close();
        }
    }
 
    /**
     * 获取队列数据
     * @param byte[] key 键名
     * @return
     */
    public static List<byte[]> lpopList(byte[] key) {
 
        List<byte[]> list = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            list = jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	jedis.close();
        }
        return list;
    }
 
    /**
     * 获取队列数据
     * @param byte[] key 键名
     * @return
     */
    public static byte[] rpop(byte[] key) {
 
        byte[] bytes = null;
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            bytes = jedis.rpop(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	jedis.close();
        }
        return bytes;
    }
 
	public static void lpush(String key,String... strings){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			jedis.lpush(key, strings);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			jedis.close();
		}
	}
	
	public static String rpop(String key){
		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			return jedis.rpop(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			jedis.close();
		}
	}
	
	 public static List<byte[]> lrange(byte[] key, int start, int end) {
	        List<byte[]> result = null;
	        Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            result = jedis.lrange(key, start, end);
	        } catch (Exception e) {
	            e.printStackTrace();
	 
	        } finally {
	        	jedis.close();
	        }
	        return result;
    }
	 
	 public static List<String> lrange(String key, int start, int end) {
		 	List<String>  result = null;
	        Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            result = jedis.lrange(key, start, end);
	        } catch (Exception e) {
	            e.printStackTrace();
	 
	        } finally {
	        	jedis.close();
	        }
	        return result;
	 }
	 
	 public static long llen(String key){
	    	Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.llen(key);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1;
	        } finally {
	        	jedis.close();
	        }
	    }
	 
	//	Redis Common Operations
		/**
		 * 删除缓存中得对象，根据key
		 * @param key
		 * @return boolean
		 */
		public static boolean del(String key){
			Jedis jedis = null;
			try {
				jedis = jedisPool.getResource();
				jedis.del(key);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}finally{
				jedis.close();
			}
		}
		
		/**
		 * 设置 key 时效
		 * @param key
		 * @param seconds
		 * @return long
		 */
		public static long expire(String key, int seconds){
	    	Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.expire(key, seconds);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1;
	        } finally {
	        	jedis.close();
	        }
	    }
		
		/**
		 * <p>
		 * key 更名
		 * </p>
	     * @param  oldKey
	     * @param  newKey
	     * @return boolean 不存在 oldkey或者 出错  都会返回 false 
	     */
		public static boolean reName(String oldKey, String newKey) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            if(jedis.exists(oldKey))return "OK".equals(jedis.rename(oldKey, newKey))?true:false;
	            return false;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 * <p>
		 * 获取 key 的剩余 时效
		 * </p>
	     * @param  key
	     * @return long
	     */
		public static  long ttl(String key) {
			 Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.ttl(key);
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	jedis.close();
	        }
			return 0l;
		}
		
		/**
	     * 判断 key  是否存在
	     * @param  key
	     * @return long
	     */
		public static boolean exists(String key) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.exists(key);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        } finally {
	        	jedis.close();
	        }
		}
		
		/**
	     * 根据key 批量删除   
	     * @param  key
	     * @return int 执行数量
	     */
		public static int batchDel(String key){
			Jedis jedis = null;
			int count=0;
	        try {
	            jedis = jedisPool.getResource();
	            Set<String> set = jedis.keys(key);
	            Iterator<String> it = set.iterator();  
	            Pipeline p = jedis.pipelined();
	            while(it.hasNext()){  
	                String keyStr = it.next();  
	                p.del(keyStr);
	                count++;
	            }  
	            p.sync();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	jedis.close();
	        }
	        return count;
		}
		
		public static int batchDel(String key,List<String> keys){
			Jedis jedis = null;
			int count=0;
	        try {
	            jedis = jedisPool.getResource();
	            Pipeline p = jedis.pipelined();
	            for (int i = 0; i < keys.size(); i++) {
	            	String key_="";
	            	if(StringUtil.isNotBlank(key)){
	            		key_=key;
	            	}
	            	p.del(key_+keys.get(i));
				}
	            p.sync();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	        	jedis.close();
	        }
	        return count;
		}
		
		public static Long incr(String key) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.incr(key);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1l;
	        } finally {
	        	jedis.close();
	        }
		}
		
		//set
		/**
		 * 添加
		 * 解释：对特定key的set增加一个或多个值，返回是增加元素的个数。注意：对同一个member多次add，set中只会保留一份
		 */
		public static Long sadd(String key,String... members) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.sadd(key,members);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1l;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 * 查询
		 *解释： 获取set中的所有member
		 */
		public static Set<String> smembers(String key) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return  jedis.smembers(key);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 *解释：判断值是否是set的member。如果值是set的member返回true，否则，返回false 
		 */
		public static boolean sismember(String key,String member) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.sismember(key,member);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return false;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 * 解释：返回set的member个数，如果set不存在，返回0
		 */
		public static Long scard(String key) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.scard(key);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1l;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 * 解释：移除一个或多个member
		 */
		public static Long srem(String key,String... members) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.srem(key,members);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1l;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 * 
		 * 解释：将fromKey中的member移动到toKey
		 */
		public static Long smove(String fromKey,String member,String toKey) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.smove(fromKey,member,toKey);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1l;
	        } finally {
	        	jedis.close();
	        }
		}
		
		/**
		 * 并集
		 * 解释：多个set的并集
		 */
		public static Set<String>  sunion(String... keys) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.sunion(keys);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 * 把并集结果存储到set
		 * 解释：求多个set并集，并把结果存储到toKeys 
		 */
		public static Long sunionstore(String toKeys,String... fromKeys) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.sunionstore(toKeys,fromKeys);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1l;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 * 交集
		 * 解释：多个set的交集
		 */
		public static Set<String>  sinter(String... keys) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.sinter(keys);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 * 把交集结果存储到指定set
		 * 解释：把多个set的交集结果存储到toKeys 
		 */
		public static Long sinterstore(String toKeys,String... fromKeys) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.sinterstore(toKeys,fromKeys);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1l;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 * set中在其他set中不存在member
		 */
		public static Set<String>  sdiff(String... keys) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.sdiff (keys);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        } finally {
	        	jedis.close();
	        }
		}
		/**
		 * 把set中在其他set中不存在的member存储到新的set
		 */
		public static Long sdiffstore(String toKeys,String... fromKeys) {
			Jedis jedis = null;
	        try {
	            jedis = jedisPool.getResource();
	            return jedis.sdiffstore(toKeys,fromKeys);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return -1l;
	        } finally {
	        	jedis.close();
	        }
		}
		
		public static Jedis getJedis(){
			return jedisPool.getResource();
		}
		
		public static void closeJedis(Jedis jedis){
			 jedis.close();;
		}
		
}
