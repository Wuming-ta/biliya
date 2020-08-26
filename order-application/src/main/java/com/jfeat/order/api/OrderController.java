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
import com.jfeat.ext.plugin.*;
import com.jfeat.ext.plugin.store.StoreApi;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.ext.plugin.vip.CouponApi;
import com.jfeat.ext.plugin.vip.bean.CouponPrice;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.marketing.CouponUsage;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.ShippingType;
import com.jfeat.member.model.Coupon;
import com.jfeat.order.OrderStoreUtil;
import com.jfeat.order.api.interceptor.OrderCreatedEnableInterceptor;
import com.jfeat.order.api.model.OrderEntity;
import com.jfeat.order.api.validator.OrderValidator;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderCustomerService;
import com.jfeat.order.model.OrderItem;
import com.jfeat.order.model.param.OrderParam;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.shiro.ShiroMethod;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//订单api（终端用户用）
@ControllerBind(controllerKey = "/rest/order")
public class OrderController extends RestController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    private String getPayExpiryTime(Date createTime, int payTimeout) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(createTime.getTime() + payTimeout * 60 * 1000);
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        return format.format(calendar.getTime());
    }

    /**
     * 查看订单列表。管理员可以查看其他人的订单（通过phone参数）
     * v=2 的时候，返回分页数据
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        Boolean queryMarketing = getParaToBoolean("queryMarketing", true);
        Boolean commented = getParaToBoolean("commented");
        String contactPhone = getPara("contact_phone");
        String phone = getPara("phone");
        String storeId = null;
        Boolean showDeleted = null;
        User currentUser = getAttr("currentUser");
        Integer userId = currentUser.getId();
        if (StrKit.notBlank(phone)) {
            User targetUser = User.dao.findByPhone(phone);
            if (targetUser == null) {
                renderFailure("user.not.found");
                return;
            }
            if (ShiroMethod.lacksPermission("order.view")) {
                renderFailure("lack.of.permission");
                return;
            }
            userId = targetUser.getId();
            storeId = getPara("storeId");
            showDeleted = true;
        }
        Integer v = getParaToInt("v", 1);
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 30);
        String type = getPara("type");
        String[] statuses = getParaValues("status");

        String[] createdDates = getParaValues("created_date");
        String startTime = null, endTime = null;
        if (createdDates != null && createdDates.length == 2) {
            startTime = StrKit.notBlank(createdDates[0]) ? createdDates[0] : null;
            endTime = StrKit.notBlank(createdDates[1]) ? createdDates[1] : null;
        }

        OrderParam orderParam = new OrderParam(pageNumber, pageSize);
        orderParam.setType(type)
                .setStatuses(statuses)
                .setUserId(userId)
                .setQueryMarketing(queryMarketing)
                .setCommented(commented)
                .setStoreId(storeId)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setShowDeleted(showDeleted)
                .setContactPhone(contactPhone);
        Page<Order> page = Order.dao.paginate(orderParam);
        List<Order> list = page.getList();
        final int payTimeout = orderService.getPayTimeout();
        list = list.stream().peek(order -> {
            order.put("commented", StrKit.notBlank(order.getCommentId()));
            order.put("order_items", order.getOrderItems());
            order.put("express_list", order.getOrderExpressList());
            if (order.getStatus().equalsIgnoreCase(Order.Status.CREATED_PAY_PENDING.toString())) {
                order.put("pay_expiry_time", getPayExpiryTime(order.getCreatedDate(), payTimeout));
            }
        }).collect(Collectors.toList());

        if (v == 1) {
            renderSuccess(list);
            return;
        }
        page = new Page<>(list, page.getPageNumber(), page.getPageSize(), page.getTotalPage(), page.getTotalRow());
        renderSuccess(page);
    }

    /**
     * GET /rest/order/<order_number>
     * <p/>
     * Return:
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void show() {
        User currentUser = getAttr("currentUser");
        Order order = Order.dao.findByOrderNumber(getPara());
        if (order != null && currentUser.getId().equals(order.getUserId())) {
            order.put("commented", StrKit.notBlank(order.getCommentId()));
            order.put("order_items", order.getOrderItems());
            order.put("express_list", order.getOrderExpressList());
            List<OrderCustomerService> orderCustomerServices = order.getOrderCustomerService();
            orderCustomerServices = orderCustomerServices.stream().peek(item -> {
                item.put("images", item.getImagesToList());
                item.put("log", item.getLogToListMap());
                item.put("returns", item.getReturns());
                item.put("exchanges", item.getExchanges());
            }).collect(Collectors.toList());
            order.put("order_customer_services", orderCustomerServices);
            if (order.getStatus().equalsIgnoreCase(Order.Status.CREATED_PAY_PENDING.toString())) {
                int payTimeout = orderService.getPayTimeout();
                order.put("pay_expiry_time", getPayExpiryTime(order.getCreatedDate(), payTimeout));
            }
            renderSuccess(order);
        } else {
            renderFailure("invalid.order.id");
        }
    }

    /**
     * POST /rest/order
     * {
     * "marketing": "WHOLESALE",
     * "coupon_id": 1,
     * "payment_type": "POINT",
     * "remark": null,
     * "receiving_time": "anytime",
     * "invoice": 1,
     * "invoice_title": "ABC company",
     * "contact": {
     * "contact_user": "Mr Huang",
     * "phone": "1380000000",
     * "zip": "510000",
     * "province": "GD",
     * "city": "GZ",
     * "district": "Tiahne",
     * "street": "jianzhong road",
     * "detail": "6F"
     * },
     * "order_items": [{
     * "product_id": 1,
     * "quantity": 1,
     * "product_specification_id": 1,
     * "marketing_id": 1,
     * }, {
     * "product_id": 2,
     * "quantity": 1,
     * "marketing_id": 2
     * }, {
     * "product_id": 4,
     * "quantity": 2,
     * "marketing_id": 4
     * }, {
     * "product_id": 5,
     * "quantity": 2,
     * "marketing_id": 5
     * }]
     * }
     * <p/>
     * <p/>
     * Return:
     */
    //线上订单（用户下的单）
    @Override
    @Before({CurrentUserInterceptor.class, OrderCreatedEnableInterceptor.class, OrderValidator.class})
    public void save() {
        User currentUser = getAttr("currentUser");
        Integer userId = currentUser.getId();
        BasePlugin vipPlugin = ExtPluginHolder.me().get(VipPlugin.class);
        BasePlugin storePlugin = ExtPluginHolder.me().get(StorePlugin.class);

        OrderEntity orderEntity = getPostJson(OrderEntity.class);
        List<OrderEntity.OrderItemsEntity> orderItemsEntityList = orderEntity.getOrder_items();
        Coupon coupon = getAttr("coupon");
        String couponTypeName = coupon == null ? null : coupon.getType();

        //默认配送方式为EXPRESS（快递）
        Order.DeliveryType deliveryType = Order.DeliveryType.EXPRESS;
        if (StrKit.notBlank(orderEntity.getDelivery_type())) {
            deliveryType = Order.DeliveryType.valueOf(orderEntity.getDelivery_type());
        }

        Integer credit = orderEntity.getPay_credit();
        boolean freeShipping = false;
        String marketingDescription = null;
        Map<Integer, Marketing> marketings = getAttr("marketings");
        for (Marketing marketing : marketings.values()) {
            CouponUsage couponUsage = marketing.getCouponUsage(couponTypeName);
            coupon = couponUsage.equals(CouponUsage.DISABLED) ? null : coupon;
            freeShipping = marketing.getShippingType() == ShippingType.FREE;
            marketingDescription = marketing.getDescription();
        }
        if (marketings.size() == 0) {
            //对于非营销活动, 不能用营销活动的券
            if (StrKit.notBlank(couponTypeName) && couponTypeName.startsWith(Marketing.MARKETING_COUPON_PREFIX)) {
                coupon = null;
            }
        }

        String origin = StrKit.notBlank(orderEntity.getOrigin()) ? orderEntity.getOrigin() : Order.Origin.OTHER.toString();
        Order.Origin originEnum = Order.Origin.valueOf(origin);

        Order order = new Order();
        order.setOrigin(originEnum.toString());
        order.setType(Order.Type.ORDER.toString());
        order.setUserId(userId);
        order.setRemark(orderEntity.getRemark());
        order.setInvoice(orderEntity.getInvoice());
        order.setInvoiceTitle(orderEntity.getInvoice_title());
        order.setReceivingTime(orderEntity.getReceiving_time());
        order.setMarketing(orderEntity.getMarketing());
        order.setMarketingDescription(marketingDescription);
        order.setMid(orderEntity.getMid());
        order.setMname(orderEntity.getMname());
        order.setDeliveryType(deliveryType.toString());
        order.setStoreId(orderEntity.getStore_id());
        order.setStoreName(orderEntity.getStore_name());
        if (StrKit.notBlank(orderEntity.getPayment_type())) {
            order.setPaymentType(orderEntity.getPayment_type());
        }
        if (orderEntity.getContact() != null) {
            order.setContactUser(orderEntity.getContact().getContact_user());
            order.setPhone(orderEntity.getContact().getPhone());
            order.setProvince(orderEntity.getContact().getProvince());
            order.setCity(orderEntity.getContact().getCity());
            order.setDistrict(orderEntity.getContact().getDistrict());
            order.setStreet(orderEntity.getContact().getStreet());
            order.setZip(orderEntity.getContact().getZip());
            order.setDetail(orderEntity.getContact().getDetail());
        }

        for (OrderEntity.OrderItemsEntity itemEntity : orderItemsEntityList) {
            OrderItem item = new OrderItem();
            item.setProductId(itemEntity.getProduct_id());
            item.setQuantity(itemEntity.getQuantity());

            item.setProductSpecificationId(itemEntity.getProduct_specification_id());
            Marketing marketing = marketings.get(itemEntity.getMarketing_id());
            // 如果是营销活动,那么定价就由活动定
            if (marketing != null) {
                item.setPrice(marketing.getPrice());
                item.setMarketingId(itemEntity.getMarketing_id());
                item.setMarketing(orderEntity.getMarketing());
                item.setMarketingDescription(marketing.getDescription());
                item.setProductName(marketing.getMarketingName());
            }
            order.getOrderItems().add(item);
            order.setMarketingId(itemEntity.getMarketing_id());
        }

        if (vipPlugin.isEnabled() && storePlugin.isEnabled()) {
            OrderStoreUtil.updateStore(currentUser, order);
        }
        if (storePlugin.isEnabled() && StrKit.notBlank(orderEntity.getStore_id())) {
            StoreApi storeApi = new StoreApi();
            Store store = storeApi.getStore(Long.parseLong(orderEntity.getStore_id()));
            order.setStoreId(String.valueOf(store.getId()));
            order.setStoreCode(store.getCode());
            order.setStoreCover(store.getAvatar());
            order.setStoreName(store.getName());
            order.setStoreAddress(store.getFormattedAddress());
        }

        boolean extCoupon = false;
        if (orderEntity.getExt() != null && vipPlugin.isEnabled()) {
            logger.info("Ext coupon provided. ignore system coupon.");
            coupon = null;
            OrderEntity.ExtEntity extCouponEntity = orderEntity.getExt();
            order.setExtCouponId(extCouponEntity.getCoupon_id());
            order.setExtCouponType(extCouponEntity.getCoupon_type());
            order.setExtCuts(extCouponEntity.getCuts());
            order.setExtDiscount(extCouponEntity.getDiscount());
            order.setExtUserType(extCouponEntity.getUser_type());
            extCoupon = true;
        }

        try {
            Long warehouseId = getAttr("warehouseId");
            Ret ret = orderService.createOrder(order, coupon, extCoupon, credit, freeShipping, warehouseId);
            if (BaseService.isSucceed(ret)) {
                logger.debug("Order: {}", order);
                for (Marketing marketing : marketings.values()) {
                    if (marketing != null) {
                        marketing.process(order.getOrderNumber());
                        //record the marketing master id to order if the Marketing's class is PieceGroupMarketing or PieceGroupJointMarketing
                        //record the wholesale id to order if it is the Marketing's class is WholesaleMarketing
                        order.setMarketingId(marketing.getId());
                    }
                }
                if (order.getTotalPrice().compareTo(BigDecimal.ZERO) == 0) {
                    logger.debug("Order {}: total price is 0, directly change status to PAID_CONFIRM_PENDING", order.getOrderNumber());
                    order.setStatus(Order.Status.PAID_CONFIRM_PENDING.toString());
                }
                ret = orderService.updateOrder(order);
                logger.debug("Ret = {}", ret.getData());

                renderSuccess(order);
            } else {
                logger.error("Create order error. Ret={}", ret);
                renderFailure("新建订单失败 - " + BaseService.getMessage(ret));
            }
        } catch (Exception e) {
            logger.error("Error occurred. {}", e.getMessage());
            renderFailure("新建订单失败");
        }
    }

    /**
     * PUT {"status": "CLOSED_CONFIRMED"}
     */
    @Override
    @Before({CurrentUserInterceptor.class})
    public void update() {
        OrderEntity orderEntity = getPostJson(OrderEntity.class);
        Order order = Order.dao.findByOrderNumber(getPara());
        User currentUser = getAttr("currentUser");
        if (order == null || !currentUser.getId().equals(order.getUserId())) {
            renderFailure("invalid.order");
            return;
        }

        Order.Status status = Order.Status.valueOf(orderEntity.getStatus());
        if (status != Order.Status.CLOSED_CONFIRMED && status != Order.Status.CLOSED_CANCELED) {
            renderFailure("invalid.status");
            return;
        }

        order.setStatus(orderEntity.getStatus());
        Ret ret = orderService.updateOrder(order);

        if (BaseService.isSucceed(ret)) {
            logger.debug("Order updated. order={}", order);
            renderSuccessMessage("order.updated");
            return;
        }

        renderFailure("order.status.transfer.error");
    }

    @Override
    @Before(CurrentUserInterceptor.class)
    public void delete() {
        Order order = Order.dao.findByOrderNumber(getPara());
        User currentUser = getAttr("currentUser");
        if (order == null || !currentUser.getId().equals(order.getUserId())) {
            renderFailure("invalid.order");
            return;
        }
        Ret ret = orderService.deleteOrder(order);
        if (BaseService.isSucceed(ret)) {
            renderSuccessMessage(BaseService.getMessage(ret));
            return;
        }
        renderFailure(BaseService.getMessage(ret));
    }
}
