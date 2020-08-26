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
import com.jfeat.identity.model.User;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderCustomerService;
import com.jfeat.order.model.param.OrderParam;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提供给订单打印软件的api
 * Created by jackyhuang on 16/8/19.
 */
@ControllerBind(controllerKey = "/rest/admin/order")
public class OrderController extends RestController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    /**
     * GET /rest/admin/order?status=CONFIRMED_DELIVER_PENDING&status=DELIVERING
     * &started_date=1313432434&end_date=1334453432424
     * &order_number=32432432432&order_number=23432432432
     * &sku_id=1&warehouse_id=2
     */
    @RequiresPermissions("order.view")
    @Override
    public void index() {
        String[] statuses = getParaValues("status");
        String[] orderNumbers = getParaValues("order_number");
        Long startedDate = getParaToLong("started_date");
        Long endDate = getParaToLong("end_date");
        String skuId = getPara("sku_id");
        String warehouseId = getPara("warehouse_id");
        if (statuses == null || statuses.length == 0) {
            statuses = new String[]{
                    Order.Status.CONFIRMED_DELIVER_PENDING.toString()
            };
        }
        OrderParam param = new OrderParam();
        param.setOrderNumbers(orderNumbers).setStatuses(statuses)
                .setSkuId(skuId).setWarehouseId(warehouseId)
                .setStartTime(convertTime(startedDate))
                .setEndTime(convertTime(endDate))
                .setShowDeleted(true);
        Page<Order> orderPage = Order.dao.paginate(param);
        List<Order> orders = orderPage.getList();
        orders = orders.stream().peek(order -> {
            order.put("order_items", order.getOrderItems());
        }).collect(Collectors.toList());

        renderSuccess(orders);
    }

    private String convertTime(Long date) {
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return simpleDateFormat.format(new Date(date));
        }
        return null;
    }

    /**
     * GET /rest/admin/order/:orderNumber
     */
    @RequiresPermissions("order.view")
    @Override
    public void show() {
        Order order = Order.dao.findByOrderNumber(getPara());
        if (order == null) {
            renderFailure("order.not.found");
            return;
        }
        order.put("commented", StrKit.notBlank(order.getCommentId()));
        order.put("order_items", order.getOrderItems());
        List<OrderCustomerService> orderCustomerServices = order.getOrderCustomerService();
        orderCustomerServices = orderCustomerServices.stream().peek(item -> {
            item.put("images", item.getImagesToList());
            item.put("log", item.getLogToListMap());
            item.put("returns", item.getReturns());
            item.put("exchanges", item.getExchanges());
        }).collect(Collectors.toList());
        order.put("order_customer_services", orderCustomerServices);
        renderSuccess(order);
    }
}
