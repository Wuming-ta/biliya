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
import com.jfeat.marketing.piece.api.PieceGroupPurchaseController;

public class MarketingApplicationModule extends Module {

    public MarketingApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        MarketingApplicationModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(PieceGroupPurchaseController.class);
        addController(com.jfeat.marketing.piece.controller.PieceGroupPurchaseController.class);
        addController(com.jfeat.marketing.wholesale.controller.WholesaleController.class);
        addController(com.jfeat.marketing.wholesale.controller.WholesaleCategoryController.class);
        addController(com.jfeat.marketing.common.controller.RegionSelectController.class);
        addController(com.jfeat.marketing.common.controller.ProductSelectController.class);
        addController(com.jfeat.marketing.trial.controller.TrialController.class);
        addController(com.jfeat.marketing.trial.controller.TrialApplicationController.class);


        addController(com.jfeat.marketing.piece.api.MyPieceGroupPurchaseController.class);
        addController(com.jfeat.marketing.piece.api.PieceGroupPurchaseController.class);
        addController(com.jfeat.marketing.piece.api.PieceGroupPurchaseMasterController.class);
        addController(com.jfeat.marketing.wholesale.api.WholesaleController.class);
        addController(com.jfeat.marketing.wholesale.api.WholesaleCategoryController.class);
        addController(com.jfeat.marketing.trial.api.TrialController.class);
        addController(com.jfeat.marketing.trial.api.TrialApplicationController.class);

        addController(com.jfeat.marketing.piece.sys.api.PieceGroupPurchaseController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new PcdDomainModule(jfeatConfig);
        new MarketingDomainModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);
        new ProductApplicationModule(jfeatConfig);
        new OrderApplicationModule(jfeatConfig);
    }

}
