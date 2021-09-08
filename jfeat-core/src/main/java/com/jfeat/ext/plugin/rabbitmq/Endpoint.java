package com.jfeat.ext.plugin.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jackyhuang on 2017/8/16.
 */
public abstract class Endpoint {

    protected static final Logger logger = LoggerFactory.getLogger(Endpoint.class);

    protected String endpointName;
    protected Channel channel;

    public Endpoint(String endpointName) throws IOException {
        this.endpointName = endpointName;

        this.channel = RabbitMQKit.getChannel();

        //declaring a queue for this channel. If queue does not exist,
        //it will be created on the server.
        this.channel.queueDeclare(endpointName, true, false, false, null);
    }
}
