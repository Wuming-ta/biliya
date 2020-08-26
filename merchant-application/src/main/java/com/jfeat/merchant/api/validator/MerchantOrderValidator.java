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

package com.jfeat.merchant.api.validator;

import com.jfeat.core.RestController;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.merchant.model.UserSettledMerchant;
import com.jfeat.order.model.Order;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.util.List;

/**
 * Created by jackyhuang on 2018/1/15.
 */
public class MerchantOrderValidator extends Validator {

    @Override
    protected void validate(Controller controller) {
        setShortCircuit(true);
        RestController restController = (RestController) controller;

        Subject currentUser = SecurityUtils.getSubject();
        Integer userId = ((ShiroUser) currentUser.getPrincipal()).id;

        List<UserSettledMerchant> userSettledMerchantList = UserSettledMerchant.dao.findByUserId(userId);
        if (userSettledMerchantList.size() == 0) {
            addError("error", "merchant.not.found");
        }
        Integer mid = userSettledMerchantList.get(0).getMerchantId();

        if (getActionKey().equals("/rest/merchant/order/update")) {
            Order order = Order.dao.findByOrderNumber(controller.getPara());
            if (order == null || order.getMid() == null || !mid.equals(order.getMid())) {
                addError("error", "非法订单");
            }
            restController.setAttr("order", order);
        }
    }

    @Override
    protected void handleError(Controller controller) {
        RestController restController = (RestController) controller;
        restController.renderFailure(controller.getAttr("error"));
    }
}
