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

package com.jfeat.identity.subject;

import com.jfeat.observer.ObserverKit;
import com.jfeat.observer.Subject;

/**
 * Created by huangjacky on 16/7/12.
 */
public class AttemptingUpdateInviterSubject implements Subject {

    public static final int EVENT_ATTEMPTING_UPDATE = 100;

    private Integer userId;
    private Integer invitorId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInvitorId() {
        return invitorId;
    }

    public void setInvitorId(Integer invitorId) {
        this.invitorId = invitorId;
    }

    public AttemptingUpdateInviterSubject(Integer userId, Integer invitorId) {
        this.userId = userId;
        this.invitorId = invitorId;
    }

    public void notifyObserver() {
        notifyAllObserver(this, EVENT_ATTEMPTING_UPDATE, invitorId);
    }

    @Override
    public void notifyAllObserver(Subject subject, int event, Object param) {
        ObserverKit.me().notifyObserverSync(subject, event, param);
        ObserverKit.me().notifyObserver(subject, event, param);
    }
}
