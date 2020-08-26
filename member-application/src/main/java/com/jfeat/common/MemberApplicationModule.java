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
import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.core.ServiceContext;
import com.jfeat.member.controller.CouponStatisticController;
import com.jfeat.member.service.WalletPayService;
import com.jfeat.service.PayService;
import com.jfeat.service.exception.RetrieveOrderException;
import com.jfinal.config.Constants;
import com.jfinal.kit.StrKit;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MemberApplicationModule extends Module {

    public MemberApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        MemberApplicationModelMapping.mapping(this);

        addXssExcluded("/coupon_type");

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(com.jfeat.member.controller.MemberController.class);
        addController(com.jfeat.member.controller.MemberLevelController.class);
        addController(com.jfeat.member.controller.CouponController.class);
        addController(com.jfeat.member.controller.CouponTypeController.class);
        addController(com.jfeat.member.controller.CouponStrategyController.class);
        addController(com.jfeat.member.api.CouponController.class);
        addController(com.jfeat.member.api.MemberLevelController.class);
        addController(com.jfeat.member.api.ContactController.class);
        addController(com.jfeat.member.api.MemberController.class);
        addController(com.jfeat.member.api.DefaultContactController.class);
        addController(com.jfeat.member.api.CouponCalculationController.class);
        addController(com.jfeat.member.api.CouponNotifyController.class);
        addController(com.jfeat.member.api.CouponShareController.class);
        addController(com.jfeat.member.api.WalletController.class);
        addController(com.jfeat.member.api.WalletChargeController.class);
        addController(com.jfeat.member.api.WalletHistoryController.class);
        addController(com.jfeat.member.api.WalletPayController.class);
        addController(com.jfeat.member.api.WalletPasswordController.class);
        addController(com.jfeat.member.api.WalletVerifyPasswordController.class);
        addController(com.jfeat.member.api.admin.ConponController.class);
        addController(com.jfeat.member.api.admin.ConponTypeController.class);
        addController(com.jfeat.member.api.admin.WalletPayController.class);
        addController(com.jfeat.member.sys.api.CouponController.class);
        addController(CouponStatisticController.class);


        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new MemberDomainModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);
        new ProductApplicationModule(jfeatConfig);



//        //JUST FOR TEST
//        ServiceContext.me().register("OrderPayService", new PayService() {
//            @Override
//            public Map<String, Object> retrieveToPayOrder(String orderNumber) throws RetrieveOrderException {
//                Map<String, Object> map = new HashMap<>();
//                map.put("description", "DEMO");
//                map.put("total_price", new BigDecimal(11));
//                return map;
//            }
//
//            @Override
//            public void paidNotify(String orderNumber, String paymentType, String tradeNumber) throws RetrieveOrderException {
//                System.out.println(orderNumber);
//                System.out.println(paymentType);
//                System.out.println(tradeNumber);
//            }
//        });


    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);
        Boolean smsCaptchaEnabled = getJFeatConfig().getPropertyToBoolean("sms.captcha.enabled", false);
        if (smsCaptchaEnabled) {
            CaptchaKit.init(new CaptchaServiceDummyImpl());
        }

        String strategyTypeExcludes = getJFeatConfig().getProperty("coupon.strategy.type.excludes");
        if (StrKit.notBlank(strategyTypeExcludes)) {
            String[] excludes = strategyTypeExcludes.split(",");
            Arrays.stream(excludes).forEach(exclude -> CouponConfigHolder.me().addExcludedStrategyType(exclude.trim()));
        }
        String templateTypeExcludes = getJFeatConfig().getProperty("coupon.template.type.excludes");
        if (StrKit.notBlank(templateTypeExcludes)) {
            String[] excludes = templateTypeExcludes.split(",");
            Arrays.stream(excludes).forEach(exclude -> CouponConfigHolder.me().addExcludedTemplateType(exclude.trim()));
        }
    }
}
