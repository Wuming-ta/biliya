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
import com.jfeat.kit.DateKit;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderStatistic;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jackyhuang on 16/9/3.
 */
public class StatisticService extends BaseService {

    /**
     * 统计窗口时间内 已确认 订单的总金额
     * @param fromDate
     * @param toDate
     */
    public void orderStatistic(String fromDate, String toDate) {
        BigDecimal totalPrice = Order.dao.statisticTotalPriceFromDealtOrder(fromDate, toDate);
        OrderStatistic orderStatistic = new OrderStatistic();
        orderStatistic.setSalesAmount(totalPrice);
        orderStatistic.save();
    }

    public List<OrderStatistic> queryOrderStatistic(String fromDate, String toDate) {
        return OrderStatistic.dao.queryStatistic(fromDate, toDate);
    }
}
