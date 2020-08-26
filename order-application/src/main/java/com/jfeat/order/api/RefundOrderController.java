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
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderCustomerService;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;

import java.util.List;

/**
 * Created by jackyhuang on 16/12/14.
 */
@ControllerBind(controllerKey = "/rest/refund_order")
public class RefundOrderController extends RestController {

    @Before(CurrentUserInterceptor.class)
    @Override
    public void index() {
        User currentUser = getAttr("currentUser");
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        List<Order> orders = Order.dao.paginateReturnRefundOrder(pageNumber, pageSize, Order.Type.ORDER.toString(), currentUser.getId()).getList();
        for (Order order : orders) {
            List<OrderCustomerService> orderCustomerServices = order.getOrderCustomerService();
            order.put("order_customer_service", orderCustomerServices.isEmpty() ? null : orderCustomerServices.get(0));
            order.put("order_customer_services", orderCustomerServices);
        }
        renderSuccess(orders);
    }
}
