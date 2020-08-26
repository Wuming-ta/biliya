package com.jfeat.marketing.handler;

import com.jfeat.marketing.common.model.MarketingConfig;
import com.jfeat.marketing.service.impl.WholesaleMarketing;
import com.jfeat.marketing.trial.model.TrialApplication;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.order.model.Order;
import com.jfeat.partner.handler.PhysicalCrownShipExpiredHandler;
import com.jfeat.partner.model.Seller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by kang on 2017/7/3.
 */
public class OrderDeliveringHandler implements Observer {

    private Logger logger = LoggerFactory.getLogger(OrderDeliveringHandler.class);

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_DELIVERING) {
            Order order = Order.dao.findById(((Order) subject).getId());
            logger.debug("handling delivering order {}.", order);

            try {
                if (WholesaleMarketing.NAME.equals(order.getMarketing()) && order.getMarketingId() != null) {
                    //处理redis，让其60秒后处理 线下皇冠商到期检查事件，这里不用检查订单数额必须达到指定额，因为
                    //之前已经控制了第1单批发数额必须达到指定额
                    Long expiredTime = System.currentTimeMillis() / 1000 + 60;
                    Seller seller = Seller.dao.findByUserId(order.getUserId());
                    String message = PhysicalCrownShipExpiredHandler.buildMessage(seller.getId());
                    String res = PhysicalCrownShipExpiredHandler.CACHE.set(message, seller.getId());
                    logger.debug("Redis set - key:{}, res:{}", message, res);
                    Long result = PhysicalCrownShipExpiredHandler.CACHE.expireAt(message, expiredTime);
                    logger.debug("Redis expired - key:{}, expired:{}, res:{}", message, expiredTime, result);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex.getMessage());
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
                }
            }

            try {
                //如果是试用活动产生的订单
                if (order.getMarketingId() != null && MarketingConfig.Type.TRIAL.toString().equals(order.getMarketing())) {
                    logger.debug("update trial application to delivered.");
                    TrialApplication trialApplication = TrialApplication.dao.findFirstByOrderId(order.getId());
                    trialApplication.setStatus(TrialApplication.Status.DELIVERED.toString());
                    trialApplication.update();
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
