/*
 * Copyright (C) 2014-2017 www.kequandian.net
 *
 *  The program may be used and/or copied only with the written permission
 *  from kequandian.net, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */
package com.jfeat.common;

import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.ext.plugin.redis.RedisSubscriberThread;
import com.jfeat.marketing.MarketingHolder;
import com.jfeat.marketing.handler.*;
import com.jfeat.marketing.service.impl.PieceGroupJointMarketing;
import com.jfeat.marketing.service.impl.PieceGroupMarketing;
import com.jfeat.marketing.service.impl.TrialMarketing;
import com.jfeat.marketing.service.impl.WholesaleMarketing;
import com.jfeat.marketing.trial.model.Trial;
import com.jfeat.observer.ObserverKit;
import com.jfeat.order.model.Order;
import com.jfinal.config.Constants;
import com.jfinal.config.Plugins;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;

public class MarketingDomainModule extends Module {

    private String cacheName;
    private String channel;
    private MarketingExpiredSubscriber subscriber;
    private boolean cronEnabled;

    public MarketingDomainModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        MarketingDomainModelMapping.mapping(this);

        // config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);

        new IdentityDomainModule(jfeatConfig);
        new ProductDomainModule(jfeatConfig);
        new OrderDomainModule(jfeatConfig);
        new MiscDomainModule(jfeatConfig);
        new MemberDomainModule(jfeatConfig);

        ObserverKit.me().register(Order.class, Order.EVENT_ORDER_PAID, OrderPaidHandler.class);
        ObserverKit.me().register(Order.class, Order.EVENT_ORDER_REFUNDED, OrderRefundedHandler.class);
        ObserverKit.me().register(Order.class, Order.EVENT_ORDER_DELIVERING, OrderDeliveringHandler.class);
    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);

        cronEnabled = getJFeatConfig().getPropertyToBoolean("cron.enabled", true);

        MarketingHolder.me().register(PieceGroupMarketing.NAME, PieceGroupMarketing.DISPLAY_NAME, PieceGroupMarketing.class);
        MarketingHolder.me().register(PieceGroupJointMarketing.NAME, PieceGroupJointMarketing.DISPLAY_NAME, PieceGroupJointMarketing.class);
        MarketingHolder.me().register(WholesaleMarketing.NAME, WholesaleMarketing.DISPLAY_NAME, WholesaleMarketing.class);
        MarketingHolder.me().register(TrialMarketing.NAME, TrialMarketing.DISPLAY_NAME, TrialMarketing.class);
    }

    @Override
    public void configPlugin(Plugins me) {
        super.configPlugin(me);

        this.cacheName = getJFeatConfig().getProperty("marketing.redis.cache.name", "marketing");
        this.channel = getJFeatConfig().getProperty("marketing.redis.channel", "__keyevent@0__:expired");
        String host = getJFeatConfig().getProperty("marketing.redis.host", "localhost");
        Integer port = getJFeatConfig().getPropertyToInt("marketing.redis.port", 6379);
        RedisPlugin redisPlugin = new RedisPlugin(cacheName, host, port);
        me.add(redisPlugin);
    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();

        if (cronEnabled) {
            subscriber = new MarketingExpiredSubscriber(Redis.use(this.cacheName), this.channel);
            new RedisSubscriberThread(subscriber).start();
        }
        PieceGroupExpiredHandler.init(Redis.use(this.cacheName), cronEnabled);
    }

    @Override
    public void beforeJFinalStop() {
        super.beforeJFinalStop();

        if (cronEnabled) {
            subscriber.stop();
        }
    }
}
