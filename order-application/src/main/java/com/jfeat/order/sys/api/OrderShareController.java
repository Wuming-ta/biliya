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

package com.jfeat.order.sys.api;

import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponShare;
import com.jfeat.member.model.CouponTakenRecord;
import com.jfeat.member.service.CouponService;
import com.jfeat.member.service.CouponStrategyService;
import com.jfeat.order.model.Order;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;

import java.util.List;
import java.util.Map;

/**
 * 订单支付后分享红包
 * Created by jackyhuang on 16/11/28.
 */
@ControllerBind(controllerKey = "/sys/rest/order_share")
public class OrderShareController extends RestController {

    /**
     * POST /sys/rest/coupon_share
     * {
     *     "order_number": "2343243242",
     *     "user_id": 2
     * }
     *
     * return:
     * {
     *     "status_code": 1,
     *     "data": {
     *         "code": "wrwfafef",
     *         "order_number": "2343243242"
     *     }
     * }
     */
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        String orderNumber = (String) map.get("order_number");
        Integer userId = (Integer) map.get("user_id");
        Order order = Order.dao.findByOrderNumber(orderNumber);
        if (order == null || !order.getUserId().equals(userId)) {
            renderFailure("invalid.order");
            return;
        }

        CouponStrategyService service = new CouponStrategyService();
        CouponShare couponShare = service.generateCouponShare(userId, orderNumber);
        if (couponShare == null) {
            renderFailure("order.share.failure");
            return;
        }

        renderSuccess(couponShare);
    }
}
