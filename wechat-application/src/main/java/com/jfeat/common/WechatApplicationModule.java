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

import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.order.payment.WechatPay;
import com.jfeat.payment.PaymentHolder;
import com.jfeat.wechat.api.*;
import com.jfeat.wechat.config.WechatConfig;
import com.jfeat.wechat.controller.*;
import com.jfeat.wechat.sys.api.WxaExpressOrderController;
import com.jfeat.wechat.sys.api.WxaExpressOrderTestController;
import com.jfeat.wechat.sys.api.WxaExpressPrinterController;
import com.jfinal.config.Constants;

public class WechatApplicationModule extends Module {

    public WechatApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        WechatApplicationModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(com.jfeat.wechat.controller.WechatMenuController.class);
        addController(com.jfeat.wechat.controller.WechatCertController.class);
        addController(com.jfeat.wechat.controller.WechatTemplateMessageController.class);
        addController(WechatCustomerServiceController.class);
        addController(WechatAutoreplyController.class);
        addController(WechatSubscribeAutoreplyController.class);
        addController(WechatMessageAutoreplyController.class);
        addController(WechatKeywordAutoreplyController.class);

        addController(WechatUnionidMigrationController.class);

        addController(WeixinMsgController.class);
        addController(WxAppLoginController.class);
        addController(WxaLoginController.class);
        addController(WxaRegisterController.class);
        addController(PushOrderController.class);
        addController(PayNotifyController.class);
        addController(WxHostPrefixController.class);
        addController(WxaExpressPathController.class);
        addController(WxaExpressOrderController.class);
        addController(WxaExpressOrderTestController.class);
        addController(WxaExpressPrinterController.class);

        addController(WxLoginController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);


        new WechatDomainModule(jfeatConfig);
        new ConfigApplicationModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);

        PaymentHolder.me().register(WechatPay.PAYMENT_TYPE, new WechatPay());

        addXssExcluded("/wechat_autoreply");


//        //JUST FOR TEST
//        ServiceContext.me().register(new OrderPayService() {
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
//
//            }
//        });
    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);

        WechatConfig.me().setCertUploadPath(getJFeatConfig().getProperty("wechat.cert.upload.path"));
        WechatConfig.me().setInvitationUrlPrefix(getJFeatConfig().getProperty("wechat.wxa.invitation.url.prefix"));
    }
}
