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

package com.jfeat.order.task;

import com.jfeat.job.CronExp;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 每天定时运行，对已过了确认收货时间期限的订单进行自动确认。对超出最迟退货时间的订单进行自动结算。
 * 确认后，如果最迟退货时间没设，则系统会马上自动结算拥金订单项。
 *
 * Created by jacky on 4/28/16.
 */
@CronExp(value = "0 0 0/1 * * ?")
public class AutoConfirmOrderJob implements Job {
    private static Logger logger = LoggerFactory.getLogger(AutoConfirmOrderJob.class);

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("=========start to confirm order==========");

        List<Order> orders = orderService.queryConfirmTimeoutOrders();
        for (Order order : orders) {
            order.setStatus(Order.Status.CLOSED_CONFIRMED.toString());
            Ret ret = orderService.updateOrder(order);
            logger.debug("Ret = {}", ret);
        }

        List<Order> returnTimeoutOrders = orderService.queryReturnTimeoutOrders();
        for (Order order : returnTimeoutOrders) {
            order.closedOrderNotify();
        }

        logger.info("=========end of confirm order==========");
    }
}
