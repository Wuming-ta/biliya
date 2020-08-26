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

package com.jfeat.merchant.api;

import com.jfeat.config.model.Config;
import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfeat.merchant.model.UserSettledMerchant;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;

import java.util.List;

/**
 * Created by jackyhuang on 2018/1/16.
 */
@ControllerBind(controllerKey = "/rest/merchant/profile")
public class MerchantProfileController extends RestController {

    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        List<UserSettledMerchant> userSettledMerchantList = UserSettledMerchant.dao.findByUserId(currentUser.getId());
        if (userSettledMerchantList.size() == 0) {
            renderFailure("merchant.not.found");
            return;
        }

        SettledMerchant settledMerchant = userSettledMerchantList.get(0).getSettledMerchant();
        String url = "/app?mid=" + settledMerchant.getId() + "&invite_code=" + currentUser.getInvitationCode();
        Config config = Config.dao.findByKey("wx.host");
        if (config != null) {
            url = config.getValue() + url;
        }
        settledMerchant.put("url", url);

        renderSuccess(settledMerchant);
    }
}
