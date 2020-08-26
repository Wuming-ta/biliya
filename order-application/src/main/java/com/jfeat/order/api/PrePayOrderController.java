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

package com.jfeat.order.api;

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.kit.IpKit;

/**
 *
 * Created by jackyhuang on 17/5/6.
 */
@Deprecated
@ControllerBind(controllerKey = "/rest/pre_pay_order")
public class PrePayOrderController extends RestController {

    private static final String NOTIFY_URL = "/order_pay_notify";

    private OrderService orderService = new OrderService();

    /**
     * para: string order_number - 订单号
     */
    @Validation(rules = {
            "order_number = required"
    })
    public void index() {
        String orderNumber = getPara("order_number");
        Order order = Order.dao.findByOrderNumber(orderNumber);
        String notifyUrl = getRequest().getRequestURL().append(NOTIFY_URL).toString();
        Ret ret = orderService.prePayOrder(order, getIp(), notifyUrl);
        logger.debug("ret = {}", ret.getData());
        if (BaseService.isSucceed(ret)) {
            renderSuccess(ret.get("code_url"));
            return;
        }
        renderFailure("prepay.failure");
    }

    private String getIp() {
        String ip = IpKit.getRealIp(this.getRequest());
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
        }
        if(StrKit.isBlank(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
