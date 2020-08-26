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

import com.jfeat.core.Module;
import com.jfeat.core.JFeatConfig;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.PermDataPlugin;
import com.jfeat.ext.plugin.rabbitmq.RabbitMQPlugin;
import com.jfeat.identity.mq.StaffUpdatedNotifier;
import com.jfeat.identity.mq.UserUpdatedNotifier;
import com.jfeat.identity.mq.VipAccountUpdatedHandler;
import com.jfeat.identity.service.PermissionCache;
import com.jfeat.kit.UIDKit;
import com.jfinal.config.Constants;
import com.jfinal.config.Plugins;
import com.jfinal.plugin.redis.RedisPlugin;

public class IdentityDomainModule extends Module {

    private String cacheName;

    public IdentityDomainModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        IdentityDomainModelMapping.mapping(this);

        // config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);

    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);
        UIDKit.setCluster(getJFeatConfig().getPropertyToInt("uid.cluster", 1));

        PermDataPlugin permDataPlugin = new PermDataPlugin(
                getJFeatConfig().getPropertyToBoolean("ext.perm.enabled", false),
                getJFeatConfig().getProperty("ext.perm.api.host", getJFeatConfig().getProperty("ext.api.host")),
                getJFeatConfig().getProperty("ext.perm.jwt.key", getJFeatConfig().getProperty("ext.jwt.key")));
        ExtPluginHolder.me().start(PermDataPlugin.class, permDataPlugin);
    }

    @Override
    public void configPlugin(Plugins me) {
        super.configPlugin(me);
        this.cacheName = getJFeatConfig().getProperty("permission.redis.cache.name", "permission");
        String host = getJFeatConfig().getProperty("permission.redis.host", "localhost");
        Integer port = getJFeatConfig().getPropertyToInt("permission.redis.port", 6379);
        RedisPlugin redisPlugin = new RedisPlugin(this.cacheName, host, port);
        me.add(redisPlugin);


        boolean rabbitmqEnabled = getJFeatConfig().getPropertyToBoolean("staff.rabbitmq.enabled", false);
        if (rabbitmqEnabled) {
            String rabbitmqHost = getJFeatConfig().getProperty("staff.rabbitmq.host", "localhost");
            Integer rabbitmqPort = getJFeatConfig().getPropertyToInt("staff.rabbitmq.port", 5672);
            String rabbitmqUsername = getJFeatConfig().getProperty("staff.rabbitmq.username", "guest");
            String rabbitmqPassword = getJFeatConfig().getProperty("staff.rabbitmq.password", "guest");
            String rabbitmqQueue = getJFeatConfig().getProperty("staff.rabbitmq.queue", "staff-updated-queue");
            RabbitMQPlugin rabbitMQPlugin = new RabbitMQPlugin(rabbitmqHost, rabbitmqPort, rabbitmqUsername, rabbitmqPassword);
            StaffUpdatedNotifier.init(rabbitmqQueue);
            me.add(rabbitMQPlugin);
        }

        boolean userCreatedRabbitmqEnabled = getJFeatConfig().getPropertyToBoolean("user.created.rabbitmq.enabled", false);
        if (userCreatedRabbitmqEnabled) {
            String rabbitmqHost = getJFeatConfig().getProperty("user.created.rabbitmq.host", "localhost");
            Integer rabbitmqPort = getJFeatConfig().getPropertyToInt("user.created.rabbitmq.port", 5672);
            String rabbitmqUsername = getJFeatConfig().getProperty("user.created.rabbitmq.username", "guest");
            String rabbitmqPassword = getJFeatConfig().getProperty("user.created.rabbitmq.password", "guest");
            String rabbitmqQueue = getJFeatConfig().getProperty("user.created.rabbitmq.queue", "user-increasement-queue");
            RabbitMQPlugin rabbitMQPlugin = new RabbitMQPlugin(rabbitmqHost, rabbitmqPort, rabbitmqUsername, rabbitmqPassword);
            UserUpdatedNotifier.init(rabbitmqQueue);
            me.add(rabbitMQPlugin);
        }

        boolean userRabbitmqEnabled = getJFeatConfig().getPropertyToBoolean("user.rabbitmq.enabled", false);
        if (userRabbitmqEnabled) {
            String rabbitmqHost = getJFeatConfig().getProperty("user.rabbitmq.host", "localhost");
            Integer rabbitmqPort = getJFeatConfig().getPropertyToInt("user.rabbitmq.port", 5672);
            String rabbitmqUsername = getJFeatConfig().getProperty("user.rabbitmq.username", "guest");
            String rabbitmqPassword = getJFeatConfig().getProperty("user.rabbitmq.password", "guest");
            RabbitMQPlugin rabbitMQPlugin = new RabbitMQPlugin(rabbitmqHost, rabbitmqPort, rabbitmqUsername, rabbitmqPassword);
            me.add(rabbitMQPlugin);
        }
    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        boolean cacheEnabled = getJFeatConfig().getPropertyToBoolean("permission.redis.enabled", false);
        PermissionCache.me().setCacheName(this.cacheName);
        PermissionCache.me().setEnabled(cacheEnabled);

        boolean userRabbitmqEnabled = getJFeatConfig().getPropertyToBoolean("user.rabbitmq.enabled", false);
        if (userRabbitmqEnabled) {
            String rabbitmqQueue = getJFeatConfig().getProperty("user.rabbitmq.queue", "vip-account-update-queue");
            VipAccountUpdatedHandler.me().setEndpointName(rabbitmqQueue).init();
        }
    }
}
