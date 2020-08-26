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

import com.jfeat.config.model.Config;
import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.CouponShare;
import com.jfeat.member.service.CouponStrategyService;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.List;

/**
 * Created by jackyhuang on 16/12/1.
 */
@ControllerBind(controllerKey = "/rest/coupon_share")
public class CouponShareController extends RestController {

    private CouponStrategyService strategyService = new CouponStrategyService();

    /**
     * Get my coupon share
     *
     * return :
     {
     "status_code": 0,
     "data": [
     {
     "valid_date": "2016-12-06 12:18:43",
     "code": "e58a9055-f966-4b59-b36d-b8de66bbfc25",
     "user_id": 1,
     "order_number": "12345",
     "share_date": "2016-12-01 12:18:43",
     "link": "http://www.kequandian.net/app/app/coupon?share_code=e58a9055-f966-4b59-b36d-b8de66bbfc25&invite_code=a1b2c3",
     "id": 1
     }
     ]
     }

     */
    @Before(CurrentUserInterceptor.class)
    public void index() {
        User user = getAttr("currentUser");
        Config config = Config.dao.findByKey("wx.host");
        String host = null;
        if (config != null && StrKit.notBlank(config.getValueToStr())) {
            host = config.getValueToStr();
        }
        List<CouponShare> couponShareList = strategyService.findCouponShare(user.getId());
        for (CouponShare couponShare : couponShareList) {
            if (host != null) {
                StringBuilder builder = new StringBuilder();
                builder.append(host);
                builder.append("/app/coupon?share_code=");
                builder.append(couponShare.getCode());
                builder.append("&invite_code=");
                builder.append(user.getInvitationCode());
                couponShare.put("link", builder.toString());
            }
        }
        renderSuccess(couponShareList);
    }
}
