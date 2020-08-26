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

package com.jfeat.settlement.handler;

import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.order.model.Order;
import com.jfeat.settlement.model.OrderItemReward;
import com.jfeat.settlement.service.OwnerBalanceService;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** 把ORDER转为settlement数据。
 * 先把orderid放到MQ里, MQ的消费者从Queue里逐条拿出进行处理。
 * Created by jacky on 3/23/16.
 */
public class OrderClosedHandler implements Observer {

    private static Logger logger = LoggerFactory.getLogger(OrderClosedHandler.class);

    private static OwnerBalanceService ownerBalanceService = Enhancer.enhance(OwnerBalanceService.class);

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_CLOSED) {
            try {
                Integer orderId = ((Order) subject).getId();
                Order order = Order.dao.findById(orderId);
                logger.debug("handling closed order {}.", order);

                List<OrderItemReward> orderItemRewardList = new ArrayList<>();
                for (OrderItemReward orderItemReward : OrderItemReward.dao.findByOrderId(order.getId())) {
                    if (OrderItemReward.State.SETTLED.toString().equals(orderItemReward.getState())) {
                        logger.debug("OrderItemReward [id = {}] already settled.", orderItemReward.getId());
                        continue;
                    }
                    orderItemReward.setState(OrderItemReward.State.SETTLED.toString());
                    orderItemReward.setSettledTime(new Date());
                    Integer userId = orderItemReward.getOwnerId();
                    BigDecimal reward = orderItemReward.getReward();
                    if (ownerBalanceService.addReward(userId, reward)) {
                        orderItemRewardList.add(orderItemReward);
                    }
                }
                if (!orderItemRewardList.isEmpty()) {
                    Db.batchUpdate(orderItemRewardList, 100);
                }
                order.setSettled(Order.SETTLED);
                order.update();
            }
            catch (Exception e) {
                logger.error(e.getMessage());
                for (StackTraceElement element : e.getStackTrace()) {
                    logger.error("    {}:{} - {}:{}",
                            element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }
        }
    }
}
