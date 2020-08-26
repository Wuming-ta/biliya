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

package com.jfeat.service.impl;

import com.jfeat.kit.DateKit;
import com.jfeat.order.model.Order;
import com.jfeat.service.HistoryBuyCountService;

/**
 * 查询某用户过去N天内购买过某产品的数量
 * Created by jackyhuang on 16/10/14.
 */
public class HistoryBuyCountServiceImpl implements HistoryBuyCountService {
    @Override
    public long getHistoryBuyCount(int productId, int userId, int lastDays) {
        String startTime = null;
        String endTime = null;
        if (lastDays > 0) {
            startTime = DateKit.daysAgoStr(lastDays, "yyyy-MM-dd 00:00:00");
            endTime = DateKit.today("yyyy-MM-dd 23:59:59");
        }
        return Order.dao.countHistoryBuyProduct(productId, userId, startTime, endTime);
    }
}
