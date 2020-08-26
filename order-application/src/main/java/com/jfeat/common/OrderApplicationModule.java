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

public class OrderApplicationModule extends Module {

    public OrderApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        OrderApplicationModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(com.jfeat.order.controller.OrderController.class);
        addController(com.jfeat.order.controller.StoreOrderController.class);
        addController(com.jfeat.order.controller.PrintPreviewController.class);
        addController(com.jfeat.order.controller.ExpressController.class);
        addController(com.jfeat.order.controller.OrderCustomerServiceController.class);
        addController(com.jfeat.order.controller.ReturnRefundOrderController.class);
        addController(com.jfeat.order.controller.OrderStatisticController.class);
        addController(com.jfeat.order.api.OrderCountController.class);
        addController(com.jfeat.order.api.OrderCommentController.class);
        addController(com.jfeat.order.api.OrderController.class);
        addController(com.jfeat.order.api.PrePayOrderController.class);
        addController(com.jfeat.order.api.OrderPayNotifyController.class);
        addController(com.jfeat.order.api.ShoppingCartController.class);
        addController(com.jfeat.order.api.ExpressInfoController.class);
        addController(com.jfeat.order.api.OrderCustomerServiceController.class);
        addController(com.jfeat.order.api.OrderDeliverReminderController.class);
        addController(com.jfeat.order.api.DefaultExpressController.class);
        addController(com.jfeat.order.api.RefundOrderController.class);
        addController(com.jfeat.order.api.ProductCarriageController.class);
        addController(com.jfeat.order.api.ExpressController.class);
        addController(com.jfeat.order.api.OrderExpressController.class);
        addController(com.jfeat.order.api.admin.ShoppingCartController.class);
        addController(com.jfeat.order.api.admin.OrderCountController.class);
        addController(com.jfeat.order.api.admin.OrderController.class);
        addController(com.jfeat.order.api.admin.ExpressController.class);
        addController(com.jfeat.order.api.admin.DeliverController.class);
        addController(com.jfeat.order.api.admin.DeliveredController.class);
        addController(com.jfeat.order.sys.api.OrderController.class);
        addController(com.jfeat.order.sys.api.OrderShareController.class);
        addController(com.jfeat.order.sys.api.OrderDeliverController.class);
        addController(com.jfeat.order.sys.api.ExpressController.class);
        addController(com.jfeat.order.api.store.OrderController.class);
        addController(com.jfeat.order.api.store.OrderCountController.class);
        addController(com.jfeat.order.api.store.OrderCustomerServiceController.class);

        addController(com.jfeat.order.controller.OrderTestController.class);

        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new OrderDomainModule(jfeatConfig);
        new ProductDomainModule(jfeatConfig);
        new ProductApplicationModule(jfeatConfig);
        new IdentityApplicationModule(jfeatConfig);
        new MemberDomainModule(jfeatConfig);
        new PcdDomainModule(jfeatConfig);

        //PaymentHolder.me().register(WechatPay.PAYMENT_TYPE, new WechatPay());
    }

    @Override
    public void configConstant(com.jfinal.config.Constants me) {
        super.configConstant(me);
    }
}
