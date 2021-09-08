package com.jfeat.ext.plugin.rabbitmq;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by jackyhuang on 2017/8/16.
 */
public class QueueConsumer extends Endpoint implements Runnable, Consumer {

    public QueueConsumer(String endPointName) throws IOException, TimeoutException {
        super(endPointName);
    }

    @Override
    public void run() {
        try {
            //start consuming messages. Auto acknowledge messages.
            channel.basicConsume(endpointName, true,this);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Called when consumer is registered.
     */
    @Override
    public void handleConsumeOk(String consumerTag) {
        logger.debug("Consumer "+consumerTag +" registered");
    }

    /**
     * Called when new message is available.
     */
    @Override
    public void handleDelivery(String consumerTag, Envelope env,
                               AMQP.BasicProperties props, byte[] body) {
        String message = SerializationUtils.deserialize(body);
        logger.debug("### MQ ### received message: {}", message);
    }

    @Override
    public void handleCancel(String consumerTag) {}
    @Override
    public void handleCancelOk(String consumerTag) {}
    @Override
    public void handleRecoverOk(String consumerTag) {}
    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {}
}
