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

package com.jfeat.wechat.notification;

import com.google.common.collect.Maps;
import com.jfeat.ext.plugin.async.AsyncTaskKit;

import java.util.Map;

/**
 * Created by jackyhuang on 17/2/15.
 */
public abstract class AbstractNotification {
    private String openid;
    private String templateMessageName;
    private Map<String, String> params = Maps.newHashMap();
    private String url;

    public AbstractNotification(String openid, String templateMessageName) {
        this.openid = openid;
        this.templateMessageName = templateMessageName;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public AbstractNotification param(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public AbstractNotification setUrl(String url) {
        this.url = url;
        return this;
    }

    public void send() {
        //TODO use mq later
        AsyncTaskKit.submit(new NotificationTask(openid, templateMessageName, params, url));
    }
}
