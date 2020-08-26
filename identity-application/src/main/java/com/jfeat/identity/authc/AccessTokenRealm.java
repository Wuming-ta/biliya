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

package com.jfeat.identity.authc;

import com.jfeat.identity.model.Role;
import com.jfeat.identity.model.User;
import com.jfeat.kit.Digests;
import com.jfeat.kit.Encodes;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by ehngjen on 9/2/2015.
 */
public class AccessTokenRealm  extends AuthorizingRealm {
    private static Logger logger = LoggerFactory.getLogger(AccessTokenRealm.class);

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof AccessTokenToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Collection principalCollection = principals.fromRealm(getName());
        if (principalCollection != null && principalCollection.size() > 0) {
            ShiroUser shiroUser = (ShiroUser) principalCollection.iterator().next();
            logger.debug(shiroUser.toString());
            User user = User.dao.findByLoginName(shiroUser.loginName);
            if (user != null) {
                logger.debug(user.toString());
                SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
                info.addRoles(user.getRoleList());
                for (Role role : user.getRoles()) {
                    info.addStringPermissions(role.getPermissionList());
                }
                return info;
            }
        }

        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        AccessTokenToken tokenToken = (AccessTokenToken) authenticationToken;
        String loginName = (String) tokenToken.getPrincipal();
        User user = User.dao.findByLoginName(loginName);
        if (user != null) {
            byte[] salt = Encodes.decodeHex(user.getTokenSalt());
            byte[] hashToken = Digests.sha1(user.getPassword().getBytes(), salt, User.HASH_INTERATIONS);
            return new SimpleAuthenticationInfo(new ShiroUser(user.getId(), user.getLoginName(),user.getName(), user.getTokenExpiredDate()),
                    ByteSource.Util.bytes(hashToken),
                    getName());
        } else {
            return null;
        }
    }
}
