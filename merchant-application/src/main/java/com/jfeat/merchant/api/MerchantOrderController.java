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

package com.jfeat.merchant.api;

import com.jfeat.core.BaseService;
import com.jfeat.core.Pagination;
import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.merchant.api.validator.MerchantOrderValidator;
import com.jfeat.merchant.model.UserSettledMerchant;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 2017/12/27.
 */
@ControllerBind(controllerKey = "/rest/merchant/order")
public class MerchantOrderController extends RestController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String status = getPara("status");
        List<UserSettledMerchant> userSettledMerchantList = UserSettledMerchant.dao.findByUserId(currentUser.getId());
        if (userSettledMerchantList.isEmpty()) {
            renderFailure("merchant.not.found");
            return;
        }
        Pagination<Order> pagination = Order.dao.createPagination()
                .setPageNumber(pageNumber).setPageSize(pageSize)
                .addParam("mid", userSettledMerchantList.get(0).getMerchantId());
        if (StrKit.notBlank(status)) {
            pagination.addParam("status", status);
        }
        pagination.orderBy("id", true);
        renderSuccess(pagination.doPaginate().getList());
    }

    /**
     * PUT {"status": "CLOSED_CONFIRMED"}
     */
    @Override
    @Before({MerchantOrderValidator.class})
    public void update() {
        Map<String, Object> map = convertPostJsonToMap();
        Order order = getAttr("order");
        order.setStatus((String) map.get("status"));
        Ret ret = orderService.updateOrder(order);

        if (BaseService.isSucceed(ret)) {
            logger.debug("Merchant order updated. order={}", order);
            renderSuccessMessage("order.updated");
        } else {
            renderFailure("order.status.transfer.error");
        }
    }

    /**
     * GET /rest/merchant/order/<order_number>
     * <p/>
     * Return:
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void show() {
        User currentUser = getAttr("currentUser");
        List<UserSettledMerchant> userSettledMerchantList = UserSettledMerchant.dao.findByUserId(currentUser.getId());
        if (userSettledMerchantList.isEmpty()) {
            renderFailure("merchant.not.found");
            return;
        }
        Order order = Order.dao.findByOrderNumber(getPara());
        Integer mid = userSettledMerchantList.get(0).getMerchantId();
        if (order != null && mid.equals(order.getMid())) {
            order.put("order_items", order.getOrderItems());
            order.put("order_customer_services", order.getOrderCustomerService());
            renderSuccess(order);
        } else {
            renderFailure("invalid.order.id");
        }
    }
}
