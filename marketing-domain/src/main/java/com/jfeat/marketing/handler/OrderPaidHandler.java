package com.jfeat.marketing.handler;


import com.jfeat.marketing.common.model.MarketingConfig;
import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;
import com.jfeat.marketing.trial.model.TrialApplication;
import com.jfeat.marketing.wholesale.model.Wholesale;
import com.jfeat.marketing.wholesale.model.WholesaleMember;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderItem;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by kang on 2017/5/15.
 */
public class OrderPaidHandler implements Observer {

    private static Logger logger = LoggerFactory.getLogger(OrderPaidHandler.class);

    @Before(Tx.class)
    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_PAID) {
            Order order = Order.dao.findById(((Order) subject).getId());
            logger.debug("handling paid order {}.", order);

            try {
                PieceGroupPurchaseMember pieceGroupPurchaseMember = PieceGroupPurchaseMember.dao.findFirstByUserIdOrderNumberStatus(
                        order.getUserId(), order.getOrderNumber(), PieceGroupPurchaseMember.Status.UNPAID.toString());
                if (pieceGroupPurchaseMember != null) {
                    pieceGroupPurchaseMember.setStatus(PieceGroupPurchaseMember.Status.PAID.toString());
                    pieceGroupPurchaseMember.update();

                    PieceGroupPurchase pieceGroupPurchase = pieceGroupPurchaseMember.getPieceGroupPurchaseMaster().getPieceGroupPurchase();
                    List<OrderItem> orderItemList = order.getOrderItems();
                    int totalCount = 0;
                    for (OrderItem orderItem : orderItemList) {
                        if (pieceGroupPurchase.getProductId().equals(orderItem.getProductId())) {
                            totalCount += orderItem.getQuantity();
                        }
                    }
                    pieceGroupPurchase.increaseSale(totalCount);
                }

                List<WholesaleMember> wholesaleMemberList = WholesaleMember.dao.findFirstByUserIdOrderNumberStatus(
                        order.getUserId(), order.getOrderNumber(), WholesaleMember.Status.UNPAID.toString());
                for (WholesaleMember wholesaleMember : wholesaleMemberList) {
                    wholesaleMember.setStatus(WholesaleMember.Status.PAID.toString());
                    wholesaleMember.update();

                    Wholesale wholesale = wholesaleMember.getWholesale();
                    List<OrderItem> orderItemList = order.getOrderItems();
                    int totalCount = 0;
                    for (OrderItem orderItem : orderItemList) {
                        if (wholesale.getProductId().equals(orderItem.getProductId())) {
                            totalCount += orderItem.getQuantity();
                        }
                    }
                    wholesale.increaseSale(totalCount);
                }

                //如果是试用活动产生的订单
                if (order.getMarketingId() != null && MarketingConfig.Type.TRIAL.toString().equals(order.getMarketing())) {
                    //若是要支付一定金额的试用订单（注意：试用一般是免费的，这里只是保留另一种可能）
                    if(order.getTotalPrice().compareTo(BigDecimal.valueOf(0)) > 0) {
                        TrialApplication trialApplication = TrialApplication.dao.findFirstByOrderId(order.getId());
                        trialApplication.setStatus(TrialApplication.Status.AUDITING.toString());
                        trialApplication.update();
                    }
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
