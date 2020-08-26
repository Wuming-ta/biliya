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

package com.jfeat.identity.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认角色名服务
 * 上层需要把具体的默认角色provider注入到这个类里面。
 *
 * Created by jacky on 4/23/16.
 */
public class DefaultRole {

    private static Logger logger = LoggerFactory.getLogger(DefaultRole.class);

    private RoleProvider roleProvider = new EmptyRoleProvider();

    private static DefaultRole me = new DefaultRole();

    public static DefaultRole me() {
        return me;
    }

    public void setRoleProvider(RoleProvider roleProvider) {
        logger.info("using role provider: {}", roleProvider.getClass().getName());
        this.roleProvider = roleProvider;
    }

    public RoleProvider getRoleProvider() {
         return roleProvider;
    }
}
