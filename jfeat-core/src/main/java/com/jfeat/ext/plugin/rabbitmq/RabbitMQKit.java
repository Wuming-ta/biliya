package com.jfeat.ext.plugin.rabbitmq;

import com.rabbitmq.client.Channel;

/**
 * Created by jackyhuang on 2017/8/16.
 */
public class RabbitMQKit {

    private static Channel channel;

    public static boolean isInited() {
        return channel != null;
    }

    public static void init(Channel channel) {
        RabbitMQKit.channel = channel;
    }

    public static Channel getChannel() {
        return channel;
    }

}
