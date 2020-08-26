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
import com.jfeat.member.api.model.MemberExtEntity;
import com.jfeat.member.model.MemberExt;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

/**
 * Created by jacky on 4/8/16.
 */
@ControllerBind(controllerKey = "/rest/member")
public class MemberController extends RestController {

    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        MemberExt memberExt = MemberExt.dao.findByUserId(currentUser.getId());
        renderSuccess(memberExt);
    }

    @Before(CurrentUserInterceptor.class)
    public void update() {
        User currentUser = getAttr("currentUser");
        MemberExtEntity memberExtEntity = getPostJson(MemberExtEntity.class);
        MemberExt memberExt = MemberExt.dao.findByUserId(currentUser.getId());
        memberExt.setName(memberExtEntity.getName());
        memberExt.setAddress(memberExtEntity.getAddress());
        memberExt.setBirthday(memberExtEntity.getBirthday());
        memberExt.setDescription(memberExtEntity.getDescription());
        memberExt.setMobile(memberExtEntity.getMobile());
        memberExt.setSex(memberExtEntity.getSex());
        memberExt.update();
        renderSuccessMessage("member.updated");
    }
}
