package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

public class RedisShardedPool {
    private static ShardedJedisPool pools;//sharded jedis连接池

    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//连接池最大连接数

    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));//在jedispool中最大idle(空闲)状态的jedis实例的个数

    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","2"));//在jedispool中最大idle(空闲)状态的jedis实例的个数

    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));//在获得一个jedis实例的时候，是否需要验证操作，如果赋值为true，则得到的jedis实例肯定是可以用的

    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.total","true"));//在归还一个jedis实例的时候，是否需要一个验证操作，如果赋值为true,则归还的jedis实例肯定是可用的

    private static String redisIp1 = PropertiesUtil.getProperty("redis.ip1");

    private static Integer redisPort1 = Integer.parseInt(PropertiesUtil.getProperty("redis.port1"));

    private static String redisIp2 = PropertiesUtil.getProperty("redis.ip2");

    private static Integer redisPort2 = Integer.parseInt(PropertiesUtil.getProperty("redis.port2"));

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);//达到最大连接数时，新的连接是否阻塞，false为抛出异常，true为阻塞直到超时，默认为true

        JedisShardInfo info1 = new JedisShardInfo(redisIp1,redisPort1,2 * 1000);
        JedisShardInfo info2 = new JedisShardInfo(redisIp2,redisPort2,2 * 1000);
        List<JedisShardInfo> list = new ArrayList<JedisShardInfo>();
        list.add(info1);
        list.add(info2);
        pools = new ShardedJedisPool(config,list, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);

    }

    static{
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pools.getResource();
    }

    public static void returnResource(ShardedJedis jedis){
        pools.returnResource(jedis);
    }
    public static void returnBrokenResource(ShardedJedis jedis){
        pools.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = getJedis();
        for(int i = 1;i < 10;i++){
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);
//        pools.destroy();
        System.out.println("Program is end!");
    }
}
