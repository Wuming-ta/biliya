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

package com.jfeat.order.api.store;

import com.google.common.collect.Lists;
import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.BasePlugin;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.StorePlugin;
import com.jfeat.ext.plugin.WmsPlugin;
import com.jfeat.ext.plugin.store.StoreApi;
import com.jfeat.ext.plugin.store.bean.Assistant;
import com.jfeat.ext.plugin.store.bean.QueryStoresApiResult;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.ext.plugin.wms.WmsApi;
import com.jfeat.ext.plugin.wms.services.domain.model.Inventory;
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
import com.jfeat.order.model.OrderItem;
import com.jfeat.order.model.base.OrderCustomerServiceItemBase;
import com.jfeat.order.service.OrderService;
import com.jfeat.order.service.StoreUtil;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 订单售后（店员用）
 * Created by huangjacky on 16/6/15.
 */
@ControllerBind(controllerKey = "/rest/store/order_customer_service")
public class OrderCustomerServiceController extends RestController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    @Before(CurrentUserInterceptor.class)
    @Override
    public void index() {
        String storeId = getPara("storeId");
        if (StrKit.isBlank(storeId)) {
            renderFailure("查找失败 - 必须指定店铺id");
            return;
        }

        String verifyResult = StoreUtil.verify(this, storeId);
        if (StrKit.notBlank(verifyResult)) {
            renderFailure(verifyResult);
            return;
        }

        User currentUser = getAttr("currentUser");


        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String orderNumber = getPara("orderNumber");
        String serviceType = getPara("serviceType");
        String serviceNumber = getPara("serviceNumber");
        String startTime = getPara("startTime");
        String endTime = getPara("endTime");
        Boolean byMe = getParaToBoolean("byMe", false);
        String storeUserId = byMe ? Integer.toString(currentUser.getId()) : null;
        startTime = StrKit.notBlank(startTime) ? startTime + " 00:00:00" : null;
        endTime = StrKit.notBlank(endTime) ? endTime + " 23:59:59" : null;

        Page<OrderCustomerService> page = OrderCustomerService.dao.paginate(pageNumber, pageSize,
                orderNumber,
                serviceType, serviceNumber,
                storeId, storeUserId,
                startTime, endTime);
        List<OrderCustomerService> list = page.getList();
        list = list.stream().peek(orderCustomerService -> {
            int quantity = orderCustomerService.getReturns().stream().mapToInt(OrderCustomerServiceItemBase::getQuantity).sum();
            orderCustomerService.put("return_count", quantity);
        }).collect(Collectors.toList());
        Page<OrderCustomerService> pageResult = new Page<>(list, page.getPageNumber(), page.getPageSize(), page.getTotalPage(), page.getTotalRow());
        renderSuccess(pageResult);
    }

    @Before(CurrentUserInterceptor.class)
    @Override
    public void show() {
        User currentUser = getAttr("currentUser");
        String serviceNumber = getPara();
        if (StrKit.isBlank(serviceNumber)) {
            renderFailure("查找失败，必须指定售后单号");
            return;
        }

        OrderCustomerService orderCustomerService = OrderCustomerService.dao.findFirstByField(OrderCustomerService.Fields.SERVICE_NUMBER.toString(), serviceNumber);
        if (orderCustomerService == null) {
            renderError(404);
            return;
        }

        //退货单可以关联订单，也可不关联订单；换货单必须关联订单。因此Order不一定存在。
        Order order = orderCustomerService.getOrder();
        if (order == null) {
            order = new Order();
        }


        String verifyResult = StoreUtil.verify(this, orderCustomerService.getStoreId());
        if (StrKit.notBlank(verifyResult)) {
            renderFailure(verifyResult);
            return;
        }

        orderCustomerService.put("order", order);
        orderCustomerService.put("order_items", order.getOrderItems());

        List<OrderCustomerServiceItem> returns = orderCustomerService.getReturns();
        orderCustomerService.put("returns", returns);

        List<OrderCustomerServiceItem> exchanges = orderCustomerService.getExchanges();
        orderCustomerService.put("exchanges", exchanges);

        renderSuccess(orderCustomerService);
    }

//
//    POST:{
//        "store_id": "1", //required
//        "store_name": "总店", // required
//        "order_number": "2342323432432",
//        "service_type": "RETURN", //required，REFUND-仅退款 RETURN-退货退款,   EXCHANGE-换货
//        "reason": "AFSFSF", //required
//        "content": "afaf", //optional
//        "images": ["http://host/a.jpg", "http://host/b.jgp"], //optional
//        "returns": [ //退货项
//                {
//                    "product_id": 130, //required（无论是否提供product_specification_id，都要提供product_id）
//                    "product_specification_id": 22, //optional
//                    // 1.对于需要关联订单的退货单，不需要传递quantity，会使用其对应的order item的quantity；
//                    // 2.对于不需要关联订单的退货单，必须传递quantity
//                    // 3.对于一定要关联订单的换货单，这种单据有两个清单（退货项清单和置换项清单）。无论是退货项还是置换项，都必须指定quantity
//                    "quantity": 3,
//                    //对于退货单的退货项，必须指定refund_fee；对于换货单的退货项，无需指定refund_fee，refund_fee由 “此换货单关联的订单对应的订单项的 price * 传上来的退回数量“ 决定
//                    "refund_fee": 40
//                }
// 	    ],
//        "exchanges": [ //置换项
//                {
//                    "product_id": 122, //required
//                    "quantity": 2 //required
//                     //refund_fee无需提供
//                },
//                {
//                    "product_id": 130, //required
//                     "product_specification_id": 22, //optional
//                    // 1.对于需要关联订单的退货单，不需要传递quantity，会使用其对应的order item的quantity；
//                    // 2.对于不需要关联订单的退货单，必须传递quantity
//                    // 3.对于一定要关联订单的换货单，这种单据有两个清单（退货项清单和置换项清单）。无论是退货项还是置换项，都必须指定quantity
//                     "quantity": 3, //required
//                    //refund_fee无需提供
//                }
// 	    ]
//    }

    /**
     * 店员创建售后单，并执行原路退回。
     */
    @Before(CurrentUserInterceptor.class)
    @Override
    @Validation(rules = { "store_id = required", "reason = required", "service_type = required" })
    public void save() {
        OrderCustomerServiceEntity orderCustomerServiceEntity = getPostJson(OrderCustomerServiceEntity.class);

        String verifyResult = StoreUtil.verify(this, orderCustomerServiceEntity.getStore_id());
        if (StrKit.notBlank(verifyResult)) {
            renderFailure(verifyResult);
            return;
        }
        Store store = getAttr("store");
        Assistant assistant = getAttr("assistant");
        User currentUser = getAttr("currentUser");

        OrderCustomerService.ServiceType serviceType = OrderCustomerService.ServiceType.valueOf(orderCustomerServiceEntity.getService_type());
        //仅退款
        if (serviceType.equals(OrderCustomerService.ServiceType.REFUND)) {
            renderFailure("暂不支持此种方式售后单");
            return;
        }

        Order order = Order.dao.findByOrderNumber(orderCustomerServiceEntity.getOrder_number());
        if (StrKit.notBlank(orderCustomerServiceEntity.getOrder_number()) && order == null) {
            renderFailure("order.not.found");
            return;
        }

        List<OrderCustomerServiceItemEntity> returns = orderCustomerServiceEntity.getReturns();
        List<OrderCustomerServiceItemEntity> exchanges = orderCustomerServiceEntity.getExchanges();

        //退货单和换货单都必须指定退货项目
        if (serviceType.equals(OrderCustomerService.ServiceType.RETURN)
                && (returns == null || returns.isEmpty())) {
            renderFailure("售后单创建失败 - 退货单，换货单必须指定退货项目");
            return;
        }

        //换货单必须指定置换项目
        if (serviceType.equals(OrderCustomerService.ServiceType.EXCHANGE)
                && (order == null
                    || (returns == null || returns.isEmpty())
                    || (exchanges == null || exchanges.isEmpty()))) {
            renderFailure("售后单创建失败 - 换货单必须指定置换项目");
            return;
        }

        if (order == null) {
            logger.debug("creating customer service without order. create a dummy order for it. ");
            List<OrderItem> orderItems = new ArrayList<>();
            returns.forEach(item -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(item.getQuantity());
                Product product = Product.dao.findById(item.getProduct_id());
                if (product == null) {
                    renderFailure(String.format("售后单创建失败 - 产品(id:%s)不存在", item.getProduct_id()));
                    return;
                }
                orderItem.setCover(product.getCover());
                orderItem.setProductName(product.getName());
                orderItem.setBarcode((StrKit.notBlank(product.getBarCode()) ? product.getBarCode() : product.getBarcode()));
                orderItem.setProductId(item.getProduct_id());
                orderItem.setProductSpecificationId(item.getProduct_specification_id());
                ProductSpecification productSpecification = ProductSpecification.dao.findById(item.getProduct_specification_id());
                if (productSpecification != null) {
                    orderItem.setProductSpecificationName(productSpecification.getName());
                    orderItem.setBarcode(productSpecification.getBarCode());
                }
                orderItem.setQuantity(item.getQuantity());
                orderItems.add(orderItem);
            });
            order = orderService.createFakeOrder(currentUser.getId(), orderItems);
            order.setStatus(Order.Status.DELIVERED_CONFIRM_PENDING.toString());
            order.setDeliveryType(Order.DeliveryType.SELF_PICK.toString());
            order.setStoreCover(store.getAvatar());
            order.setStoreCode(store.getCode());
            order.setStoreAddress(store.getFormattedAddress());
            order.setStoreName(store.getName());
            order.setStoreId(store.getId().toString());
            order.setPaymentType(Order.PaymentType.CASH.toString());
            order.setOrigin(Order.Origin.OTHER.toString());
            order.setType(Order.Type.STORE_ORDER.toString());
            order.setStoreUserName(assistant.getName());
            order.setStoreUserCode(assistant.getCode());
            order.setStoreUserId(assistant.getUserId().toString());
            order.setDescription("REFUND");
            order.update();
            logger.debug("order created: {}", order.toJson());
        }

        //构建要保存的OrderCustomerService
        OrderCustomerService orderCustomerService = new OrderCustomerService();
        orderCustomerService.setServiceType(serviceType.toString());
        orderCustomerService.setReason(orderCustomerServiceEntity.getReason());
        orderCustomerService.addLog(currentUser.getName(), orderCustomerServiceEntity.getContent());
        orderCustomerService.setListToImages(orderCustomerServiceEntity.getImages());
        orderCustomerService.setStoreId(orderCustomerServiceEntity.getStore_id());
        orderCustomerService.setStoreName(orderCustomerServiceEntity.getStore_name());
        orderCustomerService.setStoreUserId(Integer.toString(currentUser.getId()));
        orderCustomerService.setStoreUserName(currentUser.getName());

        //////////////////////////////////////
        ////////////////// 退货 //////////////
        if (serviceType.equals(OrderCustomerService.ServiceType.RETURN)) {
            //退货单可以有单号，也可以没有单号
            //若此退货单是关联一个订单的，且此订单是关联一个营销活动的，则必须此营销活动是支持退货才允许退货
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
            orderCustomerService.setSupplementaryFee(BigDecimal.ZERO);

            //构建要保存的OrderCustomerServiceItem列表
            List<OrderCustomerServiceItem> returnOrderCustomerServiceItems = OrderCustomerServiceUtil.buildReturns(order, returns);
            Ret ret = orderService.applyCustomerService(order, orderCustomerService, returnOrderCustomerServiceItems, null);
            if (BaseService.isSucceed(ret)) {
                logger.debug("attempting refund, otherway = {}, {}", orderCustomerServiceEntity.getOtherway(), orderCustomerService.toJson());
                ret = orderService.refundOrder(orderCustomerService, orderCustomerServiceEntity.getOtherway());
                logger.debug("refund result = {}", ret.getData());

                if (BaseService.isSucceed(ret)) {
                    User user = order.getUser();
                    String note = String.format("用户 %s 订单号 %s 退货单号 %s，回退库存", user.getName(), order.getOrderNumber(), orderCustomerService.getServiceNumber());
                    Long warehouseId = StoreUtil.getWarehouseId(order.getStoreId());
                    orderService.decreaseRefundProductSales(user.getId().longValue(), user.getLoginName(), orderCustomerService, note, warehouseId);
                }

                renderSuccessMessage(BaseService.getMessage(ret));
            } else {
                renderFailure(BaseService.getMessage(ret));
            }
            return;
        }

        //////////////////////////////////////
        ////////////////// 换货 //////////////
        if (serviceType.equals(OrderCustomerService.ServiceType.EXCHANGE)) {
            //换货单必须有单号
            //若此换货单所关联的订单是关联一个营销活动的，则必须此营销活动是支持退货才允许退货
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
            orderCustomerService.setSupplementaryFee(orderCustomerServiceEntity.getSupplementary_fee());

            //构建要保存的OrderCustomerServiceItem列表（退货清单）
            List<OrderCustomerServiceItem> returnOrderCustomerServiceItems = OrderCustomerServiceUtil.buildReturns(order, returns);
            //构建要保存的OrderCustomerServiceItem列表（置换清单）
            List<OrderCustomerServiceItem> exchangeOrderCustomerServiceItems = OrderCustomerServiceUtil.buildExchanges(currentUser, exchanges);

            Ret ret = orderService.applyCustomerService(order, orderCustomerService, returnOrderCustomerServiceItems, exchangeOrderCustomerServiceItems);
            if (BaseService.isSucceed(ret)) {
                logger.debug("attempting refund, otherway = {}, {}", orderCustomerServiceEntity.getOtherway(), orderCustomerService.toJson());
                ret = orderService.refundOrder(orderCustomerService, orderCustomerServiceEntity.getOtherway());
                logger.debug("refund result = {}", ret.getData());

                if (BaseService.isSucceed(ret)) {
                    User user = order.getUser();
                    String note = String.format("用户 %s 订单号 %s 退货单号 %s，回退库存", user.getName(), order.getOrderNumber(), orderCustomerService.getServiceNumber());
                    Long warehouseId = StoreUtil.getWarehouseId(order.getStoreId());
                    orderService.decreaseRefundProductSales(user.getId().longValue(), user.getLoginName(), orderCustomerService, note, warehouseId);
                }

                renderSuccessMessage(BaseService.getMessage(ret));
            } else {
                renderFailure(BaseService.getMessage(ret));
            }
        }
    }

}
