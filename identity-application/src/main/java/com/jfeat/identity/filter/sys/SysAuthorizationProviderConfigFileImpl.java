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

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by jackyhuang on 17/2/16.
 */
public class SysAuthorizationProviderConfigFileImpl implements SysAuthorizationProvider {

    private String userName = "sys";
    private String password = "sys";
    private Collection<String> allowIps = new ArrayList<>();

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAllowIps(Collection<String> allowIps) {
        this.allowIps = allowIps;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<String> getAllowIps() {
        return allowIps;
    }
}
