package com.jfeat.ext.plugin.redis;

import com.jfinal.plugin.redis.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by jackyhuang on 17/5/3.
 */
public class RedisSubscriber extends JedisPubSub implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);

    private Cache cache;
    private String channel;

    public RedisSubscriber(Cache cache, String channel) {
        this.cache = cache;
        this.channel = channel;
    }

    public void onMessage(String channel, String message) {
        logger.debug(String.format("receive redis published message, channel %s, message %s", channel, message));
    }

    public void onSubscribe(String channel, int subscribedChannels) {
        logger.debug(String.format("subscribe redis channel success, channel %s, subscribedChannels %d", channel, subscribedChannels));
    }

    public void onUnsubscribe(String channel, int subscribedChannels) {
        logger.debug(String.format("unsubscribe redis channel, channel %s, subscribedChannels %d", channel, subscribedChannels));

    }

    @Override
    public void run() {
        Jedis jedis = cache.getJedis();
        try {
            jedis.subscribe(this, channel);
        }
        finally {
            cache.close(jedis);
        }
    }

    public void stop() {
        this.unsubscribe();
    }
}
