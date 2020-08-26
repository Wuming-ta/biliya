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
import com.jfeat.mall.api.SysApiConfigHolder;
import com.jfeat.mall.config.WxConfig;
import com.jfeat.mall.interceptor.HomeInterceptor;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class MallWechatModule extends Module {

    private static final Logger logger = LoggerFactory.getLogger(MallWechatModule.class);

    public MallWechatModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        MallWechatModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(com.jfeat.mall.controller.WPayController.class);
        addController(com.jfeat.mall.controller.PPayController.class);
        addController(com.jfeat.mall.controller.AppController.class);

    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);

        ApiConfigKit.setDevMode(me.getDevMode());
        SysApiConfigHolder.me().setHost(getJFeatConfig().getProperty("sys.api.host"));
        SysApiConfigHolder.me().setUsername(getJFeatConfig().getProperty("sys.api.username"));
        SysApiConfigHolder.me().setPassword(getJFeatConfig().getProperty("sys.api.password"));
    }

    @Override
    public void configInterceptor(Interceptors me) {
        super.configInterceptor(me);
        me.add(new HomeInterceptor(
                        getJFeatConfig().getProperty("mall.name"),
                        getJFeatConfig().getProperty("mall.url.weuiCss"),
                        getJFeatConfig().getProperty("mall.url.angularJs"),
                        getJFeatConfig().getProperty("mall.url.angularUiRouterJs"),
                        getJFeatConfig().getProperty("mall.url.ocLazyLoadJs"),
                        getJFeatConfig().getProperty("extension")
                )
        );
    }
}
