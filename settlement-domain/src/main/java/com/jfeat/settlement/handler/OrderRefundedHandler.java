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
import com.jfeat.order.model.OrderCustomerService;
import com.jfeat.order.model.OrderItem;
import com.jfeat.settlement.model.OrderItemReward;
import com.jfinal.plugin.activerecord.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 退货退款达成，相应的拥金也要扣除。
 * Created by jacky on 4/26/16.
 */
public class OrderRefundedHandler implements Observer {

    private static Logger logger = LoggerFactory.getLogger(OrderRefundedHandler.class);

    @Override
    public void invoke(Subject subject, int event, Object o) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_REFUNDED) {
            Order order = Order.dao.findById(((Order) subject).getId());
            logger.debug("handling refunded order {}. update corresponding order-item-reward to status REFUNDED.", order);
            try {
                logger.debug("handling refunded order {}.", order);

                List<OrderItemReward> orderItemRewardList = new ArrayList<>();

                // 对于提供了returns字段的退货单，取相应的数据
                order.getOrderCustomerService().forEach(orderCustomerService -> {
                    if (orderCustomerService.getStatus().equals(OrderCustomerService.Status.REFUNDED.toString())) {
                        orderCustomerService.getReturns().forEach(item -> {
                            OrderItem orderItem = OrderItem.dao.findFirstByOrderIdProductIdProductSpecificationId(orderCustomerService.getOrderId(),
                                    item.getProductId(),
                                    item.getProductSpecificationId());
                            if (orderItem.getStatus().equals(OrderItem.Status.REFUNDED.toString())) {
                                orderItemRewardList.addAll(OrderItemReward.dao.findByField(OrderItemReward.Fields.ORDER_ITEM_ID.toString(), orderItem.getId()));
                            }
                        });
                    }
                });

                // 否则对整个订单的进行修改
                if (orderItemRewardList.isEmpty()) {
                    for (OrderItemReward orderItemReward : OrderItemReward.dao.findByOrderId(order.getId())) {
                        orderItemReward.setState(OrderItemReward.State.REFUNDED.toString());
                        orderItemRewardList.add(orderItemReward);
                    }
                }

                Db.batchUpdate(orderItemRewardList, 100);
            }
            catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                for (StackTraceElement element : e.getStackTrace()) {
                    logger.error("    {}:{} - {}:{}",
                            element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }
        }
    }

}
