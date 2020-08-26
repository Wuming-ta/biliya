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

package com.jfeat.service.impl;

import com.jfeat.config.model.Config;
import com.jfeat.identity.filter.sys.SysAuthorizationProvider;
import com.jfinal.kit.StrKit;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by jackyhuang on 17/2/16.
 */
public class SysAuthorizationProviderDbImpl implements SysAuthorizationProvider {

    private String userName;
    private String password;
    private Collection<String> allowIps;

    private static long expiredTime = 30 * 60 * 1000; // 30 min
    private long cachedTime = System.currentTimeMillis();

    private boolean isExpired() {
        long current = System.currentTimeMillis();
        if ((cachedTime + expiredTime - current) < 0) {
            cachedTime = current;
            return true;
        }
        return false;
    }

    private synchronized void retrieveData() {
        userName = "";
        Config userNameConfig = Config.dao.findByKey("sys.auth.username");
        if (userNameConfig != null) {
            userName = userNameConfig.getValueToStr();
        }
        password = "";
        Config passwordConfig = Config.dao.findByKey("sys.auth.password");
        if (passwordConfig != null) {
            password = passwordConfig.getValueToStr();
        }
        Config allowIpsConfig = Config.dao.findByKey("sys.auth.allowips");
        allowIps = new ArrayList<>();
        if (allowIpsConfig != null && StrKit.notBlank(allowIpsConfig.getValueToStr())) {
            for (String ip : allowIpsConfig.getValueToStr().split(",")) {
                if (StrKit.notBlank(ip.trim())) {
                    allowIps.add(ip.trim());
                }
            }
        }
    }

    @Override
    public String getUserName() {
        if (userName == null || isExpired()) {
            retrieveData();
        }
        return userName;
    }

    @Override
    public String getPassword() {
        if (password == null || isExpired()) {
            retrieveData();
        }
        return password;
    }

    @Override
    public Collection<String> getAllowIps() {
        if (allowIps == null || isExpired()) {
            retrieveData();
        }
        return allowIps;
    }
}
