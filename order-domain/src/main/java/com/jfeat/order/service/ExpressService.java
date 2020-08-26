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

package com.jfeat.order.service;

import com.jfeat.core.BaseService;
import com.jfeat.order.model.Express;
import com.jfeat.order.model.Order;
import com.jfinal.kit.Ret;

/**
 * Created by jackyhuang on 16/9/29.
 */
public abstract class ExpressService extends BaseService {

    /**
     * 根据订单号去查询物流信息
     * @param orderNumber
     * @return
     */
    public Ret queryExpress(String orderNumber) {
        Order order = Order.dao.findByOrderNumber(orderNumber);
        if (order == null) {
            return failure("invalid.order");
        }

        ExpressInfo expressInfo = queryExpress(order.getExpressCode(), order.getExpressNumber());
        if (expressInfo.isSucceed()) {
            return success("OK", expressInfo);
        }

        return failure();
    }

    protected static String getExpressCompany(String comCode) {
        Express express = Express.dao.findByCode(comCode);
        return express != null ? express.getName() : comCode;
    }

    public abstract ExpressInfo queryExpress(String com, String num);
}
