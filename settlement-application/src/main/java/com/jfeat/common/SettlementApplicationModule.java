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
import com.jfeat.settlement.controller.OrderItemRewardController;
import com.jfinal.config.Constants;
import com.jfinal.i18n.I18n;

public class SettlementApplicationModule extends Module {

    public SettlementApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        SettlementApplicationModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(com.jfeat.settlement.controller.OrderItemRewardController.class);
        addController(com.jfeat.settlement.controller.RewardCashController.class);
        addController(com.jfeat.settlement.controller.OwnerBalanceController.class);
        addController(com.jfeat.settlement.controller.WithdrawAccountController.class);
        addController(com.jfeat.settlement.api.OwnerBalanceController.class);
        addController(com.jfeat.settlement.api.RewardCashController.class);
        addController(com.jfeat.settlement.api.OrderItemRewardController.class);
        addController(com.jfeat.settlement.api.WithdrawAccountController.class);
        addController(com.jfeat.settlement.api.ProductSettlementController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new SettlementDomainModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);
        new CooperativePartnerDomainModule(jfeatConfig);
    }
}
