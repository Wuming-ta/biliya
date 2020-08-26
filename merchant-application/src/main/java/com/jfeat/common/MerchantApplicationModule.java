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

import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.merchant.api.MerchantOrderController;
import com.jfeat.merchant.api.MerchantProfileController;
import com.jfeat.merchant.controller.*;
import com.jfeat.merchant.sys.api.MerchantController;

public class MerchantApplicationModule extends Module {

    public MerchantApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        MerchantApplicationModelMapping.mapping(this);
        addXssExcluded("/config");

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(SettledMerchantController.class);
        addController(MerchantConfigController.class);
        addController(SettledMerchantInfoController.class);
        addController(MerchantApplyController.class);

        addController(MerchantController.class);
        addController(MerchantOrderController.class);
        addController(MerchantProfileController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);
        new ConfigApplicationModule(jfeatConfig);
        new MerchantDomainModule(jfeatConfig);
    }

}
