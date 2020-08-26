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

package com.jfeat.common;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Collection;

/**
 * Created by huangjacky on 16/6/1.
 */
public class AccessTokenRealm  extends AuthorizingRealm {

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof AccessTokenToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Collection principalCollection = principals.fromRealm(getName());
        if (principalCollection != null && principalCollection.size() > 0) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            return info;
        }

        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        AccessTokenToken tokenToken = (AccessTokenToken) authenticationToken;
        String loginName = (String) tokenToken.getPrincipal();
        //for weixin user, openid is loginname
        return new SimpleAuthenticationInfo(new ShiroUser(tokenToken.getId(), loginName, loginName,
                tokenToken.getAccessToken(), tokenToken.getInviteCode(), tokenToken.getSellerStatus(), tokenToken.getInviterId()),
                tokenToken.getCredentials(), getName());
    }
}
