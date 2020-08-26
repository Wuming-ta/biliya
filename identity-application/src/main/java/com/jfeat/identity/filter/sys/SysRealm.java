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

package com.jfeat.identity.filter.sys;

import com.jfeat.core.Service;
import com.jfeat.core.ServiceContext;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by jacky on 3/3/16.
 */
public class SysRealm {

    public static SysAuthorizationProvider defaultSysAuthorizationProvider;

    private String userName = "sys";
    private String password = "sys";
    private Collection<String> allowIps = new ArrayList<>();

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<String> getAllowIps() {
        return allowIps;
    }

    void setAllowIps(Collection<String> allowIps) {
        this.allowIps = allowIps;
    }

    public void init() {
        SysAuthorizationProvider sysAuthorizationProvider = defaultSysAuthorizationProvider;
        Service service = ServiceContext.me().getService(SysAuthorizationProvider.class.getName());
        if (service != null) {
            sysAuthorizationProvider = (SysAuthorizationProvider) service;
        }

        userName = sysAuthorizationProvider.getUserName();
        password = sysAuthorizationProvider.getPassword();
        allowIps = sysAuthorizationProvider.getAllowIps();
    }
}
