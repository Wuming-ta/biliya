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

package com.jfeat.order.api.admin;

import com.jfeat.core.RestController;
import com.jfeat.order.model.Express;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Map;

/**
 * Created by jackyhuang on 16/8/19.
 */
@ControllerBind(controllerKey = "/rest/admin/deliver")
public class DeliverController extends RestController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    /**
     * 批量更新订单发货信息
     * POST /rest/admin/deliver
     * [
     *  {
     *      "order_number": "23432432432",
     *      "express_id": 2,
     *      "express_number": "23423432432"
     *  }
     * ]
     */
    @RequiresPermissions("order.view")
    public void save() {
        Map<String, Object>[] maps = convertPostJsonToMapArray();
        if (maps == null) {
            renderError(400);
            return;
        }
        for (Map<String, Object> map : maps) {
            String orderNumber = (String) map.get("order_number");
            Integer expressId = (Integer) map.get("express_id");
            String expressNumber = (String) map.get("express_number");
            Order order = Order.dao.findByOrderNumber(orderNumber);
            Express express = Express.dao.findById(expressId);
            if (order != null &&
                    express != null &&
                    expressNumber != null &&
                    (order.getStatus().equals(Order.Status.CONFIRMED_DELIVER_PENDING.toString()) || order.getStatus().equals(Order.Status.DELIVERING.toString()))) {
                order.setExpressCode(express.getCode());
                order.setExpressCompany(express.getName());
                order.setExpressNumber(expressNumber);
                order.setStatus(Order.Status.DELIVERING.toString());
                Ret ret = orderService.updateOrder(order);
                logger.debug("result: {}", ret.getData());
            }
        }

        renderSuccessMessage("order.delivered");
    }
}
