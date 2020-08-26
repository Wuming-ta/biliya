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

package com.jfeat.identity.interceptor;

import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.identity.model.User;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Created by ehngjen on 1/18/2016.
 */
public class RefreshShiroUserInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        inv.invoke();
        Subject currentUser = SecurityUtils.getSubject();
        ShiroUser shiroUser  = (ShiroUser) currentUser.getPrincipal();
        User user = User.dao.findById(shiroUser.id);
        shiroUser.setName(user.getName());
        shiroUser.setAvatar(user.getAvatar());
    }
}
