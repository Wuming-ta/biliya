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

package com.jfeat.order.handler;

import com.jfeat.config.model.Config;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jackyhuang on 2018/1/15.
 */
public class OrderDeliverPendingHandler implements Observer {

    private static Logger logger = LoggerFactory.getLogger(OrderDeliverPendingHandler.class);
    private static final String AUTO_DELIVER_ORDER_KEY = "mall.auto_deliver_order";

    private OrderService orderService = Enhancer.enhance(OrderService.class);


    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_DELIVER_PENDING) {
            Order order = Order.dao.findById(((Order) subject).getId());
            logger.debug("handling deliver pending order {}.", order);

            try {
                Config config = Config.dao.findByKey(AUTO_DELIVER_ORDER_KEY);
                if (config != null && config.getValueToBoolean()) {
                    logger.debug("auto deliver order, going to change status to DELIVERING, orderNumber = {}", order.getOrderNumber());
                    Order.Status targetStatus = Order.Status.DELIVERING;
                    order.setStatus(targetStatus.toString());
                    Ret ret = orderService.updateOrder(order);
                    logger.debug("Order {}: change status to {}, result = {}", order.getOrderNumber(), targetStatus, ret.getData());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex.getMessage());
                logger.error(ex.toString());
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
                }
            }
        }
    }
}
