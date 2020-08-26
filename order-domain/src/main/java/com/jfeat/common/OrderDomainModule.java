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
import com.jfeat.core.ServiceContext;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.StorePlugin;
import com.jfeat.ext.plugin.VipPlugin;
import com.jfeat.ext.plugin.WmsPlugin;
import com.jfeat.ext.plugin.quartz.QuartzPlugin;
import com.jfeat.ext.plugin.rabbitmq.RabbitMQPlugin;
import com.jfeat.ext.plugin.redis.RedisSubscriberThread;
import com.jfeat.job.JobScheduler;
import com.jfeat.observer.ObserverKit;
import com.jfeat.order.handler.*;
import com.jfeat.order.model.Order;
import com.jfeat.order.notify.OrderBillNotifier;
import com.jfeat.order.payment.DummyPayment;
import com.jfeat.order.service.OrderPayService;
import com.jfeat.order.service.OrderService;
import com.jfeat.order.task.AutoConfirmOrderJob;
import com.jfeat.order.task.CleanupShoppingCartJob;
import com.jfeat.order.task.StatisticOrderJob;
import com.jfeat.payment.PaymentHolder;
import com.jfeat.product.model.Product;
import com.jfeat.service.impl.HistoryBuyCountServiceImpl;
import com.jfeat.service.impl.WholesaleAmountValidationService;
import com.jfinal.config.Constants;
import com.jfinal.config.Plugins;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;

public class OrderDomainModule extends Module {

    private boolean cronEnabled;
    private String cacheName;
    private String channel;
    private OrderSubscriber subscriber;

    public OrderDomainModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        OrderDomainModelMapping.mapping(this);

        // config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new IdentityDomainModule(jfeatConfig);
        new ProductDomainModule(jfeatConfig);
        new ConfigDomainModule(jfeatConfig);
        new EventLogDomainModule(jfeatConfig);
        new MemberDomainModule(jfeatConfig);
        new WechatDomainModule(jfeatConfig);
        new ConfigDomainModule(jfeatConfig);

        ObserverKit.me().registerSync(Order.class, Order.EVENT_ORDER_PAID, OrderPaidHandler.class);
        ObserverKit.me().registerSync(Order.class, Order.EVENT_ORDER_DELIVER_PENDING, OrderDeliverPendingHandler.class);
        ObserverKit.me().registerSync(Order.class, Order.EVENT_ORDER_DELIVERING, OrderDeliveringHandler.class);
        ObserverKit.me().register(Order.class, Order.EVENT_ORDER_CLOSED, OrderClosedHandler.class);

        ObserverKit.me().registerSync(Product.class, Product.EVENT_PRICE_UPDATE, ProductUpdatedHandler.class);
        ObserverKit.me().registerSync(Product.class, Product.EVENT_COVER_UPDATE, ProductUpdatedHandler.class);

        ServiceContext.me().register(new HistoryBuyCountServiceImpl());
        ServiceContext.me().register(new WholesaleAmountValidationService());
        ServiceContext.me().register(OrderPayService.name, new OrderService());

        PaymentHolder.me().register(DummyPayment.PAYMENT_TYPE, new DummyPayment());
    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);

        cronEnabled = getJFeatConfig().getPropertyToBoolean("cron.enabled", true);
        if (cronEnabled) {
            JobScheduler.me().addJob(AutoConfirmOrderJob.class);
            JobScheduler.me().addJob(StatisticOrderJob.class);
            JobScheduler.me().addJob(CleanupShoppingCartJob.class);
        }

        VipPlugin vipPlugin = new VipPlugin(
                getJFeatConfig().getPropertyToBoolean("ext.vip.enabled", false),
                getJFeatConfig().getProperty("ext.vip.api.host", getJFeatConfig().getProperty("ext.api.host")),
                getJFeatConfig().getProperty("ext.vip.jwt.key", getJFeatConfig().getProperty("ext.jwt.key")));
        ExtPluginHolder.me().start(VipPlugin.class, vipPlugin);

        StorePlugin storePlugin = new StorePlugin(
                getJFeatConfig().getPropertyToBoolean("ext.store.enabled", false),
                getJFeatConfig().getProperty("ext.store.api.host", getJFeatConfig().getProperty("ext.api.host")),
                getJFeatConfig().getProperty("ext.store.jwt.key", getJFeatConfig().getProperty("ext.jwt.key")));
        ExtPluginHolder.me().start(StorePlugin.class, storePlugin);

        WmsPlugin wmsPlugin = new WmsPlugin(
                getJFeatConfig().getPropertyToBoolean("ext.wms.enabled", false),
                getJFeatConfig().getProperty("ext.wms.api.host", getJFeatConfig().getProperty("ext.api.host")),
                getJFeatConfig().getProperty("ext.wms.jwt.key", getJFeatConfig().getProperty("ext.jwt.key")));
        ExtPluginHolder.me().start(WmsPlugin.class, wmsPlugin);
    }

    @Override
    public void configPlugin(Plugins me) {
        super.configPlugin(me);

        if (cronEnabled && !exists(me, QuartzPlugin.class)) {
            QuartzPlugin quartzPlugin = new QuartzPlugin();
            me.add(quartzPlugin);
            JobScheduler.me().registerPlugin(quartzPlugin);
        }

        this.cacheName = getJFeatConfig().getProperty("order.redis.cache.name", "order");
        this.channel = getJFeatConfig().getProperty("order.redis.channel", "__keyevent@0__:expired");
        String host = getJFeatConfig().getProperty("order.redis.host", "localhost");
        Integer port = getJFeatConfig().getPropertyToInt("order.redis.port", 6379);
        RedisPlugin redisPlugin = new RedisPlugin(cacheName, host, port);
        me.add(redisPlugin);

        boolean rabbitmqEnabled = getJFeatConfig().getPropertyToBoolean("order.rabbitmq.enabled", false);
        if (rabbitmqEnabled) {
            String rabbitmqHost = getJFeatConfig().getProperty("order.rabbitmq.host", "localhost");
            Integer rabbitmqPort = getJFeatConfig().getPropertyToInt("order.rabbitmq.port", 5672);
            String rabbitmqUsername = getJFeatConfig().getProperty("order.rabbitmq.username", "guest");
            String rabbitmqPassword = getJFeatConfig().getProperty("order.rabbitmq.password", "guest");
            String rabbitmqQueue = getJFeatConfig().getProperty("order.rabbitmq.queue", "order-bill-queue");
            RabbitMQPlugin rabbitMQPlugin = new RabbitMQPlugin(rabbitmqHost, rabbitmqPort, rabbitmqUsername, rabbitmqPassword);
            OrderBillNotifier.init(rabbitmqQueue);
            me.add(rabbitMQPlugin);
        }
    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();

        if (cronEnabled) {
            subscriber = new OrderSubscriber(Redis.use(this.cacheName), this.channel);
            new RedisSubscriberThread(subscriber).start();
        }

        OrderExpiredHandler.init(Redis.use(this.cacheName), cronEnabled);
    }

    @Override
    public void beforeJFinalStop() {
        super.beforeJFinalStop();
        if (cronEnabled) {
            subscriber.stop();
        }
    }

}
