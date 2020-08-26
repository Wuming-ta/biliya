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

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.kit.JsonKit;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.Map;

/**
 * Created by jacky on 5/3/16.
 */
@ControllerBind(controllerKey = "/sys/rest/order")
public class OrderController extends RestController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    /**
     * GET /sys/rest/order/<order-number>
     */
    public void show() {
        Order order = Order.dao.findByOrderNumber(getPara());
        if (order == null) {
            renderFailure("order.not.found");
            return;
        }
        renderSuccess(order);
    }

    /**
     * pay order
     * POST /sys/rest/order
     * {
     *     "order_number": "232343243242"
     * }
     */
    @Override
    @Before(Tx.class)
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        String orderNumber = (String) map.get("order_number");
        Order order = Order.dao.findByOrderNumber(orderNumber);
        if (order == null) {
            renderFailure("invalid.order");
            return;
        }

        Ret ret = orderService.payOrder(order);
        logger.debug("Order {} pay result = {}", orderNumber, ret.getData());
        if (BaseService.isSucceed(ret)) {
            renderSuccessMessage(BaseService.getMessage(ret));
        }
        else {
            renderFailure(BaseService.getMessage(ret));
        }
    }

    /**
     * 支付成功后pay_notify会调用这个方法更新订单
     *
     * PUT /sys/rest/order/<order-number>
     *
     * data:
     * {
     *    "status":"NewStatus',
     *    "trade_number": "2432424324324",
     *    "payment_type": "WECHAT"
     * }
     */
    @Override
    @Before(Tx.class)
    public void update() {
        Order order = Order.dao.findByOrderNumber(getPara());
        if (order == null) {
            renderFailure("invalid.order");
            return;
        }
        Map<String, Object> map = convertPostJsonToMap();
        order.setStatus((String) map.get("status"));
        order.setPaymentType((String) map.get("payment_type"));
        order.setTradeNumber((String) map.get("trade_number"));
        orderService.updateOrder(order);
        renderSuccessMessage("order.updated");
    }
}
