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

package com.jfeat.order.controller;

import com.jfeat.core.BaseController;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderItem;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Before;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.RequiresRoles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 测试用
 * Created by jacky on 3/3/16.
 */
@RequiresRoles("admin")
public class OrderTestController extends BaseController {

    private OrderService service = new OrderService();

    public void create() {
        Integer userId = getParaToInt("userId");
        Integer[] productIds = getParaValuesToInt("productId");
        Order order = new Order();
        order.setUserId(userId);
        order.setDeliveryType(Order.DeliveryType.EXPRESS.toString());
        List<OrderItem> orderItems = new ArrayList<>();
        for (Integer productId : productIds) {
            OrderItem item = new OrderItem();
            item.setProductId(productId);
            item.setQuantity(1);
            orderItems.add(item);
        }
        order.setOrderItems(orderItems);
        try {
            service.createOrder(order, null, false,0, false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        renderText("ok");
    }

    @Before(Tx.class)
    public void pay() {
        Order order = Order.dao.findByOrderNumber(getPara());
        Order.Status status = Order.Status.valueOf(order.getStatus());
        Date payDate = order.getPayDate();
        if (status != Order.Status.PAID_CONFIRM_PENDING && payDate == null) {
            order.setStatus(Order.Status.PAID_CONFIRM_PENDING.toString());
            order.setTradeNumber("test_trade_num");
            order.setPaymentType("WECHAT");
            service.updateOrder(order);
        }
        renderText("ok");
    }

    @Before(Tx.class)
    public void paynotify() {
        String orderNumber = getPara("orderNumber");
        String openid = getPara("openid");
        service.paidNotify(orderNumber, "WECHAT","test_trade_num", openid);
        renderText("ok");
    }

    public void confirm() {
        Order order = Order.dao.findByOrderNumber(getPara());
        order.setStatus(Order.Status.CLOSED_CONFIRMED.toString());
        Ret ret = service.updateOrder(order);
        renderJson(ret);
    }
}
