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

package com.jfeat.settlement.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.config.model.Config;
import com.jfeat.identity.model.User;
import com.jfeat.job.CronExp;
import com.jfeat.kit.DateKit;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderItem;
import com.jfeat.partner.model.PhysicalPurchaseJournal;
import com.jfeat.partner.model.PhysicalPurchaseSummary;
import com.jfeat.partner.model.PhysicalSeller;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfeat.settlement.model.OrderItemReward;
import com.jfeat.settlement.service.OwnerBalanceService;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 2017/6/5.
 */
@CronExp(value = "0 0 1 1 * ?")
public class PhysicalSellerSettlementJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(PhysicalSellerSettlementJob.class);
    private static OwnerBalanceService ownerBalanceService = Enhancer.enhance(OwnerBalanceService.class);
    private static final String POINT_EXCHANGE_RATE_KEY = "mall.point_exchange_rate";

    //每月1号凌晨1点, 把上一个月的线下提成进行结算
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("========= Physical Seller Settlement Started =========");
        String month = DateKit.lastMonth("yyyy-MM");
        execute(month);
    }

    /**
     * @param month yyyy-MM
     */
    public void execute(String month) {
        try {
            Config config = Config.dao.findByKey(POINT_EXCHANGE_RATE_KEY);
            int pointExchangeRate = 100;
            if (config != null && config.getValueToInt() != null) {
                pointExchangeRate = config.getValueToInt();
            }

            PhysicalSellerService physicalSellerService = Enhancer.enhance(PhysicalSellerService.class);
            //遍历所有线下皇冠（包含线下临时皇冠）
            for (PhysicalSeller crownSeller : PhysicalSeller.dao.findCrownSeller()) {
                //acquirePurchaseSummary方法不仅获取，还保存/更新
                PhysicalPurchaseSummary summary = physicalSellerService.acquirePurchaseSummary(crownSeller, month + "-01", BigDecimal.ZERO);
                if (summary.getTransferred().equals(PhysicalPurchaseSummary.TRANSFERRED)) {
                    logger.debug("physical summary already transferred. ignore it. summary = {}", summary);
                    continue;
                }
                if (summary.getMonthlySettledAmount().compareTo(BigDecimal.ZERO) == 0) {
                    logger.debug("monthlySettledAmount=0, ignore it. summary = {}", summary);
                    continue;
                }

                User user = crownSeller.getSeller().getUser();
                List<PhysicalPurchaseJournal> journals = PhysicalPurchaseJournal.dao.findBySellerIdMonth(crownSeller.getSellerId(), month);
                List<OrderItem> orderItems = Lists.newArrayList();
                Map<Integer, Order> orderMap = Maps.newHashMap();
                for (PhysicalPurchaseJournal journal : journals) {
                    if (journal.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                        logger.debug("journal {} amount <= 0, ignore it", journal.getId());
                        continue;
                    }
                    Order order = Order.dao.findByOrderNumber(journal.getOrderNumber());
                    if (!orderStatusValid(order)) {
                        logger.debug("Invalid order. ignore it. ordernumber={}, order={}", journal.getOrderNumber(), order);
                        continue;
                    }
                    if (orderMap.containsKey(order.getId())) {
                        continue;
                    }
                    orderItems.addAll(order.getOrderItems());
                    orderMap.put(order.getId(), order);
                }
                int totalOrderItemCount = orderItems.size();
                if (totalOrderItemCount == 0) {
                    logger.debug("No order items found for seller sellerid={} - name={}, just ignore it.", crownSeller.getSellerId(), user.getName());
                    continue;
                }

                //  更新汇总表
                summary.setTransferred(PhysicalPurchaseSummary.TRANSFERRED);
                summary.setTransferredAmount(summary.getMonthlySettledAmount().multiply(new BigDecimal(pointExchangeRate)));
                summary.update();

                //  更新皇冠商信息
                crownSeller.setTotalAmount(crownSeller.getTotalAmount().add(summary.getMonthlyAmount()));
                crownSeller.setTotalSettledAmount(crownSeller.getTotalSettledAmount().add(summary.getMonthlySettledAmount()));
                crownSeller.update();

                //  添加到积分系统
                logger.debug("sellerId={}, monthlySettledAmount={}", summary.getSellerId(), summary.getMonthlySettledAmount());
                OrderItem orderItem = orderItems.get(orderItems.size() - 1);
                OrderItemReward newOrderItemReward = new OrderItemReward();
                newOrderItemReward.setType(OrderItemReward.Type.WHOLESALE.toString());
                newOrderItemReward.setState(OrderItemReward.State.PENDING_SETTLEMENT.toString());
                newOrderItemReward.setCreatedTime(new Date());
                newOrderItemReward.setOrderUserName(user.getName());
                newOrderItemReward.setPointExchangeRate(pointExchangeRate);
                newOrderItemReward.setOwnerId(user.getId());
                newOrderItemReward.setOrderId(orderItem.getOrderId());
                newOrderItemReward.setOrderItemId(orderItem.getId());

                Order o = Order.dao.findById(orderItem.getOrderId());
                newOrderItemReward.setOrderNumber(o.getOrderNumber());
                newOrderItemReward.setOrderCreatedTime(o.getCreatedDate());
                newOrderItemReward.setOrderPaidTime(o.getPayDate());
                newOrderItemReward.setOrderTotalPrice(o.getTotalPrice());

                newOrderItemReward.setPaymentType(orderMap.get(orderItem.getOrderId()).getPaymentType());
                newOrderItemReward.setOrderProfit(summary.getMonthlySettledAmount());
                newOrderItemReward.setReward(summary.getMonthlySettledAmount());
                logger.debug("saving order item reward: {}", newOrderItemReward.toJson());
                newOrderItemReward.save();

                // 结算到余额表
                List<OrderItemReward> orderItemRewardList = Lists.newArrayList();
                for (Order order : orderMap.values()) {
                    for (OrderItemReward orderItemReward : OrderItemReward.dao.findByOrderId(order.getId())) {
                        orderItemReward.setState(OrderItemReward.State.SETTLED.toString());
                        orderItemReward.setSettledTime(new Date());
                        Integer userId = orderItemReward.getOwnerId();
                        if (ownerBalanceService.addReward(userId, orderItemReward.getReward())) {
                            orderItemRewardList.add(orderItemReward);
                        }
                    }
                }
                if (!orderItemRewardList.isEmpty()) {
                    Db.batchUpdate(orderItemRewardList, 100);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }

    private boolean orderStatusValid(Order order) {
        if (order == null) {
            return false;
        }
        if (Order.Status.DELIVERING.toString().equals(order.getStatus()) ||
                Order.Status.DELIVERED_CONFIRM_PENDING.toString().equals(order.getStatus()) ||
                Order.Status.CLOSED_CONFIRMED.toString().equals(order.getStatus())) {
            return true;
        }
        return false;
    }

}
