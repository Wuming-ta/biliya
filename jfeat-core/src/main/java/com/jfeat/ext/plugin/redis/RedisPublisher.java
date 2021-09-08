package com.jfeat.ext.plugin.redis;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import redis.clients.jedis.Jedis;

/** 发布消息
 * Created by jackyhuang on 17/5/3.
 */
public class RedisPublisher {
    private String channel;
    private Cache cache;

    public RedisPublisher(String channel) {
        this.channel = channel;
        this.cache = Redis.use();
    }

    public RedisPublisher(Cache cache, String channel) {
        this.channel = channel;
        this.cache = cache;
    }

    public void publish(String message) {
        Jedis jedis = cache.getJedis();
        try {
            jedis.publish(this.channel, message);
        }
        finally {
            cache.close(jedis);
        }
    }
}
