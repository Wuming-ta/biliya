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
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.HttpSession;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 *
 * 基于ehcache实现的Flash管理器
 * Created by ehngjen on 5/4/2015.
 */
public class EhCacheFlashManager implements IFlashManager{
    /**
     * ehcache 中的cache名称。
     */
    private final String flashCacheName;

    /**
     * 锁
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * 构造函数
     * @param flashCacheName  ehcache 中的cache名称。
     */
    public EhCacheFlashManager(String flashCacheName ) {
        if (StrKit.isBlank(flashCacheName)){
            throw new IllegalArgumentException("flashCacheName can not be blank.");
        }
        this.flashCacheName = flashCacheName;
    }

    @SuppressWarnings("unchecked")
    public void setFlash(HttpSession session, String curAction, String key,
                         Object value) {
        String sessionKey = session.getId();
        sessionKey = sessionKey + curAction.replace("/", "_");
        lock.lock();
        Object obj = CacheKit.get(flashCacheName, sessionKey);
        Map<String, Object> map = null;
        if (obj != null) {
            map = (Map<String, Object>) obj;
        } else {
            map = new ConcurrentHashMap<String, Object>();
            CacheKit.put(flashCacheName, sessionKey, map);
        }
        lock.unlock();
        map.put(key, value);
    }

    public void updateFlashKey(HttpSession session, String curAction,
                               String nextAction) {
        String sessionKey = session.getId();
        String oldKey = sessionKey + curAction.replace("/", "_");
        String newkey = sessionKey + nextAction.replace("/", "_");
        lock.lock();
        Object obj = CacheKit.get(flashCacheName, oldKey);
        if (obj != null) {
            CacheKit.remove(flashCacheName, oldKey);
            CacheKit.put(flashCacheName, newkey, obj);
        }
        lock.unlock();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getFlash(HttpSession session, String curAction) {
        String sessionKey = session.getId();
        String sessionActionKey = sessionKey + curAction.replace("/", "_");
        Map<String, Object> map = null;
        lock.lock();
        Object obj = CacheKit.get(flashCacheName, sessionActionKey);
        if (obj != null) {
            map = (Map<String, Object>) obj;
            CacheKit.remove(flashCacheName, sessionActionKey);
        }
        lock.unlock();
        return map;
    }
}
