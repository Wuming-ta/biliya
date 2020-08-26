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

import com.jfeat.ext.plugin.zbus.handler.TMsgHandler;
import com.jfeat.ext.plugin.zbus.sender.MqSender;
import com.jfeat.ext.plugin.zbus.sender.Sender;
import com.jfeat.identity.model.User;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfeat.merchant.model.SettledMerchantSettlementProportion;
import com.jfeat.merchant.model.UserSettledMerchant;
import com.jfeat.merchant.service.SettledMerchantService;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderItem;
import com.jfeat.partner.model.*;
import com.jfeat.partner.service.AgentService;
import com.jfeat.partner.service.SellerService;
import com.jfeat.product.model.ProductSettlementProportion;
import com.jfeat.settlement.model.OrderItemReward;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单支付后，马上就进行分销结算，但这时还不能提现的。
 * Created by jacky on 4/26/16.
 */
public class OrderPaidHandler implements Observer {

    private static Logger logger = LoggerFactory.getLogger(OrderPaidHandler.class);

    private static SellerService sellerService = new SellerService();
    private static AgentService agentService = new AgentService();

    @Override
    public void invoke(Subject subject, int event, Object o) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_PAID) {
            Order order = Order.dao.findById(((Order) subject).getId());
            try {
                logger.debug("handling paid order {}.", order);
                if (StrKit.isBlank(order.getMarketing())
                        || !order.getMarketing().equals(Constants.MARKETING_WHOLESALE)) {
                    handleCooperativePartner(order);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                for (StackTraceElement element : e.getStackTrace()) {
                    logger.error("    {}:{} - {}:{}",
                            element.getFileName(), element.getLineNumber(), element.getClassName(), element.getMethodName());
                }
            }

        }
    }

    private void handleCooperativePartner(Order order) {
        Seller orderSeller = Seller.dao.findByUserId(order.getUserId());

        //partner 合伙人（星级经销商）
        Seller partner = orderSeller.getPartner();
        if (partner != null && partner.isPartnerShip()) {
            PartnerLevel partnerLevel = PartnerLevel.dao.findById(partner.getPartnerLevelId());
            handleInternal(partner.getUserId(),
                    order,
                    SettlementProportion.dao.findByPartner(partnerLevel.getLevel()),
                    OrderItemReward.Type.PARTNER,
                    partnerLevel.getLevel());
        }

        //seller （普通分销商）
        //如果分销商自己参与分成,那么他就拿走一级分销分成,他的父节点拿走二级,
        //如果分销商自己不参与分成,那么他的父节点拿走一级分销分成, 其他类推。
        int maxLevel = MerchantOptions.dao.getDefault().getMaxLevel();
        if (orderSeller.isSellerShip()) {
            Seller theSeller = orderSeller;
            for (int i = 1; i <= maxLevel; i++) {
                if (theSeller != null && theSeller.isSellerShip()) {
                    handleInternal(theSeller.getUserId(),
                            order,
                            SettlementProportion.dao.findBySeller(i),
                            OrderItemReward.Type.SELLER,
                            i);

                }
                theSeller = sellerService.queryParentSeller(orderSeller, i);
            }
        } else {
            for (int i = 1; i <= maxLevel; i++) {
                Seller theSeller = sellerService.queryParentSeller(orderSeller, i);
                if (theSeller != null && theSeller.isSellerShip()) {
                    handleInternal(theSeller.getUserId(),
                            order,
                            SettlementProportion.dao.findBySeller(i),
                            OrderItemReward.Type.SELLER,
                            i);
                }
            }
        }


        //agent
        Agent provinceAgent = agentService.queryProvinceAgent(order.getProvince());
        SettlementProportion provinceSettlementProportion = SettlementProportion.dao.findByAgent(Agent.PROVINCE_AGENT);
        if (provinceAgent != null && provinceSettlementProportion.getProportionObject().getValue() > 0) {
            handleInternal(provinceAgent.getUserId(), order, provinceSettlementProportion, OrderItemReward.Type.AGENT, 1);
        }
        Agent cityAgent = agentService.queryCityAgent(order.getCity());
        SettlementProportion citySettlementProportion = SettlementProportion.dao.findByAgent(Agent.CITY_AGENT);
        if (cityAgent != null && citySettlementProportion.getProportionObject().getValue() > 0) {
            handleInternal(cityAgent.getUserId(), order, citySettlementProportion, OrderItemReward.Type.AGENT, 2);
        }
        Agent districtAgent = agentService.queryDistrictAgent(order.getDistrict());
        SettlementProportion districtSettlementProportion = SettlementProportion.dao.findByAgent(Agent.DISTRICT_AGENT);
        if (districtAgent != null && districtSettlementProportion.getProportionObject().getValue() > 0) {
            handleInternal(districtAgent.getUserId(), order, districtSettlementProportion, OrderItemReward.Type.AGENT, 3);
        }

        //platform
        Seller platformSeller = Seller.dao.findPlatformSeller();
        SettlementProportion platformSettlementProportion = SettlementProportion.dao.findByPlatform();
        if (platformSeller != null && platformSettlementProportion.getProportionObject().getValue() > 0) {
            handleInternal(platformSeller.getUserId(), order, platformSettlementProportion, OrderItemReward.Type.PLATFORM, null);
        }

        //crown
        Seller crownSeller = orderSeller.getCrown();
        if (crownSeller != null && crownSeller.isCrownShip()) {
            handleInternal(crownSeller.getUserId(),
                    order,
                    SettlementProportion.dao.findByCrown(),
                    OrderItemReward.Type.CROWN,
                    null);
        }


        //merchant
        if (order.getMid() != null) {
            handleMerchant(order);
        }
    }

    private void handleMerchant(Order order) {
        List<UserSettledMerchant> userSettledMerchants = UserSettledMerchant.dao.findByMerchantId(order.getMid());
        SettledMerchantSettlementProportion settledMerchantSettlementProportion = SettledMerchantSettlementProportion.dao.getDefault();
        if (userSettledMerchants != null && userSettledMerchants.size() > 0) {
            User user = order.getUser();
            Integer ownerId = userSettledMerchants.get(0).getUserId();
            BigDecimal reward = order.getTotalPrice()
                    .multiply(settledMerchantSettlementProportion.getPercentage())
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP)
                    .divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);

            OrderItem orderItem = order.getOrderItems().get(0);
            OrderItemReward orderItemReward = new OrderItemReward();
            orderItemReward.setOrderId(order.getId());
            orderItemReward.setPaymentType(order.getPaymentType());
            orderItemReward.setPointExchangeRate(order.getPointExchangeRate());
            orderItemReward.setOrderItemId(orderItem.getId());
            orderItemReward.setOwnerId(ownerId);
            orderItemReward.setType(OrderItemReward.Type.MERCHANT.toString());
            orderItemReward.setLevel(null);
            orderItemReward.setOrderUserName(user.getName());
            orderItemReward.setOrderNumber(order.getOrderNumber());
            orderItemReward.setOrderCreatedTime(order.getCreatedDate());
            orderItemReward.setOrderPaidTime(order.getPayDate());
            orderItemReward.setOrderTotalPrice(order.getTotalPrice());
            orderItemReward.setPercent(settledMerchantSettlementProportion.getPercentage().intValue());
            orderItemReward.setOrderProfit(order.getTotalPrice());
            orderItemReward.setReward(reward);

            if (reward.compareTo(new BigDecimal(0)) > 0) {
                orderItemReward.save();
                logger.debug("handleMerchant reward saved. {}", orderItemReward);
            } else {
                logger.debug("handleMerchant reward is 0, don't save. {}", orderItemReward);
            }
        }
    }


    private void handleInternal(Integer userId, Order order, SettlementProportion settlementProportion, OrderItemReward.Type type, Integer level) {
        logger.debug("handling userId {}", userId);
        User user = order.getUser();
        for (OrderItem orderItem : order.getOrderItems()) {
            SettlementProportion.Proportion proportion = settlementProportion.getProportionObject();
            logger.debug("proportion={}", proportion.toString());

            BigDecimal productProfit = orderItem.getPrice().subtract(orderItem.getCostPrice());
            if (proportion.isPercentage() && SettlementProportion.SettlementType.SALES_AMOUNT.toString().equals(proportion.getSettlementtype())) {
                productProfit = orderItem.getPrice();
            }
            OrderItemReward orderItemReward = new OrderItemReward();
            orderItemReward.setOrderId(order.getId());
            orderItemReward.setPaymentType(order.getPaymentType());
            orderItemReward.setPointExchangeRate(order.getPointExchangeRate());
            orderItemReward.setOrderItemId(orderItem.getId());
            orderItemReward.setOwnerId(userId);
            orderItemReward.setType(type.toString());
            orderItemReward.setLevel(level);
            orderItemReward.setOrderUserName(user.getName());
            orderItemReward.setOrderNumber(order.getOrderNumber());
            orderItemReward.setOrderCreatedTime(order.getCreatedDate());
            orderItemReward.setOrderPaidTime(order.getPayDate());
            orderItemReward.setOrderTotalPrice(order.getTotalPrice());

            BigDecimal reward = new BigDecimal(0);

            Integer levelForProduct = level == null ? 0 : level; //皇冠商传进来的level是null,product_settlement_proportion中皇冠商对应的level是0
            // CROWN/PARTNER/SELLER 其中如果是SELLER（只有PARTNER与CROWN才使用为每个产品分别设置的根据等级划分的分成比例），则下面查找productSettlementProportion必为null
            String typeForProduct = type.toString();
            ProductSettlementProportion productSettlementProportion =
                    ProductSettlementProportion.dao.findFirstByProductIdTypeLevel(orderItem.getProductId(), typeForProduct, levelForProduct);
            Double settlementForProduct = productSettlementProportion == null ? null : productSettlementProportion.getProportionObject().getValue(); //第2个结果依然可能为null
            logger.debug("settlementForProduct = {}", settlementForProduct);
            if (settlementForProduct == null || settlementForProduct.doubleValue() <= 0.0) {
                if (proportion.isPercentage()) {
                    orderItemReward.setPercent(proportion.getValue().intValue());
                    //单品的利润 * 分成比率 * 数量
                    //最后除以1目的是为了保留2位小数。
                    reward = productProfit.multiply(BigDecimal.valueOf(proportion.getValue() * 1.0 / 100))
                            .divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP)
                            .multiply(new BigDecimal(orderItem.getQuantity()))
                            .divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                } else {
                    if (orderItem.getPartnerLevelZone() != null) {
                        Double value = proportion.getValue(orderItem.getPartnerLevelZone());
                        if (value != null) {
                            reward = BigDecimal.valueOf(value).multiply(new BigDecimal(orderItem.getQuantity())).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                        }
                    }
                }
            } else {
                reward = BigDecimal.valueOf(settlementForProduct).multiply(new BigDecimal(orderItem.getQuantity())).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
            }
            BigDecimal profit = productProfit.multiply(new BigDecimal(orderItem.getQuantity())).divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
            orderItemReward.setOrderProfit(profit);
            orderItemReward.setReward(reward);

            if (reward.compareTo(new BigDecimal(0)) > 0) {
                orderItemReward.save();
                logger.debug("reward saved. {}", orderItemReward);
            } else {
                logger.debug("reward is 0, don't save. {}", orderItemReward);
            }
        }
    }
}
