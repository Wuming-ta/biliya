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

import java.util.HashMap;
import java.util.Map;

import com.jfinal.log.Log;

public class ReceiveResolverFactory {

    public static final String RESOLVER_SUFFIX = ".resolver";

    protected final Log logger = Log.getLog(getClass());

    private Map<Integer, ReceiveResolver> receiveResolverMap = new HashMap<Integer, ReceiveResolver>();
    private Map<String, Integer> messageTypeMap;

    private String typeFilter;

    public ReceiveResolverFactory(String typeFilter) {
        this.typeFilter = typeFilter;
    }

    public ReceiveResolver createReceiveResolver(Integer messageType) {
        logger.debug(" receive messageType " + messageType);
        if (messageTypeMap == null) {
            init();
        }
        return receiveResolverMap.get(messageType);
    }

    public Integer getMessageType(String key) {
        if (messageTypeMap == null) {
            init();
        }
        return messageTypeMap.get(key);
    }

    private synchronized void init() {
        messageTypeMap = new HashMap<String, Integer>();
        try {
            loadReceiveResolver();
        } catch (Exception e) {
            logger.error("load ReceiveResolver error", e);
        }
        logger.debug("resolvers in  " + typeFilter + " :" + receiveResolverMap);
    }

    private void loadReceiveResolver() {
        for (String key : JmsConfig.keys()) {
            if (key.startsWith(typeFilter) && !key.endsWith(RESOLVER_SUFFIX)) {
                Integer messageType = new Integer(JmsConfig.getStr(key));
                String messageResolver = JmsConfig.getStr(key + RESOLVER_SUFFIX);
                messageTypeMap.put(key, messageType);
                try {
                    receiveResolverMap.put(messageType, (ReceiveResolver) Class.forName(messageResolver).newInstance());
                } catch (Exception e) {
                    logger.error("cant create " + messageResolver,e);
                }
            }
        }
    }
}
