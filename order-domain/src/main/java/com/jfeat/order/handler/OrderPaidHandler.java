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

import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.MarketingHolder;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.order.model.Order;
import com.jfeat.order.notify.OrderBillNotifier;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by huangjacky on 16/7/10.
 */
public class OrderPaidHandler implements Observer {

    private static Logger logger = LoggerFactory.getLogger(OrderPaidHandler.class);

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_PAID) {
            Order order = Order.dao.findById(((Order) subject).getId());
            logger.debug("handling paid order {}.", order);

            // marketing order,
            if (StrKit.notBlank(order.getMarketing())) {
                Marketing marketing = MarketingHolder.me().getMarketing(order.getMarketing(),
                        order.getMarketingId(),
                        order.getUserId(),
                        order.getProvince(),
                        order.getCity(),
                        order.getDistrict());
                if (marketing != null && !marketing.shouldChangeOrderStatusAfterPaid()) {
                    logger.debug("marketing {} shouldChangeOrderStatusAfterPaid = false", marketing.getMarketingName());
                    return;
                }
            }

            try {
                //
                OrderBillNotifier.sendPaidOrderNotify(order);

                //direct change status to CONFIRMED_DELIVER_PENDING after paid.
                Order.DeliveryType deliveryType = Order.DeliveryType.valueOf(order.getDeliveryType());
                logger.debug("delivery type = ", order.getDeliveryType());
                //仅对快递方式如此，对于自提或极速送达的方式，因为需要店员在ipad端按受理或拒绝来处理（如果是拒绝，通常是订单关联的store没有货，此时
                //此单的storeId清空，好让平台重新分配此订单的店铺），不能一支付就立刻变成待发货
                if (deliveryType == Order.DeliveryType.EXPRESS) {
                    Order.Status targetStatus = Order.Status.CONFIRMED_DELIVER_PENDING;
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
