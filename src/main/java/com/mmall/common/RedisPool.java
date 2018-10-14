package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool pools;//jedis连接池

    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//连接池最大连接数

    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));//在jedispool中最大idle(空闲)状态的jedis实例的个数

    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","2"));//在jedispool中最大idle(空闲)状态的jedis实例的个数

    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));//在获得一个jedis实例的时候，是否需要验证操作，如果赋值为true，则得到的jedis实例肯定是可以用的

    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.total","true"));//在归还一个jedis实例的时候，是否需要一个验证操作，如果赋值为true,则归还的jedis实例肯定是可用的

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");

    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);//达到最大连接数时，新的连接是否阻塞，false为抛出异常，true为阻塞直到超时，默认为true

        pools = new JedisPool(config,redisIp,redisPort,1000*2);
    }

    static{
        initPool();
    }

    public static Jedis getJedis(){
        return pools.getResource();
    }

    public static void returnResource(Jedis jedis){
        pools.returnResource(jedis);
    }
    public static void returnBrokenResource(Jedis jedis){
        pools.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = getJedis();
        jedis.set("name","wpj");
        returnResource(jedis);
        pools.destroy();
        System.out.println("Program is end!");
    }
}