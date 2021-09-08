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

package com.jfeat.flash;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

/**
 *
 * 基于Session实现的Flash管理器
 *
 * Created by ehngjen on 5/4/2015.
 */
public class SessionFlashManager implements IFlashManager{

    /**
     *默认存储session前缀
     */
    private final static String sessionKeyPrefix = "_flash_";

    /**
     * 构造函数
     */
    public SessionFlashManager() {
    }

    @SuppressWarnings("unchecked")
    public void setFlash(HttpSession session, String curAction, String key,
                         Object value) {
        String sessionKey = sessionKeyPrefix + curAction.replace("/", "_");
        Object obj = session.getAttribute(sessionKey);
        Map<String, Object> map = null;
        if (obj != null) {
            map = (Map<String, Object>) obj;
        } else {
            map = new ConcurrentHashMap<String, Object>();
            session.setAttribute(sessionKey, map);
        }
        map.put(key, value);
    }

    public void updateFlashKey(HttpSession session, String curAction,
                               String nextAction) {
        String oldKey = sessionKeyPrefix + curAction.replace("/", "_");
        String newkey = sessionKeyPrefix + nextAction.replace("/", "_");
        Object obj = session.getAttribute(oldKey);
        if (obj != null) {
            session.removeAttribute(oldKey);
            session.setAttribute(newkey, obj);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getFlash(HttpSession session, String curAction) {
        String sessionActionKey = sessionKeyPrefix + curAction.replace("/", "_");
        Map<String, Object> map = null;
        Object obj = session.getAttribute(sessionActionKey);
        if (obj != null) {
            map = (Map<String, Object>) obj;
            session.removeAttribute(sessionActionKey);
        }
        return map;
    }
}
