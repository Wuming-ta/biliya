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

package com.jfeat.settlement.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.settlement.model.RewardCash;
import com.jfinal.aop.Before;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * Created by jingfei on 2016/3/31.
 */
@ControllerBind(controllerKey = "/rest/reward_cash")
public class RewardCashController extends RestController {

    @Override
    @Before(CurrentUserInterceptor.class)
    public void index(){
        String phone = getPara("phone");
        User currentUser = getAttr("currentUser");
        Integer userId = currentUser.getId();
        if (StrKit.notBlank(phone)) {
            User targetUser = User.dao.findByPhone(phone);
            if (targetUser == null) {
                renderFailure("user.not.found");
                return;
            }
            if (ShiroMethod.lacksPermission("SettlementApplication.view")) {
                renderFailure("lack.of.permission");
                return;
            }
            userId = targetUser.getId();
        }
        Integer pageNumber = getParaToInt("page_number", 1);
        Integer pageSize = getParaToInt("page_size", 30);
        String startDate = getPara("start_date", DateKit.currentMonth("yyyy-MM-01")) + " 00:00:00";
        String endDate = getPara("end_date", DateKit.today("yyyy-MM-dd")) + " 23:59:59";
        String status = getPara("status");

        Page<RewardCash> rewardCashPage = RewardCash.dao.queryRewardHistorical(pageNumber, pageSize, startDate, endDate, userId, null, status);
        renderSuccess(rewardCashPage.getList());
    }

}
