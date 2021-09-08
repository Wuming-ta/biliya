package com.jfeat.plugintest.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfeat.ext.plugin.rabbitmq.QueueConsumer;
import com.jfinal.kit.StrKit;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author jackyhuang
 * @date 2018/12/20
 */
public class TestQueueConsumer extends QueueConsumer {


    public TestQueueConsumer(String endPointName) throws IOException, TimeoutException {
        super(endPointName);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope env, AMQP.BasicProperties props, byte[] body) {
        try {
            String message = SerializationUtils.deserialize(body);
            if (StrKit.isBlank(message)) {
                logger.debug("message is empty.");
                return;
            }

            logger.debug("message is {}", message);

        } catch (Exception ex) {
            logger.error("consumer error. {}", ex.getMessage());
        }
    }
}
