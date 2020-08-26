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
import com.jfeat.job.JobScheduler;
import com.jfinal.config.Plugins;
import com.jfinal.plugin.redis.RedisPlugin;

public class MallCronModule extends Module {

    public MallCronModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        MallCronModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);

        new CooperativePartnerDomainModule(jfeatConfig);
        new MemberDomainModule(jfeatConfig);
        new OrderDomainModule(jfeatConfig);
        new SettlementDomainModule(jfeatConfig);
        new MarketingDomainModule(jfeatConfig);
    }

}
