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

package com.jfeat.order.api;

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.MarketingHolder;
import com.jfeat.order.OrderCustomerServiceUtil;
import com.jfeat.order.api.model.OrderCustomerServiceEntity;
import com.jfeat.order.api.model.OrderCustomerServiceItemEntity;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderCustomerService;
import com.jfeat.order.model.OrderCustomerServiceItem;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 订单售后
 * Created by huangjacky on 16/6/15.
 */
@ControllerBind(controllerKey = "/rest/order_customer_service")
public class OrderCustomerServiceController extends RestController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    /**
     * POST: {
     * "order_number": "2342323432432",
     * "service_type": "RETURN", //RETURN: 退货退款, REFUND: 仅退款
     * "reason": "AFSFSF",
     * "content": "afaf",
     * "images": ["http://host/a.jpg", "http://host/b.jgp"],
     *
     * //        "returns": [ //退货项
     * //                {
     * //                    "product_id": 130, //required（无论是否提供product_specification_id，都要提供product_id）
     * //                    "product_specification_id": 22, //optional
     * //                    // 1.对于需要关联订单的退货单，不需要传递quantity，会使用其对应的order item的quantity；
     * //                    // 2.对于不需要关联订单的退货单，必须传递quantity
     * //                    // 3.对于一定要关联订单的换货单，这种单据有两个清单（退货项清单和置换项清单）。无论是退货项还是置换项，都必须指定quantity
     * //                    "quantity": 3,
     * //                    //对于退货单的退货项，必须指定refund_fee；对于换货单的退货项，无需指定refund_fee，refund_fee由 “此换货单关联的订单对应的订单项的 price * 传上来的退回数量“ 决定
     * //                    "refund_fee": 40
     * //                }
     * // 	    ],
     * //        "exchanges": [ //置换项
     * //                {
     * //                    "product_id": 122, //required
     * //                    "quantity": 2 //required
     * //                     //refund_fee无需提供
     * //                },
     * //                {
     * //                    "product_id": 130, //required
     * //                     "product_specification_id": 22, //optional
     * //                    // 1.对于需要关联订单的退货单，不需要传递quantity，会使用其对应的order item的quantity；
     * //                    // 2.对于不需要关联订单的退货单，必须传递quantity
     * //                    // 3.对于一定要关联订单的换货单，这种单据有两个清单（退货项清单和置换项清单）。无论是退货项还是置换项，都必须指定quantity
     * //                     "quantity": 3, //required
     * //                    //refund_fee无需提供
     * //                }
     * // 	    ]
     * }
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void save() {
        User currentUser = getAttr("currentUser");
        OrderCustomerServiceEntity orderCustomerServiceEntity = getPostJson(OrderCustomerServiceEntity.class);
        Order order = Order.dao.findByOrderNumber(orderCustomerServiceEntity.getOrder_number());
        if (order == null) {
            renderFailure("invalid.order");
            return;
        }

        if (StrKit.isBlank(orderCustomerServiceEntity.getReason())) {
            renderFailure("reason.is.empty");
            return;
        }

        if (StrKit.isBlank(orderCustomerServiceEntity.getService_type())) {
            renderFailure("service.type.is.empty");
            return;
        }

        OrderCustomerService.ServiceType serviceType;
        try {
            serviceType = OrderCustomerService.ServiceType.valueOf(orderCustomerServiceEntity.getService_type());
        } catch (IllegalArgumentException ex) {
            renderFailure("invalid.service_type");
            return;
        }
        Marketing marketing = MarketingHolder.me().getMarketing(order.getMarketing(),
                order.getMarketingId(),
                order.getUserId(),
                order.getProvince(),
                order.getCity(),
                order.getDistrict());
        if (marketing != null && !marketing.canRefund(order.getId())) {
            renderFailure("the.wholesale.order's.status.is.paid_confirm_pending.or.confirmed_deliver_pending");
            return;
        }
        OrderCustomerService orderCustomerService = new OrderCustomerService();
        orderCustomerService.setServiceType(serviceType.toString());
        orderCustomerService.setReason(orderCustomerServiceEntity.getReason());
        orderCustomerService.addLog(currentUser.getName(), orderCustomerServiceEntity.getContent());
        orderCustomerService.setListToImages(orderCustomerServiceEntity.getImages());
        orderCustomerService.setStoreId(order.getStoreId());
        orderCustomerService.setStoreName(order.getStoreName());
        orderCustomerService.setStoreUserId(order.getStoreUserId());
        orderCustomerService.setStoreUserName(order.getStoreUserName());

        List<OrderCustomerServiceItemEntity> returns = orderCustomerServiceEntity.getReturns();
        List<OrderCustomerServiceItem> returnOrderCustomerServiceItems = new ArrayList<>();
        if (returns != null && !returns.isEmpty()) {
            returnOrderCustomerServiceItems = OrderCustomerServiceUtil.buildReturns(order, returns);
        }

        Ret ret = orderService.applyCustomerService(order, orderCustomerService, returnOrderCustomerServiceItems,null);
        if (BaseService.isSucceed(ret)) {
            renderSuccessMessage(BaseService.getMessage(ret));
        } else {
            renderFailure(BaseService.getMessage(ret));
        }
    }

    @Override
    @Before(CurrentUserInterceptor.class)
    public void show() {
        User currentUser = getAttr("currentUser");
        OrderCustomerService orderCustomerService = OrderCustomerService.dao.findById(getParaToInt());

        if (orderCustomerService == null) {
            renderFailure("order.customer.service.not.found");
            return;
        }

        if (!orderCustomerService.getOrder().getUserId().equals(currentUser.getId())) {
            renderFailure("not.your.order");
            return;
        }

        orderCustomerService.put("log", orderCustomerService.getLogToListMap());
        orderCustomerService.put("images", orderCustomerService.getImagesToList());
        orderCustomerService.put("returns", orderCustomerService.getReturns());
        orderCustomerService.put("exchanges", orderCustomerService.getExchanges());
        renderSuccess(orderCustomerService);
    }

    /**
     * 更新退货单快递信息
     * PUT /rest/order_customer_service/:id
     * {
     * "express_company":"ABC",
     * "express_number":"1324234",
     * "content":"afdsafsadfds"
     * }
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void update() {
        User currentUser = getAttr("currentUser");
        OrderCustomerServiceEntity orderCustomerServiceEntity = getPostJson(OrderCustomerServiceEntity.class);
        OrderCustomerService orderCustomerService = OrderCustomerService.dao.findById(getParaToInt());

        if (orderCustomerService == null) {
            renderFailure("order.customer.service.not.found");
            return;
        }

        if (!orderCustomerService.getOrder().getUserId().equals(currentUser.getId())) {
            renderFailure("not.your.order");
            return;
        }

        if (StrKit.notBlank(orderCustomerServiceEntity.getExpress_company())) {
            orderCustomerService.setExpressCompany(orderCustomerServiceEntity.getExpress_company());
        }
        if (StrKit.notBlank(orderCustomerServiceEntity.getExpress_number())) {
            orderCustomerService.setExpressNumber(orderCustomerServiceEntity.getExpress_number());
        }
        if (StrKit.notBlank(orderCustomerServiceEntity.getContent())) {
            orderCustomerService.addLog(currentUser.getName(), orderCustomerServiceEntity.getContent());
        }
        Ret ret = orderService.updateCustomerService(orderCustomerService);
        if (BaseService.isSucceed(ret)) {
            renderSuccessMessage(BaseService.getMessage(ret));
        } else {
            renderFailure(BaseService.getMessage(ret));
        }
    }
}
