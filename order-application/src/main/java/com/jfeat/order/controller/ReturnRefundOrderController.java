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
import com.jfeat.identity.model.User;
import com.jfeat.marketing.MarketingHolder;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.param.OrderParam;
import com.jfinal.kit.StrKit;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by huangjacky on 16/7/5.
 */
public class ReturnRefundOrderController extends BaseController {

    @Override
    @RequiresPermissions(value = { "order.view", "return_order.menu" }, logical = Logical.OR)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String type = getPara("type");
        String[] statuses = getParaValues("status");
        String productName = getPara("pName");
        String barcode = getPara("barCode");
        String orderNumber = getPara("orderNumber");
        String contactUser = getPara("contactUser");
        String phone = getPara("phone");
        String uid = getPara("uid");
        Integer userId = null;
        if (StrKit.notBlank(uid)) {
            User user = User.dao.findByUid(uid);
            if (user != null) {
                userId = user.getId();
            } else {
                userId = 0;
            }
        }
        OrderParam orderParam = new OrderParam(pageNumber, pageSize);
        orderParam.setType(type)
                .setProductName(productName)
                .setBarcode(barcode)
                .setPhone(phone)
                .setContactUser(contactUser)
                .setUserId(userId)
                .setOrderNumber(orderNumber)
                .setStatuses(statuses)
                .setQueryReturnRefund(true)
                .setShowDeleted(true);

        setAttr("orders", Order.dao.paginate(orderParam));
        setAttr("nameMap", MarketingHolder.me().getEnabledNameMap());
        keepPara();
    }
}
