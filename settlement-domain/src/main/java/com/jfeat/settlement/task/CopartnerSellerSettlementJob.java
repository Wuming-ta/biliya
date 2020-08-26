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
import com.jfeat.order.service.OrderService;
import com.jfeat.partner.model.*;
import com.jfeat.partner.model.param.CopartnerParam;
import com.jfeat.partner.service.CopartnerService;
import com.jfeat.partner.service.PhysicalSellerService;
import com.jfeat.settlement.model.OrderItemReward;
import com.jfeat.settlement.service.OwnerBalanceService;
import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jfeat.partner.model.CopartnerSettlement.TRANSFERRED;
import static com.jfeat.partner.model.CopartnerSettlement.UN_TRANSFERRED;

/**
 * Created by jackyhuang on 2017/6/5.
 */
@CronExp(value = "0 0 2 1 * ?")
public class CopartnerSellerSettlementJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(CopartnerSellerSettlementJob.class);
    private static OwnerBalanceService ownerBalanceService = Enhancer.enhance(OwnerBalanceService.class);
    private static final String POINT_EXCHANGE_RATE_KEY = "mall.point_exchange_rate";

    //每月1号凌晨2点, 把上一个月的合伙人提成进行结算
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("========= Copartner Seller Settlement Started =========");
        String month = DateKit.lastMonth("yyyy-MM");
        execute(month);
    }

    /**
     * @param month yyyy-MM
     */
    public void execute(String month) {
        try {
            Config config = Config.dao.findByKey(POINT_EXCHANGE_RATE_KEY);
            int pointExchangeRate = config != null && config.getValueToInt() != null ? config.getValueToInt() : 100;

            OrderService orderService = Enhancer.enhance(OrderService.class);
            CopartnerService copartnerService = Enhancer.enhance(CopartnerService.class);
            copartnerService.handleSettlement(month + "-01");

            //wait for mysql insert data.
            TimeUnit.SECONDS.sleep(5);

            boolean isLastPage = true;
            int pageNumber = 1;
            int pageSize = 50;
            do {
                CopartnerParam param = new CopartnerParam(pageNumber, pageSize);
                param.setStatus(Copartner.Status.NORMAL.toString());
                Page<Copartner> copartnerPage = Copartner.dao.paginate(param);
                isLastPage = copartnerPage.isLastPage();
                pageNumber++;
                copartnerPage.getList().forEach(copartner -> {
                    logger.debug("transfering settlement for copartner {}", copartner.toJson());
                    CopartnerSettlement copartnerSettlement = CopartnerSettlement.dao.findByCond(copartner.getId(), month + "-01");
                    if (copartnerSettlement != null) {
                        BigDecimal transferredAmount = copartnerSettlement.getSettledAmount().multiply(new BigDecimal(pointExchangeRate));
                        copartnerSettlement.setTransferredAmount(transferredAmount);
                        copartnerSettlement.setTransferred(TRANSFERRED);
                        copartnerSettlement.update();
                        logger.debug("CopartnerSettlement updated. {}", copartnerSettlement.toJson());

                        //  添加到积分系统
                        logger.debug("sellerId={}, monthlySettledAmount={}", copartner.getSellerId(), copartnerSettlement.getSettledAmount());
                        User user = copartner.getSeller().getUser();
                        Order order = orderService.createFakeOrder(user.getId());
                        OrderItem orderItem = order.getOrderItems().get(0);
                        OrderItemReward newOrderItemReward = new OrderItemReward();
                        newOrderItemReward.setType(OrderItemReward.Type.COPARTNER.toString());
                        newOrderItemReward.setState(OrderItemReward.State.PENDING_SETTLEMENT.toString());
                        newOrderItemReward.setCreatedTime(new Date());
                        newOrderItemReward.setOrderUserName(user.getName());
                        newOrderItemReward.setPointExchangeRate(pointExchangeRate);
                        newOrderItemReward.setOwnerId(user.getId());
                        newOrderItemReward.setOrderId(orderItem.getOrderId());
                        newOrderItemReward.setOrderItemId(orderItem.getId());

                        newOrderItemReward.setOrderNumber(order.getOrderNumber());
                        newOrderItemReward.setOrderCreatedTime(order.getCreatedDate());
                        newOrderItemReward.setOrderPaidTime(order.getPayDate());
                        newOrderItemReward.setOrderTotalPrice(order.getTotalPrice());

                        newOrderItemReward.setPaymentType(Order.PaymentType.POINT.toString());
                        newOrderItemReward.setOrderProfit(copartnerSettlement.getSettledAmount());
                        newOrderItemReward.setReward(copartnerSettlement.getSettledAmount());
                        logger.debug("saving order item reward: {}", newOrderItemReward.toJson());
                        newOrderItemReward.save();

                        // 结算到余额表
                        if (ownerBalanceService.addReward(user.getId(), newOrderItemReward.getReward())) {
                            newOrderItemReward.setState(OrderItemReward.State.SETTLED.toString());
                            newOrderItemReward.setSettledTime(new Date());
                            newOrderItemReward.update();
                        }
                    }
                });
            } while (!isLastPage);


        } catch (Exception e) {
            logger.error(e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }


}
