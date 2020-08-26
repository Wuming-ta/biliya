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
import com.jfeat.core.BaseService;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderCustomerService;
import com.jfeat.order.service.OrderService;
import com.jfeat.order.service.StoreUtil;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.math.BigDecimal;

/**
 * Created by huangjacky on 16/6/17.
 */
public class OrderCustomerServiceController extends BaseController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    @Before(CurrentUserInterceptor.class)
    @RequiresPermissions("order.edit")
    public void agree() {
        User currentUser = getAttr("currentUser");
        String returnUrl = getPara("returnUrl", "/order");
        OrderCustomerService orderCustomerService = OrderCustomerService.dao.findById(getParaToInt());
        if (orderCustomerService != null) {
            orderService.agreeCustomerService(orderCustomerService, currentUser.getName());
        }
        redirect(returnUrl);
    }

    /**
     * 不同意退货退款, 订单状态回滚到之前的状态
     */
    @Before(CurrentUserInterceptor.class)
    @RequiresPermissions("order.edit")
    public void disagree() {
        User currentUser = getAttr("currentUser");
        String returnUrl = getPara("returnUrl", "/order");
        String content = getPara("content");
        OrderCustomerService orderCustomerService = OrderCustomerService.dao.findById(getParaToInt("id"));
        if (orderCustomerService != null) {
            orderService.disagreeCustomerService(orderCustomerService, currentUser.getName(), content);
        }
        redirect(returnUrl);
    }

    @Before(CurrentUserInterceptor.class)
    @RequiresPermissions("order.edit")
    public void reply() {
        User currentUser = getAttr("currentUser");
        String returnUrl = getPara("returnUrl", "/order");
        String content = getPara("content");
        OrderCustomerService orderCustomerService = OrderCustomerService.dao.findById(getParaToInt("id"));
        if (orderCustomerService != null) {
            orderCustomerService.addLog(currentUser.getName(), content);
            orderService.updateCustomerService(orderCustomerService);
        }
        redirect(returnUrl);
    }

    /**
     * 退货收到确认
     */
    @Before({CurrentUserInterceptor.class, Tx.class})
    @RequiresPermissions("order.edit")
    public void returned() {
        User currentUser = getAttr("currentUser");
        String returnUrl = getPara("returnUrl", "/order");
        Long warehouseId = getParaToLong("warehouseId");
        OrderCustomerService orderCustomerService = OrderCustomerService.dao.findById(getParaToInt("id"));
        if (orderCustomerService == null) {
            redirect(returnUrl);
            return;
        }

        orderService.returnedCustomerService(orderCustomerService, currentUser.getName(), warehouseId);

        redirect(returnUrl);
    }

    public void retaken() {

    }

    /**
     * 更新退款金额
     */
    @Before({CurrentUserInterceptor.class})
    @RequiresPermissions("order.edit")
    public void updateRefundFee() {
        User currentUser = getAttr("currentUser");
        String returnUrl = getPara("returnUrl", "/order");
        String refundFeeStr = getPara("refundFee");
        if (StrKit.isBlank(refundFeeStr)) {
            redirect(returnUrl);
            return;
        }
        BigDecimal refundFee = BigDecimal.valueOf(Double.valueOf(refundFeeStr));
        if (refundFee.compareTo(BigDecimal.ZERO) <= 0) {
            redirect(returnUrl);
            return;
        }
        OrderCustomerService orderCustomerService = OrderCustomerService.dao.findById(getParaToInt("id"));
        if (orderCustomerService != null) {
            orderCustomerService.addLog(currentUser.getName(), getRes().format("customer.service.update_refund_fee", refundFee));
            orderCustomerService.setRefundFee(refundFee);
            Ret ret = orderService.updateCustomerService(orderCustomerService);
            logger.debug("ret = {}", ret.getData());
        }
        redirect(returnUrl);
    }

    /**
     * 真正处理退款, 更新订单状态CLOSED_REFUNDED,售后单状态REFUNDED。
     * 如果已使用其它途径退款, 直接更新订单状态CLOSED_REFUNDED,售后单状态REFUNDED。
     * 退款失败抛出异常进行回滚。
     * 退款完毕
     */
    @RequiresPermissions("order.edit")
    @Before({CurrentUserInterceptor.class, Tx.class})
    public void refunded() {
        String returnUrl = getPara("returnUrl", "/order");
        Boolean otherWay = getParaToBoolean("otherWay", false);
        OrderCustomerService orderCustomerService = OrderCustomerService.dao.findById(getParaToInt());
        if (orderCustomerService == null) {
            logger.debug("order-customer-service {} not found", getParaToInt());
            redirect(returnUrl);
            return;
        }

        logger.debug("now refunding the order {}, otherWay = {}", orderCustomerService.getServiceNumber(), otherWay);

        Ret ret = orderService.refundOrder(orderCustomerService, otherWay);
        logger.info("refund result is {}", ret.getData());

        redirect(returnUrl);
    }

    /**
     * 回退库存
     * @param order
     * @param orderCustomerService
     * @param warehouseId
     */
    private void decreaseRefundProductSales(Order order, OrderCustomerService orderCustomerService, Long warehouseId) {
        User user = order.getUser();
        String note = String.format("用户 %s 订单号 %s 退货单号 %s，回退库存", user.getName(), order.getOrderNumber(), orderCustomerService.getServiceNumber());
        orderService.decreaseRefundProductSales(user.getId().longValue(), user.getLoginName(), orderCustomerService, note, warehouseId);
    }
}
