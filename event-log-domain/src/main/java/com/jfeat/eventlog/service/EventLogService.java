/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.eventlog.service;

import com.jfeat.core.BaseService;
import com.jfeat.eventlog.model.EventLog;
import com.jfinal.kit.JsonKit;

import java.util.Map;

/**
 * Created by jingfei on 2016/4/8.
 */
public class EventLogService extends BaseService {

    public void record(String eventType, String eventName, Map<String, Object> data) {
        EventLog eventLog = new EventLog();
        eventLog.setEventType(eventType);
        eventLog.setName(eventName);
        eventLog.setData(JsonKit.toJson(data));
        eventLog.save();
    }

    public void record(String eventType, String eventName, String message) {
        EventLog eventLog = new EventLog();
        eventLog.setEventType(eventType);
        eventLog.setName(eventName);
        eventLog.setData(message);
        eventLog.save();
    }

    public void record(String eventType, String eventName, String user, String ip, String userAgent, Map<String, Object> data) {
        EventLog eventLog = new EventLog();
        eventLog.setEventType(eventType);
        eventLog.setName(eventName);
        eventLog.setIp(ip);
        eventLog.setUser(user);
        eventLog.setUserAgent(userAgent);
        eventLog.setData(JsonKit.toJson(data));
        eventLog.save();
    }

    public void record(String eventType, String eventName, String user, String ip, String userAgent, String message) {
        EventLog eventLog = new EventLog();
        eventLog.setEventType(eventType);
        eventLog.setName(eventName);
        eventLog.setIp(ip);
        eventLog.setUser(user);
        eventLog.setUserAgent(userAgent);
        eventLog.setData(message);
        eventLog.save();
    }

}
