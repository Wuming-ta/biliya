/*
 * Copyright (C) 2014-2015 by ehngjen @ www.jfeat.com
 *
 *  The program may be used and/or copied only with the written permission
 *  from JFeat.com, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */
package com.jfeat.ext.plugin.jms;

import java.io.Serializable;
import java.util.Map;

import com.jfinal.log.Log;

public class JmsSender {
    protected final Log logger = Log.getLog(getClass());

    Map<String, QueueProducer> queueProducers;
    Map<String, TopicPublisher> topicPublishers;

    public boolean queueSend(String queueName, Serializable message, int msgType) {
        if (queueProducers == null) {
            logger.error("JmsPlugin not start");
            return false;
        }
        QueueProducer queueProducer = queueProducers.get(queueName);
        logger.info("send msg " + message + "to queue " + queueName + " ,msgType " + msgType);
        return queueProducer.sendMessage(message, msgType);
    }

    public boolean topicSend(String topicName, Serializable message, int msgType) {
        if (topicPublishers == null) {
            logger.error("JmsPlugin not start");
            return false;
        }

        TopicPublisher topicPublisher = topicPublishers.get(topicName);
        logger.info("send msg " + message + "to topic " + topicName);
        return topicPublisher.publishMessage(message, msgType);
    }
}
