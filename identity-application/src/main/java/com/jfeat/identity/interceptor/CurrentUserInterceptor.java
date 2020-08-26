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
 * Created by jacky on 3/8/16.
 */
public class CurrentUserInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null) {
            ShiroUser shiroUser = (ShiroUser) currentUser.getPrincipal();
            if (shiroUser != null) {
                Integer userId = shiroUser.id;
                User user = User.dao.findById(userId);
                invocation.getController().setAttr("currentUser", user);
            }
        }
        invocation.invoke();
    }
}
