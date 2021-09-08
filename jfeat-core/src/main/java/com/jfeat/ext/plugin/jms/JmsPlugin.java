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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfinal.ext.plugin.config.ConfigPlugin;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import org.omg.PortableInterceptor.INACTIVE;

import javax.jms.MessageListener;
import java.util.List;

public class JmsPlugin implements IPlugin {

    protected final Log logger = Log.getLog(getClass());

    private static final String jmsPropertiesKey = "jms.properties.file";
    private static String sendQueues = "sendQueues";
    private static String sendTopics = "sendTopics";
    private static String receiveQueues = "receiveQueues";
    private static String receiveTopics = "receiveTopics";

    private String serverUrl;
    private String username;
    private String password;

    private JmsSender jmsSender;
    private List<MessageListener> listeners = Lists.newArrayList();

    public JmsPlugin() {
        String propertiesFile = System.getProperty(jmsPropertiesKey, "jms.properties");
        JmsConfig.init(propertiesFile);
    }

    public JmsPlugin(String url, String username, String password) {
        JmsConfig.init(null);
        JmsConfig.setStr("serverUrl", url);
        JmsConfig.setStr("username", username);
        JmsConfig.setStr("password", password);
    }
    
    public void addSendQueue(String queue) {
    	  add(sendQueues, queue);
    }

    public void addSendQueue(String queue, Integer messageType) {
        add(sendQueues, queue);
        String messageKey = "queue." + queue + "." + messageType;
        add(messageKey, messageType.toString());
    }

    public void addReceiveQueue(String queue, Integer messageType, String resolver) {
        addQueue(receiveQueues, queue, messageType, resolver);
    }

    public void addSendTopic(String topic) {
        add(sendTopics, topic);
    }

    public void addSendTopic(String topic, Integer messageType) {
        add(sendTopics, topic);
        String messageKey = "topic." + topic + "." + messageType;
        add(messageKey, messageType.toString());
    }

    public void addReceiveTopic(String topic, Integer messageType, String resolver) {
        addTopic(receiveTopics, topic, messageType, resolver);
    }

    private void addTopic(String topicsKey, String topic, Integer messageType, String resolver) {
        add(topicsKey, topic);
        String messageKey = "topic." + topic + "." + messageType;
        String resolverKey = messageKey + ".resolver";
        add(messageKey, messageType.toString());
        add(resolverKey, resolver);
    }

    private void addQueue(String queuesKey, String queue, Integer messageType, String resolver) {
        add(queuesKey, queue);
        String messageKey = "queue." + queue + "." + messageType;
        String resolverKey = messageKey + ".resolver";
        add(messageKey, messageType.toString());
        add(resolverKey, resolver);
    }

    private void add(String key, String value) {
        String values = JmsConfig.getStr(key);
        if (StrKit.notBlank(values)) {
            if (values.contains(value)) {
                return;
            }
            values += "," + value;
        }
        else {
            values = value;
        }
        JmsConfig.setStr(key, values);
    }

    @Override
    public boolean start() {
        for (String key : JmsConfig.keys())
            logger.debug(key + "=" + JmsConfig.getStr(key));

        initServerConfig();
        initSender();
        initReceiver();
        iniJmsKit();
        return true;
    }

    private void iniJmsKit() {
        JmsKit.init(jmsSender);
    }

    private void initServerConfig() {
        serverUrl = JmsConfig.getStr("serverUrl");
        username = JmsConfig.getStr("username");
        password = JmsConfig.getStr("password");
        logger.debug("serverUrl : " + serverUrl + " ,username : " + username + " ,password : " + password);
    }

    private void initReceiver() {
        String receiveQueuesValue = JmsConfig.getStr(receiveQueues);
        logger.debug("receiveQueues :" + receiveQueuesValue);
        if (StrKit.notBlank(receiveQueuesValue)) {
            for (String queueName : receiveQueuesValue.split(",")) {
                JmsReceive queueReceive = new JmsReceive(new ReceiveResolverFactory("queue." + queueName));
                listeners.add(new QueueListener(serverUrl, username, password, queueName, queueReceive));
            }
        }
        String receiveTopicsValue = JmsConfig.getStr(receiveTopics);
        logger.debug("receiveTopic :" + receiveTopicsValue);
        if (StrKit.notBlank(receiveTopicsValue)) {
            for (String topicName : receiveTopicsValue.split(",")) {
                JmsReceive topicReceive = new JmsReceive(new ReceiveResolverFactory("topic." + topicName));
                listeners.add(new TopicListener(serverUrl, username, password, topicName, topicReceive));
            }
        }
        logger.debug(listeners.toString());
    }

    private void initSender() {
        jmsSender = new JmsSender();
        jmsSender.queueProducers = Maps.newHashMap();
        String sendQueuesValue = JmsConfig.getStr(sendQueues);
        logger.debug("sendQueues :" + sendQueuesValue);
        if (StrKit.notBlank(sendQueuesValue)) {
            for (String queueName : sendQueuesValue.split(",")) {
                jmsSender.queueProducers.put(queueName, new QueueProducer(serverUrl, username, password, queueName));
            }
        }
        String sendTopicsValue = JmsConfig.getStr(sendTopics);

        logger.debug("sendTopics :" + sendTopicsValue);
        if (StrKit.notBlank(sendTopicsValue)) {
            jmsSender.topicPublishers = Maps.newHashMap();
            for (String topicName : sendTopicsValue.split(",")) {
                jmsSender.topicPublishers.put(topicName, new TopicPublisher(serverUrl, username, password, topicName));
            }
        }
    }

    @Override
    public boolean stop() {
//        for (MessageListener listener : listeners) {
//            Reflect.on(listener).call("closeConnection");
//        }
        listeners.clear();
        return true;
    }

}
