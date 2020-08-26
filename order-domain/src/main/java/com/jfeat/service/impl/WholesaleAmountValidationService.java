package com.jfeat.service.impl;

import com.jfeat.order.model.Order;
import com.jfeat.service.WholesaleValidationService;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kang on 2017/6/5.
 */
public class WholesaleAmountValidationService implements WholesaleValidationService {

    @Override
    public boolean completed(int userId, Date startTime, Date endTime, BigDecimal target) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        double totalPriceSum = Order.dao.countHistoryTotalPriceSum(userId, "WHOLESALE", dateFormat.format(startTime), dateFormat.format(endTime));
        return totalPriceSum >= target.doubleValue();
    }
}
