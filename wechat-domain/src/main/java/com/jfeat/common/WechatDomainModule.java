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

import com.jfeat.config.model.Config;
import com.jfeat.core.Module;
import com.jfeat.core.JFeatConfig;
import com.jfeat.observer.ObserverKit;
import com.jfeat.wechat.config.WxConfig;
import com.jfeat.wechat.observer.WechatConfigUpdatedObserver;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.wxaapp.WxaConfigKit;

public class WechatDomainModule extends Module {

    public WechatDomainModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        WechatDomainModelMapping.mapping(this);

        // config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);

        ObserverKit.me().register(Config.class, Config.EVENT_UPDATE, WechatConfigUpdatedObserver.class);

    }

    @Override
    public void afterJFinalStart() {
        /**
         * 多个公众号时，重复调用ApiConfigKit.putApiConfig(ac)依次添加即可，第一个添加的是默认。
         */
        if (WxConfig.hasValues()) {
            ApiConfigKit.putApiConfig(WxConfig.getApiConfig());
        }

        WxaConfigKit.setWxaConfig(WxConfig.getWxaConfig());
    }
}
