package com.jfeat.marketing.handler;

import com.jfeat.marketing.piece.model.PieceGroupPurchase;
import com.jfeat.marketing.piece.model.PieceGroupPurchaseMember;
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

import java.util.List;

/**
 * Created by kang on 2017/5/16.
 */
public class OrderRefundedHandler implements Observer {
    private static Logger logger = LoggerFactory.getLogger(OrderPaidHandler.class);

    @Before(Tx.class)
    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_REFUNDED) {
            Order order = (Order) subject;
            logger.debug("handling refunded order {}.", order);
            try {
                PieceGroupPurchaseMember pieceGroupPurchaseMember = PieceGroupPurchaseMember.dao.findFirstByUserIdOrderNumberStatus(
                        order.getUserId(), order.getOrderNumber(), PieceGroupPurchaseMember.Status.PAID.toString());
                if (pieceGroupPurchaseMember != null) {
                    pieceGroupPurchaseMember.setStatus(PieceGroupPurchaseMember.Status.REFUND.toString());
                    pieceGroupPurchaseMember.update();

                    PieceGroupPurchase pieceGroupPurchase = pieceGroupPurchaseMember.getPieceGroupPurchaseMaster().getPieceGroupPurchase();
                    List<OrderItem> orderItemList = order.getOrderItems();
                    int totalCount = 0;
                    for (OrderItem orderItem : orderItemList) {
                        if (pieceGroupPurchase.getProductId().equals(orderItem.getProductId())) {
                            totalCount += orderItem.getQuantity();
                        }
                    }
                    pieceGroupPurchase.decreaseSale(totalCount);
                }

                List<WholesaleMember> wholesaleMemberList = WholesaleMember.dao.findFirstByUserIdOrderNumberStatus(
                        order.getUserId(), order.getOrderNumber(), WholesaleMember.Status.PAID.toString());
                for (WholesaleMember wholesaleMember : wholesaleMemberList) {
                    wholesaleMember.setStatus(WholesaleMember.Status.REFUND.toString());
                    wholesaleMember.update();

                    Wholesale wholesale = wholesaleMember.getWholesale();
                    List<OrderItem> orderItemList = order.getOrderItems();
                    int totalCount = 0;
                    for (OrderItem orderItem : orderItemList) {
                        if (wholesale.getProductId().equals(orderItem.getProductId())) {
                            totalCount += orderItem.getQuantity();
                        }
                    }
                    wholesale.decreaseSale(totalCount);
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
