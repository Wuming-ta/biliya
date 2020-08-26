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
import com.jfeat.member.model.MemberLevel;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by ehngjen on 1/19/2016.
 */
@ControllerBind(controllerKey = "/rest/member_level")
public class MemberLevelController extends RestController {

    public void index() {
        renderSuccess(MemberLevel.dao.findAll());
    }
}
