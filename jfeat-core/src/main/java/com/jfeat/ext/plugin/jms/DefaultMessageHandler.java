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

public class DefaultMessageHandler implements IMessageHandler {
    
    protected final Log logger = Log.getLog(getClass());
    @Override
    public void handleMessage(Message message) {
        if (message instanceof ObjectMessage) {
            logger.info("receive object message...");
        } else {
            logger.info("receive message...");
        }
    }
}
