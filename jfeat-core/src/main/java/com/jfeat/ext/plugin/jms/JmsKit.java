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

public class JmsKit {
    private static JmsSender jmsSender;

    static void init(JmsSender jmsSender) {
        JmsKit.jmsSender = jmsSender;
    }

    public static boolean sendQueue(String queueName, Serializable message, String msgName) {
        return jmsSender.queueSend(queueName, message, JmsConfig.getInt("queue." + queueName + "." + msgName));
    }

    public static boolean sendQueue(String queueName, Serializable message, Integer messageType) {
        return jmsSender.queueSend(queueName, message, messageType);
    }

    public static boolean sendTopic(String topicName, Serializable message, String msgName) {
        return jmsSender.topicSend(topicName, message, JmsConfig.getInt("topic." + topicName + "." + msgName));
    }
    
    public static boolean sendTopic(String topicName, Serializable message, Integer messageType) {
        return jmsSender.topicSend(topicName, message, messageType);
    }
}
