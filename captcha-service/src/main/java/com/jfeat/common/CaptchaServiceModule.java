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

import com.jfeat.captcha.CaptchaKit;
import com.jfeat.captcha.api.SmsController;
import com.jfeat.captcha.api.SmsVerifyController;
import com.jfeat.captcha.service.CaptchaServiceImpl;
import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.ext.plugin.sms.SmsPlugin;
import com.jfinal.config.Constants;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.RedisPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptchaServiceModule extends Module {

    private static Logger logger = LoggerFactory.getLogger(CaptchaServiceModule.class);

    // shiro plugin need it.
    private Routes routes;
    private String cacheName;

    public CaptchaServiceModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        CaptchaServiceModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(SmsController.class);
        addController(SmsVerifyController.class);


        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);

    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);
    }

    @Override
    public void configRoute(Routes me) {
        super.configRoute(me);
        this.routes = me;
    }

    @Override
    public void configPlugin(Plugins me) {
        super.configPlugin(me);
        String names = getJFeatConfig().getProperty("sms.names", "default");
        for (String name : names.split(",")) {
            name = name.trim();
            String appid = getJFeatConfig().getProperty("sms." + name + ".appid");
            String appkey = getJFeatConfig().getProperty("sms." + name + ".appkey");
            String templateId = getJFeatConfig().getProperty("sms." + name + ".templateId");
            String signName = getJFeatConfig().getProperty("sms." + name + ".signName");
            String vender = getJFeatConfig().getProperty("sms." + name + ".vender");
            String ttl = getJFeatConfig().getProperty("sms." + name + ".ttl");
            SmsConfigKit.setTtl(name, ttl);
            SmsPlugin smsPlugin = new SmsPlugin(name, appid, appkey, templateId, signName, vender);
            me.add(smsPlugin);
        }

        this.cacheName = getJFeatConfig().getProperty("sms.redis.cache.name", "sms");
        String host = getJFeatConfig().getProperty("sms.redis.host", "localhost");
        Integer port = getJFeatConfig().getPropertyToInt("sms.redis.port", 6379);
        RedisPlugin redisPlugin = new RedisPlugin(cacheName, host, port);
        me.add(redisPlugin);
    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        Boolean smsCaptchaEnabled = getJFeatConfig().getPropertyToBoolean("sms.captcha.enabled", false);
        if (smsCaptchaEnabled) {
            CaptchaKit.init(new CaptchaServiceImpl(Redis.use(this.cacheName),
                    getJFeatConfig().getPropertyToLong("sms.redis.cache.expiredSeconds", 60L)));
            CaptchaKit.setEnabled(true);
        }
    }
}
