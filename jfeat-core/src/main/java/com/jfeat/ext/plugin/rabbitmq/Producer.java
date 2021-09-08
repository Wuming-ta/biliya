package com.jfeat.ext.plugin.rabbitmq;

import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by jackyhuang on 2017/8/16.
 */
public class Producer extends Endpoint {

    public Producer(String endpointName) throws IOException {
        super(endpointName);
    }

    public void sendMessage(Serializable object) throws IOException {
        logger.debug("### MQ ### send message: {}", object);
        channel.basicPublish("", endpointName, null, SerializationUtils.serialize(object));
    }
}
