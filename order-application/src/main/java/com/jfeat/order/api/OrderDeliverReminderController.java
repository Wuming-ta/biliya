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

import com.jfeat.core.RestController;
import com.jfeat.order.service.OrderService;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

/**
 * Created by huangjacky on 16/6/29.
 */
@ControllerBind(controllerKey = "/rest/order_deliver_reminder")
public class OrderDeliverReminderController extends RestController {

    /**
     * GET /rest/order_deliver_reminder/<order-number>
     */
    public void show() {
        String orderNumber = getPara();
        if (StrKit.isBlank(orderNumber)) {
            renderFailure("order.not.found");
            return;
        }

        OrderService orderService = new OrderService();
        orderService.reminderOrderDeliver(orderNumber);
        renderSuccessMessage("order.deliver.reminded");
    }
}
