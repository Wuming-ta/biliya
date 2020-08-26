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
import com.jfeat.ext.plugin.quartz.QuartzPlugin;
import com.jfeat.identity.model.User;
import com.jfeat.job.JobScheduler;
import com.jfeat.member.handler.UserCreatedHandler;
import com.jfeat.member.handler.UserInfollowSubscribeHandler;
import com.jfeat.member.handler.UserUpdatedHandler;
import com.jfeat.member.service.WalletPay;
import com.jfeat.member.service.WalletPayService;
import com.jfeat.member.service.WalletService;
import com.jfeat.member.task.CouponStatisticJob;
import com.jfeat.member.task.OverdueCouponsJob;
import com.jfeat.observer.ObserverKit;
import com.jfeat.payment.PaymentHolder;
import com.jfinal.config.Constants;
import com.jfinal.config.Plugins;

public class MemberDomainModule extends Module {

    private boolean cronEnabled;

    public MemberDomainModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        MemberDomainModelMapping.mapping(this);

        // config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new IdentityDomainModule(jfeatConfig);
        new ConfigDomainModule(jfeatConfig);

        //register observer for user register to create coupon
        ObserverKit.me().registerSync(User.class, User.EVENT_SAVE, UserCreatedHandler.class);
        ObserverKit.me().registerSync(User.class, User.EVENT_UPDATE, UserUpdatedHandler.class);
        ObserverKit.me().register(User.class, User.EVENT_USER_INFOLLOW_SUBSCRIBE, UserInfollowSubscribeHandler.class);

        ServiceContext.me().register(WalletPayService.name, new WalletService());

        PaymentHolder.me().register(WalletPay.PAYMENT_TYPE, new WalletPay());
    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);
        cronEnabled = getJFeatConfig().getPropertyToBoolean("cron.enabled", true);
        if (cronEnabled) {
            JobScheduler.me().addJob(OverdueCouponsJob.class);
            JobScheduler.me().addJob(CouponStatisticJob.class);
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
