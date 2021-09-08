package com.jfeat.ext.plugin.redis;

/**
 * Created by jackyhuang on 17/5/3.
 */
public class RedisSubscriberThread extends Thread {

    public RedisSubscriberThread(RedisSubscriber subscriber) {
        super(subscriber);
    }
}
