package com.jfeat.order.handler;

import com.jfeat.member.service.MemberService;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.param.OrderParam;
import com.jfeat.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 订单完成，更新到会员信息系统。
 * @author jackyhuang
 * @date 2018/10/24
 */
public class OrderClosedHandler implements Observer {

    private static Logger logger = LoggerFactory.getLogger(OrderClosedHandler.class);

    private MemberService memberService = new MemberService();
    private OrderService orderService = new OrderService();

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_CLOSED) {
            Order order = (Order) subject;
            updateMemberConsumeInfo(order);
            updateVipAccount(order);
        }
    }

    private void updateVipAccount(Order order) {
        if (!order.getUserId().toString().equals(order.getStoreUserId())) {
            String account = order.getUser().getLoginName();
            orderService.updateCreditConsume(account, order.getTotalPrice());
            orderService.updatePointConsume(account, order.getTotalPrice());
        }
    }

    private void updateMemberConsumeInfo(Order order) {
        OrderParam orderParam = new OrderParam();
        orderParam.setUserId(order.getUserId());
        orderParam.setStatuses(new String[] {
                Order.Status.CLOSED_CONFIRMED.toString()
        });
        Double consumeAmount = Order.dao.queryOrderTotalPrice(orderParam);
        Long consumeCount = Order.dao.countOrderByCond(orderParam);
        Date lastConsumeTime = new Date();
        logger.debug("update memberExt userId={} consume info consumeAmount={},consumeCount={},lastConsumeTime={}",
                order.getUserId(), consumeAmount, consumeCount, lastConsumeTime);
        memberService.updateConsumeInfo(order.getUserId(), consumeAmount.intValue(), consumeCount.intValue(), lastConsumeTime);
    }
}
