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

package com.jfeat.partner.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.model.User;
import com.jfeat.partner.model.Apply;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.Date;

/**
 * Created by jingfei on 2016/3/31.
 */
@ControllerBind(controllerKey = "/rest/agent")
public class AgentController extends RestController {

    @Before(Tx.class)
    public void save() {
        User currentUser = getAttr("currentUser");
        Integer currentUserId = currentUser.getId();
        Apply apply = Apply.dao.findByUserId(currentUserId);
        if (apply != null && apply.getType().equals(Apply.Type.AGENT.toString())) {
            renderFailure("apply.already.exist");
            return;
        }
        Apply newApply = new Apply();
        newApply.setUserId(currentUserId);
        newApply.setType(Apply.Type.AGENT.toString());
        newApply.setStatus(Apply.Status.INIT.toString());
        newApply.setApplyDate(new Date());
        newApply.save();

        renderSuccessMessage("apply.success");
    }

}
