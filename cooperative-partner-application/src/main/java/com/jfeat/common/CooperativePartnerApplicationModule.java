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
import com.jfeat.partner.api.PhysicalApplyTipsController;
import com.jfeat.partner.interceptor.AllianceInterceptor;
import com.jfinal.config.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CooperativePartnerApplicationModule extends Module {

    private static final Logger logger = LoggerFactory.getLogger(CooperativePartnerApplicationModule.class);

    public CooperativePartnerApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        CooperativePartnerApplicationModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(com.jfeat.partner.controller.CopartnerController.class);
        addController(com.jfeat.partner.controller.SellerController.class);
        addController(com.jfeat.partner.controller.AgentController.class);
        addController(com.jfeat.partner.controller.SettingsController.class);
        addController(com.jfeat.partner.controller.PartnerController.class);
        addController(com.jfeat.partner.controller.SellerApplyController.class);
        addController(com.jfeat.partner.controller.CrownController.class);
        addController(com.jfeat.partner.controller.PhysicalSellerController.class);
        addController(com.jfeat.partner.controller.CooperativeStatisticController.class);
        addController(com.jfeat.partner.controller.AllianceController.class);
        addController(com.jfeat.partner.api.SellerController.class);
        addController(com.jfeat.partner.api.AgentController.class);
        addController(com.jfeat.partner.api.SellerLevelController.class);
        addController(com.jfeat.partner.api.PhysicalSellerController.class);
        addController(com.jfeat.partner.api.PhysicalPurchaseSummaryController.class);
        addController(com.jfeat.partner.sys.api.SellerController.class);
        addController(com.jfeat.partner.api.PhysicalProportionController.class);
        addController(com.jfeat.partner.api.PhysicalPurchaseJournalController.class);
        addController(com.jfeat.partner.api.AgentSummaryController.class);
        addController(com.jfeat.partner.api.PhysicalAgentBonusController.class);
        addController(com.jfeat.partner.api.PhysicalApplyTipsController.class);
        addController(com.jfeat.partner.api.CopartnerController.class);
        addController(com.jfeat.partner.api.CopartnerSettlementController.class);
        addController(com.jfeat.partner.api.AllianceController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);
        new CooperativePartnerDomainModule(jfeatConfig);
        new EventLogDomainModule(jfeatConfig);
    }

    @Override
    public void configInterceptor(Interceptors me) {
        super.configInterceptor(me);
        boolean allianceInterceptorEnabled = getJFeatConfig().getPropertyToBoolean("alliance.api.interceptor.enabled", false);
        if (allianceInterceptorEnabled) {
            String urls = getJFeatConfig().getProperty("alliance.api.interceptor.whitelisturls", "");
            List<String> list = new ArrayList<>();
            for (String url : urls.split(",")) {
                list.add(url.trim());
            }
            logger.debug("whitelisturls: {}. {}", urls, list);
            me.add(new AllianceInterceptor(list));
        }
    }
}
