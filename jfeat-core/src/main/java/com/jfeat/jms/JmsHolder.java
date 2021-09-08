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

package com.jfeat.jms;

import com.jfeat.ext.plugin.jms.JmsPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ehngjen on 3/24/2015.
 */
public class JmsHolder {

    private static Logger logger = LoggerFactory.getLogger(JmsHolder.class);

    private JmsPlugin jmsPlugin;
    private List<Receiver> topicReceivers = new LinkedList<>();
    private List<Receiver> queueReceivers = new LinkedList<>();
    private List<String> queues = new LinkedList<>();
    private List<String> topics = new LinkedList<>();
    private static JmsHolder me = new JmsHolder();

    public static JmsHolder me() {
        return me;
    }

    public void addSendQueue(String queue) {
        if (!queues.contains(queue)) {
            queues.add(queue);
        }
    }

    public void addReceiveQueue(String queue, Integer messageType, String resolver) {
        Receiver receiver = new Receiver(queue, messageType, resolver);
        if (!queueReceivers.contains(receiver)) {
            queueReceivers.add(receiver);
        }
    }

    public void addSendTopic(String topic) {
        if (!topics.contains(topic)) {
            topics.add(topic);
        }
    }

    public void addReceiveTopic(String topic, Integer messageType, String resolver) {
        Receiver receiver = new Receiver(topic, messageType, resolver);
        if (!topicReceivers.contains(receiver)) {
            topicReceivers.add(receiver);
        }
    }

    public void registerPlugin(JmsPlugin jmsPlugin) {
        this.jmsPlugin = jmsPlugin;
        for (String queue : queues) {
            logger.debug("addSendQueue(" + queue + ")");
            jmsPlugin.addSendQueue(queue);
        }
        for (String topic : topics) {
            logger.debug("addSendTopic(" + topic + ")");
            jmsPlugin.addSendTopic(topic);
        }
        for (Receiver receiver : queueReceivers) {
            logger.debug("addReceiveQueue(" + receiver.getName() + ", " + receiver.getMessageType() + ", " + receiver.getResolver() + ")");
            jmsPlugin.addReceiveQueue(receiver.getName(), receiver.getMessageType(), receiver.getResolver());
        }
        for (Receiver receiver : topicReceivers) {
            logger.debug("addReceiveTopic(" + receiver.getName() + ", " + receiver.getMessageType() + ", " + receiver.getResolver() + ")");
            jmsPlugin.addReceiveTopic(receiver.getName(), receiver.getMessageType(), receiver.getResolver());
        }
    }
}
