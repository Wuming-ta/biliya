/*
 * Copyright (C) 2014-2015 by ehngjen @ www.jfeat.com
 *
 *  The program may be used and/or copied only with the written permission
 *  from JFeat.com, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */
package com.jfeat.common;

import com.jfeat.alipay.api.AlipayNotifyController;
import com.jfeat.alipay.api.AlipayPushOrderController;
import com.jfeat.alipay.config.AlipayKit;
import com.jfeat.alipay.service.AlipayPay;
import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.core.ServiceContext;
import com.jfeat.payment.PaymentHolder;
import com.jfeat.service.PayService;
import com.jfeat.service.exception.RetrieveOrderException;
import com.jfinal.config.Constants;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AlipayApplicationModule extends Module {

    public AlipayApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        AlipayApplicationModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);


        addController(AlipayPushOrderController.class);
        addController(AlipayNotifyController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);


        new ConfigApplicationModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);

        PaymentHolder.me().register(AlipayPay.PAYMENT_TYPE, new AlipayPay());


//        //JUST FOR TEST
//        ServiceContext.me().register("OrderPayService", new PayService() {
//            @Override
//            public Map<String, Object> retrieveToPayOrder(String orderNumber) throws RetrieveOrderException {
//                Map<String, Object> map = new HashMap<>();
//                map.put("description", "DEMO");
//                map.put("total_price", BigDecimal.valueOf(11.10));
//                return map;
//            }
//
//            @Override
//            public void paidNotify(String orderNumber, String paymentType, String tradeNumber, String payAccount) throws RetrieveOrderException {
//
//            }
//        });

    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);
        AlipayKit.DEV = getJFeatConfig().getPropertyToBoolean("alipay.dev", false);
        AlipayKit.DEV_NOTIFY_URL = getJFeatConfig().getProperty("alipay.dev.notifyUrl");
    }
}
