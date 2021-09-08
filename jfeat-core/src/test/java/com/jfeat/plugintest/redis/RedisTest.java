package com.jfeat.plugintest.redis;

import com.jfeat.ext.plugin.redis.RedisPublisher;
import com.jfeat.ext.plugin.redis.RedisSubscriber;
import com.jfeat.ext.plugin.redis.RedisSubscriberThread;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import org.junit.Test;

/**
 * Created by jackyhuang on 17/5/3.
 */
public class RedisTest {

    //@Test
    public void test() throws InterruptedException {
        RedisPlugin redisPlugin = new RedisPlugin("test", "localhost");
        redisPlugin.start();
        Redis.use().set("k", "v");
        System.out.println("res" + Redis.use().get("k"));

        String channel = "__keyevent@0__:expired";
        RedisSubscriber subscriber = new RedisSubscriber(Redis.use(), channel);
        Thread thread = new RedisSubscriberThread(subscriber);
        thread.start();

        RedisPublisher publisher = new RedisPublisher(channel);
        publisher.publish("haha");

        thread.join();
    }
}
