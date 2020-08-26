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

package com.jfeat.identity.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.filter.ServletUtils;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * Created by ehngjen on 9/2/2015.
 */
@ControllerBind(controllerKey = "/rest/logout")
public class LogoutController extends RestController {

    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        if (currentUser != null) {
            currentUser.resetTokenSalt();
            currentUser.setPassword("");
            currentUser.update();
        }
        renderSuccessMessage("logout.success");
    }
}
