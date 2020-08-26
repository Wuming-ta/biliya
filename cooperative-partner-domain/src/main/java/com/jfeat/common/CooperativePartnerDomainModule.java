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
import com.jfeat.ext.plugin.quartz.QuartzPlugin;
import com.jfeat.ext.plugin.redis.RedisSubscriberThread;
import com.jfeat.identity.model.User;
import com.jfeat.identity.subject.AttemptingUpdateInviterSubject;
import com.jfeat.job.JobScheduler;
import com.jfeat.observer.ObserverKit;
import com.jfeat.partner.handler.AllianceShipExpiredHandler;
import com.jfeat.partner.handler.CooperativePartnerExpiredSubscriber;
import com.jfeat.partner.handler.PhysicalCrownShipExpiredHandler;
import com.jfeat.partner.observer.InvitorUpdatedObserver;
import com.jfeat.partner.observer.SellerObserver;
import com.jfeat.partner.observer.UserInfollowSubscribeHandler;
import com.jfeat.partner.task.CooperativeStatisticJob;
import com.jfeat.service.impl.FriendsCountServiceImpl;
import com.jfeat.service.impl.PhysicalCrownAuthorityService;
import com.jfinal.config.Constants;
import com.jfinal.config.Plugins;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;

public class CooperativePartnerDomainModule extends Module {

    private String cacheName;
    private String channel;
    private CooperativePartnerExpiredSubscriber subscriber;
    private boolean cronEnabled;

    public CooperativePartnerDomainModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        CooperativePartnerDomainModelMapping.mapping(this);

        // config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new PcdDomainModule(jfeatConfig);
        new IdentityDomainModule(jfeatConfig);
        new ConfigDomainModule(jfeatConfig);
        new WechatDomainModule(jfeatConfig);

        ObserverKit.me().registerSync(User.class, User.EVENT_SAVE, SellerObserver.class);
        ObserverKit.me().register(User.class,User.EVENT_USER_INFOLLOW_SUBSCRIBE, UserInfollowSubscribeHandler.class);
        ObserverKit.me().registerSync(AttemptingUpdateInviterSubject.class,
                AttemptingUpdateInviterSubject.EVENT_ATTEMPTING_UPDATE,
                InvitorUpdatedObserver.class);

        ServiceContext.me().register(new FriendsCountServiceImpl());
        ServiceContext.me().register(new PhysicalCrownAuthorityService());
    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);
        cronEnabled = getJFeatConfig().getPropertyToBoolean("cron.enabled", true);
        if (cronEnabled) {
            JobScheduler.me().addJob(CooperativeStatisticJob.class);
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
        this.cacheName = getJFeatConfig().getProperty("cooperative.redis.cache.name", "cooperative");
        this.channel = getJFeatConfig().getProperty("cooperative.redis.channel", "__keyevent@0__:expired");
        String host = getJFeatConfig().getProperty("cooperative.redis.host", "localhost");
        Integer port = getJFeatConfig().getPropertyToInt("cooperative.redis.port", 6379);
        RedisPlugin redisPlugin = new RedisPlugin(cacheName, host, port);
        me.add(redisPlugin);
    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        if (cronEnabled) {
            subscriber = new CooperativePartnerExpiredSubscriber(Redis.use(this.cacheName), this.channel);
            new RedisSubscriberThread(subscriber).start();
        }
        PhysicalCrownShipExpiredHandler.init(Redis.use(this.cacheName), cronEnabled);
        AllianceShipExpiredHandler.init(Redis.use(this.cacheName), cronEnabled);
    }

    @Override
    public void beforeJFinalStop() {
        super.beforeJFinalStop();
        if (cronEnabled) {
            subscriber.stop();
        }
    }
}
