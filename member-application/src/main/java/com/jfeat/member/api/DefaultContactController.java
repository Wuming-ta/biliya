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

package com.jfeat.member.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.Contact;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by huangjacky on 16/6/7.
 */
@ControllerBind(controllerKey = "/rest/default_contact")
public class DefaultContactController extends RestController {
    @Before(CurrentUserInterceptor.class)
    public void index() {
        User user = getAttr("currentUser");
        renderSuccess(Contact.dao.findDefaultByUserId(user.getId()));
    }

    /**
     * PUT /rest/default_contact/id
     * 更新地址为默认地址
     */
    @Before(CurrentUserInterceptor.class)
    public void update() {
        User user = getAttr("currentUser");
        Contact contact = Contact.dao.findById(getParaToInt());
        if (!contact.getUserId().equals(user.getId())) {
            renderFailure("contact.not.found");
            return;
        }
        contact.setIsDefault(Contact.DEFAULT_ADDR);
        contact.update();
        renderSuccessMessage("contact.updated");
    }
}
