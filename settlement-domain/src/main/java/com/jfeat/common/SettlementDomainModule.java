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
package com.jfeat.common;

import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.ext.plugin.quartz.QuartzPlugin;
import com.jfeat.identity.model.User;
import com.jfeat.job.JobScheduler;
import com.jfeat.observer.ObserverKit;
import com.jfeat.order.model.Order;
import com.jfeat.payment.PaymentHolder;
import com.jfeat.settlement.handler.OrderClosedHandler;
import com.jfeat.settlement.handler.OrderDeliveringHandler;
import com.jfeat.settlement.handler.OrderPaidHandler;
import com.jfeat.settlement.handler.OrderRefundedHandler;
import com.jfeat.settlement.observer.WithdrawAccountObserver;
import com.jfeat.settlement.payment.PointPay;
import com.jfeat.settlement.task.CopartnerSellerSettlementJob;
import com.jfeat.settlement.task.PhysicalAgentBonusJob;
import com.jfeat.settlement.task.PhysicalAgentSettlementJob;
import com.jfeat.settlement.task.PhysicalSellerSettlementJob;
import com.jfinal.config.Constants;
import com.jfinal.config.Plugins;

public class SettlementDomainModule extends Module {

    private boolean cronEnabled;

    public SettlementDomainModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        SettlementDomainModelMapping.mapping(this);

        // config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new OrderDomainModule(jfeatConfig);
        new ConfigDomainModule(jfeatConfig);
        new CooperativePartnerDomainModule(jfeatConfig);
        new EventLogDomainModule(jfeatConfig);
        new MerchantDomainModule(jfeatConfig);

        ObserverKit.me().register(Order.class, Order.EVENT_ORDER_CLOSED, OrderClosedHandler.class);
        ObserverKit.me().register(Order.class, Order.EVENT_ORDER_PAID, OrderPaidHandler.class);
        ObserverKit.me().register(Order.class, Order.EVENT_ORDER_REFUNDED, OrderRefundedHandler.class);
        ObserverKit.me().register(Order.class, Order.EVENT_ORDER_DELIVERING, OrderDeliveringHandler.class);
        ObserverKit.me().registerSync(User.class, User.EVENT_SAVE, WithdrawAccountObserver.class);
        ObserverKit.me().registerSync(User.class, User.EVENT_UPDATE, WithdrawAccountObserver.class);

        PaymentHolder.me().register(PointPay.PAYMENT_TYPE, new PointPay());
    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);

        cronEnabled = getJFeatConfig().getPropertyToBoolean("cron.enabled", true);
        if (cronEnabled) {
            JobScheduler.me().addJob(CopartnerSellerSettlementJob.class);
            JobScheduler.me().addJob(PhysicalSellerSettlementJob.class);
            JobScheduler.me().addJob(PhysicalAgentSettlementJob.class);
            JobScheduler.me().addJob(PhysicalAgentBonusJob.class);
        }
    }

    @Override
    public void configPlugin(Plugins me) {
        super.configPlugin(me);

        if (cronEnabled && !exists(me, QuartzPlugin.class)) {
            QuartzPlugin quartzPlugin = new QuartzPlugin();
            me.add(quartzPlugin);
            JobScheduler.me().registerPlugin(quartzPlugin);
        }
    }
}
