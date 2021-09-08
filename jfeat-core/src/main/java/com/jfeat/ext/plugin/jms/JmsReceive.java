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

import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.jfinal.log.Log;

public class JmsReceive implements IMessageHandler {
    
    protected final Log logger = Log.getLog(getClass());
    
    private ReceiveResolverFactory receiveResolverFactory;

    public JmsReceive(ReceiveResolverFactory receiveResolverFactory) {
        this.receiveResolverFactory = receiveResolverFactory;
    }

    @Override
    public void handleMessage(Message message) {
        try {
            int messageType = message.getIntProperty(JMSConstants.JMS_MESSAGE_TYPE);
            ObjectMessage objMsg = (ObjectMessage) message;
            logger.debug("msgType " + messageType + " objMsg :" + objMsg);
            ReceiveResolver resolver = this.receiveResolverFactory.createReceiveResolver(new Integer(messageType));
            if (resolver == null) {
                logger.error("cant find  ReceiveResolver with messageType = " + messageType);
                return;
            }
            resolver.resolve(objMsg.getObject());
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
