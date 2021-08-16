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

package com.jfeat.order.service;

import com.google.common.collect.Lists;
import com.jfeat.config.model.Config;
import com.jfeat.config.utils.ConfigUtils;
import com.jfeat.core.BaseService;
import com.jfeat.ext.plugin.*;
import com.jfeat.ext.plugin.async.AsyncTaskKit;
import com.jfeat.ext.plugin.store.StoreApi;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.ext.plugin.vip.CouponApi;
import com.jfeat.ext.plugin.vip.CreditApi;
import com.jfeat.ext.plugin.vip.PointApi;
import com.jfeat.ext.plugin.vip.VipApi;
import com.jfeat.ext.plugin.vip.bean.CouponPrice;
import com.jfeat.ext.plugin.vip.bean.Grade;
import com.jfeat.ext.plugin.vip.bean.VipAccount;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.MarketingHolder;
import com.jfeat.member.model.Contact;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.service.CouponResult;
import com.jfeat.member.service.CouponService;
import com.jfeat.order.handler.OrderExpiredHandler;
import com.jfeat.order.model.*;
import com.jfeat.order.notification.*;
import com.jfeat.order.notify.OrderBillNotifier;
import com.jfeat.payment.Payment;
import com.jfeat.payment.PaymentHolder;
import com.jfeat.product.exception.StockBalanceException;
import com.jfeat.product.model.CarryMode;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;
import com.jfeat.product.service.CarriageCalcResult;
import com.jfeat.product.service.PostageService;
import com.jfeat.product.service.ProductPurchasing;
import com.jfeat.product.service.ProductService;
import com.jfeat.service.exception.RetrieveOrderException;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jacky on 3/10/16.
 */
public class OrderService extends BaseService implements OrderPayService {

    public static final String ORDER_PAY_TIMEOUT_KEY = "mall.pay_order_timeout";
    private static final String ORDER_RECEIVING_DEADLINE_KEY = "mall.auto_validation_receiving_deadline";
    private static final String ORDER_RETURN_TIMEOUT_KEY = "mall.latest_return_time";
    private static final String POINT_EXCHANGE_RATE_KEY = "mall.point_exchange_rate";
    private static final String FLASH_FREIGHT_KEY = "mall.flash_freight";

    private static final String FALLBACK_URL_ORDER_DETAIL_KEY = "mall.url.order_detail";

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String ORDER_STATUS_CHANGE_CONTENT = "订单【%s】状态置为【%s】";

    private static final String PRICE_UNIT_CNY = "元";
    private static final String PRICE_UNIT_POINT = "积分";

    private static final String NO_DEFAULT_CONTACT = "尚未配置默认配送区域，将不能计算价格";
    private static final String DELIVERING_NOT_SUPPORTED = "不支持此送货地区：%s";

    private PostageService postageService = Enhancer.enhance(PostageService.class);
    private ProductService productService = Enhancer.enhance(ProductService.class);
    private CouponService couponService = Enhancer.enhance(CouponService.class);

    /**
     * 极速送达的运费
     * @return
     */
    public int getFlashFreight() {
        Config config = Config.dao.findByKey(OrderService.FLASH_FREIGHT_KEY);
        int value = 0;
        if (config == null) {
            logger.error("{} is not defined. use default 0.", OrderService.FLASH_FREIGHT_KEY);
        } else {
            value = config.getValueToInt();
        }
        return value;
    }

    /**
     * 返回配置的订单超时时间,单位 分钟
     * @return
     */
    public int getPayTimeout() {
        Config config = Config.dao.findByKey(OrderService.ORDER_PAY_TIMEOUT_KEY);
        int payTimeout = 15; //15 minutes by default
        if (config == null) {
            logger.error("{} is not defined. use default 15 minutes.", OrderService.ORDER_PAY_TIMEOUT_KEY);
        } else {
            payTimeout = config.getValueToInt();
        }
        return payTimeout;
    }

    public int getPointExchangeRate() {
        //有配置积分兑换比例pointExchangeRate，则使用配置，没有则pointExchangeRate为100
        Config config = Config.dao.findByKey(POINT_EXCHANGE_RATE_KEY);
        int pointExchangeRate = 100;
        if (config != null && config.getValueToInt() != null) {
            pointExchangeRate = config.getValueToInt();
        }
        return pointExchangeRate;
    }

    /**
     * 查找超出自动确认收货期限的订单
     *
     * @return
     */
    public List<Order> queryConfirmTimeoutOrders() {
        Config config = Config.dao.findByKey(ORDER_RECEIVING_DEADLINE_KEY);
        int autoValidationReceivingDeadline = 10;
        if (config == null) {
            logger.error("{} is not define. use default 10.", ORDER_RECEIVING_DEADLINE_KEY);
        } else {
            autoValidationReceivingDeadline = config.getValueToInt();
        }

        return Order.dao.findConfirmTimeoutOrder(DateKit.daysAgoStr(autoValidationReceivingDeadline));
    }

    /**
     * 查找超出最迟退货时间的订单
     *
     * @return
     */
    public List<Order> queryReturnTimeoutOrders() {
        Config config = Config.dao.findByKey(ORDER_RETURN_TIMEOUT_KEY);
        if (config == null || config.getValueToInt() == 0) {
            logger.info("{} is null or 0", ORDER_RETURN_TIMEOUT_KEY);
            return new ArrayList<>();
        }

        return Order.dao.findReturnTimeoutOrder(DateKit.daysAgoStr(config.getValueToInt()));
    }

    private void createOrderProcessLog(Integer orderId, String content) {
        OrderProcessLog orderProcessLog = new OrderProcessLog();
        orderProcessLog.setOrderId(orderId);
        orderProcessLog.setContent(content);
        orderProcessLog.setProcessDate(new Date());
        orderProcessLog.save();
    }

    public Order createFakeOrder(Integer userId) {
        return createFakeOrder(userId, null);
    }

    public Order createFakeOrder(Integer userId, List<OrderItem> orderItems) {
        Order order = new Order();
        order.setUserId(userId);
        order.setCreatedDate(new Date());
        order.setCity("GZ");
        order.setProvince("GD");
        order.setDistrict("TH");
        order.setDetail("Dummy");
        order.setTotalPrice(BigDecimal.ZERO);
        order.setPaymentType(Order.PaymentType.CASH.toString());
        order.save();
        if (orderItems == null) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setFinalPrice(BigDecimal.ZERO);
            orderItem.setProductId(0);
            orderItem.setProductName("dummy");
            orderItem.setQuantity(1);
            orderItem.save();
        }
        else {
            orderItems.forEach(item -> {
                item.setOrderId(order.getId());
                item.save();
            });
        }
        order.setPayDate(new Date());
        order.setStatus(Order.Status.CLOSED_CANCELED.toString());
        order.update();
        return order;
    }

    /**
     * 新建订单
     *
     * @param order
     * @param coupon
     * @param extCoupon 第三方优惠券优惠价钱,如果提供就使用该值计算总价
     * @param freeShipping
     * @return
     * @throws Exception
     */
    @Before(Tx.class)
    public Ret createOrder(Order order, Coupon coupon, boolean extCoupon, Integer credit, boolean freeShipping, Long warehouseId) throws Exception {
        //必须指定至少一个订单项
        List<OrderItem> orderItems = order.getOrderItems();
        if (orderItems.isEmpty()) {
            return failure("order.items.empty");
        }

        User user = order.getUser();
        User inviter = user.getInviter();
        if (inviter != null) {
            order.setInviterUserId(inviter.getId().toString());
            order.setInviterUserName(inviter.getName());
        }

        order.setPointExchangeRate(getPointExchangeRate());
        //保存订单
        order.save();

        //创建订单处理记录
        createOrderProcessLog(order.getId(),
                String.format(ORDER_STATUS_CHANGE_CONTENT, order.getOrderNumber(), Order.Status.CREATED_PAY_PENDING.toChineseName()));

        BasePlugin vipPlugin = ExtPluginHolder.me().get(VipPlugin.class);
        Order.DeliveryType deliveryType = Order.DeliveryType.valueOf(order.getDeliveryType());
        boolean usedCoupon = false;
        BigDecimal originalPrice = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal benefit = BigDecimal.ZERO;
        StringBuilder description = new StringBuilder();
        List<ProductPurchasing> productPurchasings = new ArrayList<>();
        List<OrderItem> allowCouponOrderItems = new ArrayList<>();
        List<OrderItem> notAllowCouponOrderItems = new ArrayList<>();
        Integer toUseCredit = 0;
        for (OrderItem item : orderItems) {
            Product product = Product.dao.findById(item.getProductId());
            BigDecimal price = product.getPrice();
            BigDecimal costPrice = product.getCostPrice();
            Integer weight = product.getWeight();
            Integer bulk = product.getBulk();
            String skuId = product.getSkuId();
            String barCode = StrKit.notBlank(product.getBarCode()) ? product.getBarCode() : product.getBarcode();
            toUseCredit += product.getCredit();

            ProductSpecification productSpecification = ProductSpecification.dao.findById(item.getProductSpecificationId());
            //当前遍历到的item有指定规格且规格在数据库中存在，则使用规格的相应字段值
            if (productSpecification != null) {
                price = productSpecification.getPrice();
                costPrice = productSpecification.getCostPrice();
                barCode = productSpecification.getBarCode();
                skuId = productSpecification.getSkuId();
                item.setProductSpecificationName(productSpecification.getName());
                item.setWeight(productSpecification.getWeight());
                item.setBulk(productSpecification.getBulk());
            }
            if (item.getPrice() == null) {
                item.setPrice(price);
            }
            if (StrKit.isBlank(item.getProductName())) {
                item.setProductName(product.getName());
            }
            item.setMid(product.getMid());
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setBarcode(barCode);
            item.setSkuId(skuId);
            item.setWarehouseId(warehouseId + "");
            item.setStoreLocation(product.getStoreLocation());
            item.setCostPrice(costPrice);
            item.setCover(product.getCover());
            item.setPartnerLevelZone(product.getPartnerLevelZone());
            item.setWeight(weight);
            item.setBulk(bulk);
            item.setFinalPrice(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            originalPrice = originalPrice.add(item.getFinalPrice());

            //产品优惠券
            if (product.getAllowCoupon() == Product.AllowCoupon.YES.getValue()) {
                allowCouponOrderItems.add(item);
                CouponResult couponResult = couponService.productCouponCalc(coupon, item.getProductId(), item.getFinalPrice().doubleValue());
                if (!usedCoupon && couponResult != null && couponResult.getFinalPrice().compareTo(item.getFinalPrice()) < 0) {
                    logger.debug("use product coupon (coupon_id = {}). product_id = {}, original price = {}, price using coupon = {}",
                            coupon.getId(), item.getProductId(), item.getFinalPrice(), couponResult.getFinalPrice());
                    benefit = item.getFinalPrice().subtract(couponResult.getFinalPrice());
                    item.setFinalPrice(couponResult.getFinalPrice());
                    couponService.useCoupon(order.getUserId(), coupon, order.getOrderNumber(), benefit);
                    usedCoupon = true;
                }
            }
            else {
                notAllowCouponOrderItems.add(item);
            }
            item.save();

            totalPrice = totalPrice.add(item.getFinalPrice());
            description.append(item.getProductName());
            if (StrKit.notBlank(item.getProductSpecificationName())) {
                description.append(item.getProductSpecificationName());
            }
            description.append("x");
            description.append(item.getQuantity());

            if (StrKit.isBlank(order.getCover())) {
                order.setCover(product.getCover());
            }

            if (product.getFareId() != null) {
                ProductPurchasing productPurchasing = new ProductPurchasing(product.getFareId(), item.getQuantity(), item.getPrice(), item.getWeight(), item.getBulk());
                productPurchasings.add(productPurchasing);
            }
        }

        //整单优惠券
        if (!usedCoupon) {
            double allowCouponItemTotalPrice = allowCouponOrderItems.stream().mapToDouble(i -> i.getFinalPrice().doubleValue()).sum();
            double notAllowCouponItemTotalPrice = notAllowCouponOrderItems.stream().mapToDouble(i -> i.getFinalPrice().doubleValue()).sum();
            CouponResult couponResult = couponService.orderCouponCalc(coupon, allowCouponItemTotalPrice);
            if (couponResult != null && couponResult.getFinalPrice().compareTo(BigDecimal.valueOf(allowCouponItemTotalPrice)) < 0) {
                logger.debug("use order coupon (coupon_id = {}). order_number = {}, original price = {}, price using coupon = {}",
                        coupon.getId(), order.getOrderNumber(), allowCouponItemTotalPrice, couponResult.getFinalPrice());
                benefit = BigDecimal.valueOf(allowCouponItemTotalPrice).subtract(couponResult.getFinalPrice());
                totalPrice = couponResult.getFinalPrice().add(BigDecimal.valueOf(notAllowCouponItemTotalPrice));
                couponService.useCoupon(order.getUserId(), coupon, order.getOrderNumber(), benefit);
                usedCoupon = true;
            }
        }

        if (!usedCoupon) {
            CouponResult couponResult = couponService.marketingCouponCalc(coupon, totalPrice.doubleValue());
            if (couponResult != null && couponResult.getFinalPrice().compareTo(totalPrice) < 0) {
                logger.debug("use marketing coupon (coupon_id = {}). order_number = {}, original price = {}, price using coupon = {}",
                        coupon.getId(), order.getOrderNumber(), totalPrice, couponResult.getFinalPrice());
                benefit = totalPrice.subtract(couponResult.getFinalPrice());
                totalPrice = couponResult.getFinalPrice();
                if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
                    totalPrice = BigDecimal.ZERO;
                }
                couponService.useCoupon(order.getUserId(), coupon, order.getOrderNumber(), benefit);
                usedCoupon = true;
            }
        }

        // 积分vip-credit使用
        if (credit != null && credit > 0 && toUseCredit > 0) {
            logger.debug("user {} credit = {}, toUseCredit = {}", user.getLoginName(), credit, toUseCredit);
            if (vipPlugin.isEnabled()) {
                VipApi vipApi = new VipApi();
                VipAccount vipAccount = vipApi.getVipAccount(user.getLoginName());
                Grade grade = vipApi.getGrade(vipAccount.getGradeId());
                if (vipAccount.getInvalid() == 0
                        && grade.getCreditCashPlanEnabled() == 1
                        && grade.getCreditCashPlan() > 0
                        && grade.getCreditWinCash().compareTo(BigDecimal.ZERO) > 0) {
                    Integer finalCredit = credit > toUseCredit ? toUseCredit : credit;
                    BigDecimal amount = grade.getCreditWinCash()
                            .multiply(BigDecimal.valueOf(finalCredit))
                            .divide(BigDecimal.valueOf(grade.getCreditCashPlan()), 2, BigDecimal.ROUND_HALF_UP)
                            .divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                    order.setPayCredit(finalCredit);
                    order.setCreditPrice(amount);
                    totalPrice = totalPrice.subtract(amount);
                    logger.debug("ordernumber {} use vip credit: {}, totalPrice = {}", order.getOrderNumber(), finalCredit, totalPrice);

                    CreditApi creditApi = new CreditApi();
                    creditApi.consumeAccountCredit(user.getLoginName(), totalPrice, amount, finalCredit, order.getOrderNumber());
                }
                else {
                    logger.debug("not going to update vip {}'s credit. invalid = {}, grade enabled = {}, credit cash plan = {}, credit win cash = {}",
                            vipAccount.getAccount(), vipAccount.getInvalid(), grade.getCreditCashPlanEnabled(), grade.getCreditCashPlan(), grade.getCreditWinCash());
                }
            }
        }

        if (extCoupon && vipPlugin.isEnabled()) {
            logger.debug("orderId {} use extCoupon.", order.getId());
            CouponApi couponApi = new CouponApi();
            CouponPrice couponPrice = couponApi.calcPrice(order.getExtCouponId(),
                    order.getUserId().toString(),
                    //CR.issue, fix issue for null pointer
                    //order.getTotalPrice().toPlainString());
                    totalPrice.toPlainString());
            BigDecimal extCouponPrice = couponPrice.getCouponPrice();
            totalPrice = totalPrice.subtract(extCouponPrice);
            benefit = extCouponPrice;
            logger.debug("orderId {} used ext coupon, totalPrice = {}, benefit = {}", order.getId(), totalPrice, benefit);
        }

        order.setOriginPrice(originalPrice);
        order.setCouponPrice(benefit);

        if (usedCoupon || extCoupon) {
            buildCouponInfo(order, coupon);
        }

        //lock stockbalance
        increaseProductSales(order, order.getRemark(), warehouseId);

        if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            logger.debug("orderNumber {} totalPrice = {}, set to zero.", order.getOrderNumber(), totalPrice);
            totalPrice = BigDecimal.ZERO;
        }

        //calc postage
        if (!freeShipping && deliveryType == Order.DeliveryType.EXPRESS) {
            CarriageCalcResult carriageCalcResult = postageService.calculate(productPurchasings, getRegion(order), CarryMode.CarryWay.EXPRESS);
            order.setFreight(carriageCalcResult.getResult());
            totalPrice = totalPrice.add(carriageCalcResult.getResult());

        }
        if (!freeShipping && deliveryType == Order.DeliveryType.FLASH) {
            BigDecimal freight = BigDecimal.valueOf(getFlashFreight());
            order.setFreight(freight);
            totalPrice = totalPrice.add(freight);
        }

        order.setTotalPrice(totalPrice);
        order.setDescription(description.toString());
        order.update();
        try {
            OrderExpiredHandler.add(order.getId());
        } catch (Exception e) {
            logger.error("order_id: {} ,fail to register ExpiredHandler", order.getId());
        }
        order.put("order_items", orderItems);
        return success("order.create.success", order);
    }

    private void buildCouponInfo(Order order, Coupon coupon) {
        StringBuilder couponInfo = new StringBuilder();
        couponInfo.append("使用 ");
        if (coupon != null) {
            couponInfo.append(coupon.getName());
            couponInfo.append("-");
            couponInfo.append(coupon.getDisplayName());
        }
        if (StrKit.notBlank(order.getExtCouponId())) {
            couponInfo.append("[couponId = ").append(order.getExtCouponId());
            couponInfo.append(", couponType = ").append(order.getExtCouponType());
            couponInfo.append(", discount = ").append(order.getExtDiscount());
            couponInfo.append(", cuts = ").append(order.getExtCuts());
            couponInfo.append(", userType = ").append(order.getExtUserType());
            couponInfo.append("] ");
        }
        couponInfo.append("优惠了 ");
        couponInfo.append(order.getCouponPrice());
        couponInfo.append(" 元");
        order.setCouponInfo(couponInfo.toString());
    }

    private String getRegion(Order order) {
        StringBuilder builder = new StringBuilder();
        builder.append(order.getProvince());
        builder.append("-");
        builder.append(order.getCity());
        return builder.toString();
    }

    /**
     * @param order
     * @return
     */
    @Deprecated
    public Ret prePayOrder(Order order, String ip, String notifyUrl) {
        Payment payment = PaymentHolder.me().getPayment(order.getPaymentType());
        return payment.prePay(order.getDescription(), order.getOrderNumber(), order.getTotalPrice().doubleValue(), ip, notifyUrl);
    }

    /**
     * only POINT pay use this method currently. wechat pay doesn't go this way.
     *
     * @param order
     * @return
     */
    @Before(Tx.class)
    public Ret payOrder(Order order) {
        Payment payment = PaymentHolder.me().getPayment(order.getPaymentType());
        if (!payment.canPay(order.getUserId(), order.getTotalPrice().doubleValue())) {
            return failure("insufficient.balance");
        }

        Ret ret = payment.pay(order.getUserId(), order.getTotalPrice().doubleValue(), "");
        logger.debug("payment.pay ret = {}", ret.getData());
        if (ret.get("result")) {
            //update order status
            order.setStatus(Order.Status.PAID_CONFIRM_PENDING.toString());
            ret = updateOrder(order);
            logger.debug("change order status to PAID_CONFIRM_PENDING, ret = {}", ret.getData());
            if (isSucceed(ret)) {
                return success("order.paid", order);
            }
        }

        return failure("pay.failure");
    }

    /** 退货操作
     * @param orderCustomerService
     * @return
     */
    @Before(Tx.class)
    public Ret refundOrder(OrderCustomerService orderCustomerService, boolean otherWay) {
        Order order = orderCustomerService.getOrder();
        boolean isRefundSucceed = true;
        String failureMsg = "";
        if (!otherWay) {
            logger.debug("refund order {} with original path. {}", order.getOrderNumber(), order.getPaymentType());
            Payment payment = PaymentHolder.me().getPayment(order.getPaymentType());
            Ret ret = payment.refund(order.getUserId(),
                    "Order",
                    order.getOrigin(),
                    order.getOrderNumber(),
                    orderCustomerService.getServiceNumber(),
                    order.getTotalPrice(),
                    orderCustomerService.getRefundFee());
            isRefundSucceed = ret.get("result");
            failureMsg = ret.get("message");
            logger.debug("refund result = {}", JsonKit.toJson(ret.getData()));
        }
        String refundWay = otherWay ? OrderCustomerService.RefundWay.OTHER_WAY.toString() : OrderCustomerService.RefundWay.ORIGINAL_PATH.toString();
        orderCustomerService.setRefundWay(refundWay);

        if (isRefundSucceed) {
            orderCustomerService.addLog("system", "refund success");
            orderCustomerService.setStatus(OrderCustomerService.Status.REFUNDED.toString());
            updateCustomerService(orderCustomerService);
            logger.debug("order-customer-service = {}", orderCustomerService);

            OrderBillNotifier.sendRefundedOrderNotify(orderCustomerService);

            if (order.getOrderCustomerService().stream()
                    .filter(item -> !item.getId().equals(orderCustomerService.getId()))
                    .noneMatch(item -> {
                        OrderCustomerService.Status status = OrderCustomerService.Status.valueOf(item.getStatus());
                        return (status == OrderCustomerService.Status.REFUND_PENDING
                                || status == OrderCustomerService.Status.RETURN_PENDING
                                || status == OrderCustomerService.Status.CREATED
                        );
                    })) {
                logger.debug("no other customer service under REFUND_PENDING status. going to update order status.");
                List<OrderItem> orderItems = order.getOrderItems();
                long refundedCount = orderItems.stream().filter(item -> item.getStatus().equals(OrderItem.Status.REFUNDED.toString())).count();
                order.setStatus(Order.Status.CLOSED_REFUNDED.toString());
                if (orderItems.size() > refundedCount) {
                    order.setStatus(Order.Status.DELIVERED_CONFIRM_PENDING.toString());
                    order.setDeliverDate(new Date());
                }
                logger.debug("updating order status to {}.", order.getStatus());
                order.setRefundFee(order.getRefundFee().add(orderCustomerService.getRefundFee()));
                BigDecimal supplementaryFee = orderCustomerService.getSupplementaryFee() == null ? BigDecimal.ZERO : orderCustomerService.getSupplementaryFee();
                order.setSupplementaryFee(order.getSupplementaryFee().add(supplementaryFee));
                updateOrder(order);
                logger.debug("order = {}", order);
            }

            order.refundedOrderNotify();
            resetVipCoupon(order.getExtCouponId(), order.getUserId().toString(), order.getOrderNumber());

            new OrderRefundedNotification(order.getUser().getWeixin())
                    .param(OrderRefundedNotification.ORDER_NUMBER, order.getOrderNumber())
                    .param(OrderRefundedNotification.ORDER_PRICE, getOrderTotalPrice(order))
                    .param(OrderRefundedNotification.REFUNDED_TIME, DateKit.today(DATE_TIME_FORMAT))
                    .setUrl(ConfigUtils.getFallbackUrl(FALLBACK_URL_ORDER_DETAIL_KEY, order.getOrderNumber()))
                    .send();

            return success("order.refunded");
        }
        else {
            orderCustomerService.addLog("system", "refund failure. error=" + failureMsg);
            orderCustomerService.update();
            return failure("order.refund.failure");
        }
    }

    @Before(Tx.class)
    public Ret updateOrder(Order order) {
        if (order == null || order.getId() == null) {
            return failure("order.invalid.order");
        }

        Order originOrder = Order.dao.findById(order.getId());
        User user = order.getUser();

        if (originOrder == null) {
            return failure("order.invalid.order");
        }

        Order.Status originStatus = Order.Status.valueOf(originOrder.getStatus());
        Order.Status targetStatus = Order.Status.valueOf(order.getStatus());
        logger.debug("origin status = {}, target status = {}", originStatus, targetStatus);
        if (!originStatus.transfer(targetStatus)) {
            return failure("order.status.transfer.error");
        }

        targetStatus.handle(order);
        order.update();
        createOrderProcessLog(order.getId(),
                String.format(ORDER_STATUS_CHANGE_CONTENT, order.getOrderNumber(), targetStatus.toChineseName()));


        //the order is closed. now notify the settlement process
        if (!originStatus.equals(targetStatus) && targetStatus == Order.Status.CLOSED_CONFIRMED) {

            List<OrderCustomerService> orderCustomerServices = order.getOrderCustomerService();
            orderCustomerServices.forEach(item -> {
                if (item.getServiceType().equals(OrderCustomerService.ServiceType.EXCHANGE.toString())
                        && item.getStatus().equals(OrderCustomerService.Status.DELIVERING.toString())) {
                    item.setStatus(OrderCustomerService.Status.EXCHANGED.toString());
                    updateCustomerService(item);
                }
            });

            Config config = Config.dao.findByKey(ORDER_RETURN_TIMEOUT_KEY);
            if (config == null || config.getValueToInt() == 0) {
                order.closedOrderNotify();
            }
        }

        //the order is paid. now notify the settlement process
        if (!originStatus.equals(targetStatus) && targetStatus == Order.Status.PAID_CONFIRM_PENDING) {
            order.paidOrderNotify();
            new OrderCreatedNotification(order.getUser().getWeixin())
                    .param(OrderCreatedNotification.ORDER_NUMBER, order.getOrderNumber())
                    .param(OrderCreatedNotification.ORDER_PRICE, getOrderTotalPrice(order))
                    .param(OrderCreatedNotification.CONTACT_USER, order.getContactUser())
                    .param(OrderCreatedNotification.CONTACT_PHONE, order.getPhone())
                    .param(OrderCreatedNotification.CONTACT_ADDRESS, order.getAddress())
                    .setUrl(ConfigUtils.getFallbackUrl(FALLBACK_URL_ORDER_DETAIL_KEY, order.getOrderNumber()))
                    .send();
        }

        //the order is canceled.
        if (!originStatus.equals(targetStatus) && targetStatus == Order.Status.CLOSED_CANCELED) {
            String note = String.format("用户取消订单号 %s，回退库存", order.getOrderNumber());
            Long warehouseId = StoreUtil.getWarehouseId(order.getStoreId());
            decreaseProductSales(order, note, warehouseId);
            logger.debug("orderNumber {} rollback the credit = {}", order.getOrderNumber(), order.getPayCredit());
            rollbackVipCredit(order.getUser().getLoginName(),  order.getPayCredit());
            resetVipCoupon(order.getExtCouponId(), order.getUserId().toString(), order.getOrderNumber());
            new OrderCanceledNotification(order.getUser().getWeixin())
                    .param(OrderCanceledNotification.ORDER_NUMBER, order.getOrderNumber())
                    .param(OrderCanceledNotification.ORDER_PRICE, getOrderTotalPrice(order))
                    .param(OrderCanceledNotification.CANCELED_TIME, DateKit.today(DATE_TIME_FORMAT))
                    .setUrl(ConfigUtils.getFallbackUrl(FALLBACK_URL_ORDER_DETAIL_KEY, order.getOrderNumber()))
                    .send();
        }

        //the order is pay timeout.
        if (!originStatus.equals(targetStatus) && targetStatus == Order.Status.CLOSED_PAY_TIMEOUT) {
            String note = String.format("用户订单号 %s 支付超时关闭，回退库存", order.getOrderNumber());
            Long warehouseId = StoreUtil.getWarehouseId(order.getStoreId());
            decreaseProductSales(order, note, warehouseId);
            logger.debug("orderNumber {} rollback the credit = {}", order.getOrderNumber(), order.getPayCredit());
            rollbackVipCredit(order.getUser().getLoginName(),  order.getPayCredit());
            resetVipCoupon(order.getExtCouponId(), order.getUserId().toString(), order.getOrderNumber());
            if(user != null) {
                new OrderPayTimeoutNotification(user.getWeixin())
                        .param(OrderPayTimeoutNotification.ORDER_NUMBER, order.getOrderNumber())
                        .setUrl(ConfigUtils.getFallbackUrl(FALLBACK_URL_ORDER_DETAIL_KEY, order.getOrderNumber()))
                        .send();
            }
        }

        if (!originStatus.equals(targetStatus) && targetStatus == Order.Status.CONFIRMED_DELIVER_PENDING) {
            order.deliverPendingOrderNotify();
        }

        /**
         *  delivering，delivered其实是一样的概念。都是发货了。
         *  delivering 表示已经通知快递了。
         * delivered 表示快递已经投递了。
         * 但我们现在没有和快递端对接，所以delivered是一样的。
         */

        //the order is delivering
        if (user != null && !originStatus.equals(targetStatus) && targetStatus == Order.Status.DELIVERING) {
            new OrderDeliveringNotification(user.getWeixin())
                    .param(OrderDeliveringNotification.ORDER_NUMBER, order.getOrderNumber())
                    .param(OrderDeliveringNotification.EXPRESS_COMPANY, order.getExpressCompany())
                    .param(OrderDeliveringNotification.EXPRESS_NUMBER, order.getExpressNumber())
                    .param(OrderDeliveringNotification.CONTACT_USER, order.getContactUser())
                    .setUrl(ConfigUtils.getFallbackUrl(FALLBACK_URL_ORDER_DETAIL_KEY, order.getOrderNumber()))
                    .send();
            order.deliveringOrderNotify();
        }


        return success("order.update.success");
    }

    private String getOrderTotalPrice(Order order) {
        String price = order.getTotalPrice().toPlainString() + PRICE_UNIT_CNY;
        if (Order.PaymentType.POINT.toString().equals(order.getPaymentType())) {
            price = order.getTotalPrice()
                    .multiply(BigDecimal.valueOf(order.getPointExchangeRate()))
                    .divide(BigDecimal.ONE, 0, BigDecimal.ROUND_HALF_UP)
                    .toPlainString()
                    + PRICE_UNIT_POINT;
        }
        return price;
    }

    private void increaseProductSales(Order order, String note, Long warehouseId) throws StockBalanceException {
        logger.debug("lock stock balance and sales for order {}", order.getOrderNumber());
        List<Integer> productIds = new ArrayList<>();
        List<Integer> specificationIds = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            productIds.add(orderItem.getProductId());
            specificationIds.add(orderItem.getProductSpecificationId());
            quantities.add(orderItem.getQuantity());
        }
        User user = order.getUser();
        productService.increaseProductSales(user.getId().longValue(), user.getLoginName(), order.getOrderNumber(),
                productIds, specificationIds, quantities, note, warehouseId);
    }

    /**
     * 库存回退
     *
     * @param order
     */
    private void decreaseProductSales(Order order, String note, Long warehouseId) {
        logger.debug("rollback stock balance and sales for order {}", order.getOrderNumber());
        try {
            List<Integer> productIds = Lists.newArrayList();
            List<Integer> specificationIds = Lists.newArrayList();
            List<Integer> quantities = Lists.newArrayList();
            for (OrderItem orderItem : order.getOrderItems()) {
                productIds.add(orderItem.getProductId());
                specificationIds.add(orderItem.getProductSpecificationId());
                quantities.add(orderItem.getQuantity());
            }
            User user = order.getUser();
            productService.decreaseProductSales(user.getId().longValue(), user.getLoginName(),
                    order.getOrderNumber(), productIds, specificationIds, quantities, note, warehouseId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.toString());
            for (StackTraceElement element : ex.getStackTrace()) {
                logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
            }
        }
    }

    /**
     * 发生退货时的库存回退
     *
     * @param orderCustomerService
     */
    public void decreaseRefundProductSales(Long userId, String account, OrderCustomerService orderCustomerService, String note, Long warehouseId) {
        logger.debug("rollback stock balance and sales for orderCustomerService {}", orderCustomerService.getServiceNumber());
        try {
            List<Integer> productIds = Lists.newArrayList();
            List<Integer> specificationIds = Lists.newArrayList();
            List<Integer> quantities = Lists.newArrayList();
            for (OrderCustomerServiceItem item : orderCustomerService.getReturns()) {
                productIds.add(item.getProductId());
                specificationIds.add(item.getProductSpecificationId());
                quantities.add(item.getQuantity());
            }
            productService.decreaseProductSales(userId, account,
                    orderCustomerService.getOrder().getOrderNumber(),
                    productIds, specificationIds, quantities, note, warehouseId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.toString());
            for (StackTraceElement element : ex.getStackTrace()) {
                logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
            }
        }
    }

    /**
     * 当订单状态是支付超时和取消时,或者已关闭订单,可以删除订单
     *
     * @param order
     * @return
     */
    public Ret deleteOrder(Order order) {
        if (order == null || order.getId() == null) {
            return failure("order.invalid.order");
        }
        Order.Status status = Order.Status.valueOf(order.getStatus());
        if (status == Order.Status.CLOSED_PAY_TIMEOUT
                || status == Order.Status.CLOSED_CANCELED
                || status == Order.Status.CLOSED_REFUNDED
                || status == Order.Status.CLOSED_CONFIRMED) {
            order.delete();
            logger.info("Order {} is fake deleted.", order.getOrderNumber());
            return success("order.delete.success");
        }
        return failure("order.delete.invalid.status");
    }

    /**
     * 提醒发货
     *
     * @param orderNumber
     */
    public void reminderOrderDeliver(String orderNumber) {
        Order order = Order.dao.findByOrderNumber(orderNumber);
        if (order != null
                && order.getIsDeliverReminder() == Order.NOT_DELIVER_REMINDER
                && (order.getStatus().equals(Order.Status.CONFIRMED_DELIVER_PENDING.toString()) || order.getStatus().equals(Order.Status.DELIVERING.toString()))) {
            order.setIsDeliverReminder(Order.DELIVER_REMINDER);
            order.update();
        }
    }

    public List<ShoppingCart> queryShoppingCartByProductId(int productId) {
        return ShoppingCart.dao.findByProductId(productId);
    }

    public List<ShoppingCart> queryShoppingCartByProductSpecificationId(int specificationId) {
        return ShoppingCart.dao.findByProductSpecificationId(specificationId);
    }

    /**
     * 查询某用户的购物车, 如果购物车的产品已下架, 那么就从购物车删除
     *
     * @param userId
     * @return
     */
    public List<ShoppingCart> queryShoppingCart(int userId) {
        List<ShoppingCart> list = ShoppingCart.dao.findByUserId(userId);
        ListIterator<ShoppingCart> iterator = list.listIterator();
        Contact contact = Contact.dao.findDefaultByUserId(userId);
        while (iterator.hasNext()) {
            ShoppingCart cart = iterator.next();
            Product product = Product.dao.findById(cart.getProductId());
            if (product == null || !product.getStatus().equals(Product.Status.ONSELL.toString())) {
                cart.delete();
                iterator.remove();
                continue;
            }
            Integer stockBalance = 0;
            if (cart.getProductSpecificationId() != null) {
                ProductSpecification productSpecification = ProductSpecification.dao.findById(cart.getProductSpecificationId());
                if (productSpecification != null && productSpecification.getStockBalance() != null) {
                    stockBalance = productSpecification.getStockBalance();
                }
            } else if (product.getStockBalance() != null) {
                stockBalance = product.getStockBalance();
            }
            cart.put("credit", product.getCredit());
            cart.put("allow_coupon", product.getAllowCoupon());
            cart.put("stock_balance", stockBalance);
            if (StrKit.notBlank(cart.getMarketing()) && cart.getMarketingId() != null) {
                if (contact != null) {
                    Marketing marketing = MarketingHolder.me().getMarketing(cart.getMarketing(),
                            cart.getMarketingId(),
                            userId,
                            contact.getProvince(),
                            contact.getCity(),
                            contact.getDistrict());
                    /**
                     * 有可能为null，此种情况是用户设置了默认配送区域，但此产品不配送到此地区，在这种情况下不应该抛出异常
                     * ，因为非批发类的购物车产品应该让用户看到，批发类的购物车产品也应该让用户看到，但用户看不到价格，且
                     *  看到提示信息
                     */
                    BigDecimal marketingPrice = marketing.getPrice();
                    cart.put("price", marketing.getPrice());
                    if (marketingPrice == null) {
                        cart.put("msg", String.format(DELIVERING_NOT_SUPPORTED, contact.getProvince() + contact.getCity() + contact.getDistrict()));
                    }
                } else {
                    cart.put("price", null);
                    cart.put("msg", String.format(NO_DEFAULT_CONTACT));
                }
            }
        }
        return list;
    }


    /**
     * 更新购物车，参数productIds， quantities 须一一对应。
     * quantity 为 0时，为删除购物车。
     * 购物车的价格只是显示用的，真正结算的时候还是要看下订单之后order item的价格
     *
     * @param userId
     * @param productIds
     * @param quantities
     * @param increase
     * @param marketingIds optional，若提供，则length必须等于productIds的length
     * @param marketings   optional，若提供，则length必须等于productIds的length
     * @return
     */
    public Ret updateShoppingCart(int userId, Integer[] productIds, Integer[] quantities, Integer[] specificationArray, boolean increase, Integer[] marketingIds, String[] marketings) {
        if (productIds.length != quantities.length ||
                productIds.length != specificationArray.length ||
                marketingIds != null && productIds.length != marketingIds.length ||
                marketings != null && productIds.length != marketings.length) {
            return failure("shopping_cart.is.empty");
        }
        if ((marketingIds != null && marketings == null) || (marketingIds == null && marketings != null)) {
            return failure("both.of.marketingIds.and.marketings.must.be.null.or.not.null");
        }

        List<ShoppingCart> currentCarts = ShoppingCart.dao.findByUserId(userId);
        for (int i = 0; i < productIds.length; i++) {
            boolean found = false;
            Integer productSpecificationId = specificationArray[i];
            Integer productId = productIds[i];
            Integer quantity = quantities[i];
            Integer marketingId = marketingIds == null ? null : marketingIds[i];
            String marketing = marketings == null ? null : marketings[i];
            for (ShoppingCart currentCart : currentCarts) {
                if (productId.equals(currentCart.getProductId())) {
                    Integer currentSpecificationId = currentCart.getProductSpecificationId();
                    if (currentSpecificationId == null || currentSpecificationId.equals(productSpecificationId)) {
                        if (quantity == 0) {
                            currentCart.delete();
                        } else {
                            if (increase) {
                                quantity += currentCart.getQuantity();
                            }
                            currentCart.setQuantity(quantity);
                            ProductSpecification productSpecification = ProductSpecification.dao.findById(productSpecificationId);
                            if (productSpecification != null && productSpecification.getProductId().equals(currentCart.getProductId())) {
                                //虽然此处这些值之前已经set过了，这里再set一次是为了保证是最新值
                                currentCart.setProductSpecificationId(productSpecificationId);
                                currentCart.setProductSpecificationName(productSpecification.getName());
                                currentCart.setPrice(productSpecification.getPrice());
                                currentCart.setWeight(productSpecification.getWeight());
                                currentCart.setBulk(productSpecification.getBulk());
                            }
                            if (marketingId != null && StrKit.notBlank(marketing)) {
                                currentCart.setMarketingId(marketingId);
                                currentCart.setMarketing(marketing);
                            }
                            currentCart.update();
                        }
                        found = true;
                    }
                }
            }
            if (!found) {
                Product product = Product.dao.findById(productIds[i]);
                if (product != null && product.isOnsell() && quantities[i] > 0) {
                    ShoppingCart cart = new ShoppingCart();
                    cart.setProductName(product.getName());
                    cart.setProductId(product.getId());
                    cart.setPrice(product.getPrice());
                    cart.setCover(product.getCover());
                    cart.setFareId(product.getFareId());
                    cart.setWeight(product.getWeight());
                    cart.setBulk(product.getBulk());
                    cart.setQuantity(quantities[i]);
                    cart.setUserId(userId);
                    ProductSpecification productSpecification = ProductSpecification.dao.findById(productSpecificationId);
                    if (productSpecification != null && productSpecification.getProductId().equals(product.getId())) {
                        cart.setProductSpecificationId(productSpecificationId);
                        cart.setProductSpecificationName(productSpecification.getName());
                        cart.setPrice(productSpecification.getPrice());
                        cart.setWeight(productSpecification.getWeight());
                        cart.setBulk(productSpecification.getBulk());
                    }
                    if (marketingId != null && StrKit.notBlank(marketing)) {
                        cart.setMarketingId(marketingId);
                        cart.setMarketing(marketing);
                        Contact contact = Contact.dao.findDefaultByUserId(userId);
                        Marketing mk = MarketingHolder.me().getMarketing(marketing,
                                marketingId,
                                userId,
                                contact.getProvince(),
                                contact.getCity(),
                                contact.getDistrict());
                        cart.setProductName(mk.getMarketingName());
                    }
                    cart.save();
                }
            }
        }

        return success("shopping_cart.update.success");
    }

    public Ret cleanShoppingCart(int userId) {
        new ShoppingCart().cleanup(userId);
        return success("shopping_cart.delete.success");
    }

    public Ret cleanShoppingCartDaysAgo(int days) {
        new ShoppingCart().cleanupByDate(DateKit.daysAgoStr(30, "yyyy-MM-dd HH:mm:ss"));
        return success();
    }

    /**
     * 申请售后服务
     *
     * @param order
     * @return
     */
    @Before(Tx.class)
    public Ret applyCustomerService(Order order,
                                    OrderCustomerService orderCustomerService,
                                    List<OrderCustomerServiceItem> orderCustomerServiceItems,
                                    List<OrderCustomerServiceItem> exchangeOrderCustomerServiceItems) {
        OrderCustomerService.ServiceType serviceType = OrderCustomerService.ServiceType.valueOf(orderCustomerService.getServiceType());
        if (orderCustomerServiceItems == null) {
            return failure("invalid.customer.service.item");
        }

        Ret ret;
        boolean alreadyRefund = orderCustomerServiceItems.stream().anyMatch(item -> {
            OrderItem orderItem = OrderItem.dao.findFirstByOrderIdProductIdProductSpecificationId(order.getId(),
                    item.getProductId(),
                    item.getProductSpecificationId());
            return orderItem.getStatus().equals(OrderItem.Status.REFUNDED.toString()) || orderItem.getStatus().equals(OrderItem.Status.REFUNDING.toString());
        });
        if (alreadyRefund) {
            return failure("already.refund");
        }

        if (!order.getStatus().equals(Order.Status.CANCELED_REFUND_PENDING.toString())
                && !order.getStatus().equals(Order.Status.CANCELED_RETURN_PENDING.toString())) {
            order.setPreviousStatus(order.getStatus());
        }
        if (serviceType == OrderCustomerService.ServiceType.REFUND) {
            order.setStatus(Order.Status.CANCELED_REFUND_PENDING.toString());
        } else {
            order.setStatus(Order.Status.CANCELED_RETURN_PENDING.toString());
        }
        order.setIsDeliverReminder(Order.NOT_DELIVER_REMINDER);
        ret = updateOrder(order);

        BigDecimal totalRefundFee = BigDecimal.valueOf(0);
        if (BaseService.isSucceed(ret)) {
            orderCustomerService.setOrderId(order.getId());
            orderCustomerService.save();

            ////////////////////// for legacy
            if (orderCustomerServiceItems.isEmpty()) {
                logger.debug("order customer service items is empty, it is legacy version. just set all orderItem to REFUNDING.");
                for (OrderItem orderItem : OrderItem.dao.findByOrderId(order.getId())) {
                    orderItem.setStatus(OrderItem.Status.REFUNDING.toString());
                    orderItem.update();
                }
                totalRefundFee = order.getTotalPrice();
            }
            else {
                //创建的是退货单/换货单
                 //退货单的各个退货项的refund_fee总和 或者 换货单中的退货清单中的各个退货项的refund_fee总和
                for (OrderCustomerServiceItem orderCustomerServiceItem : orderCustomerServiceItems) {
                    orderCustomerServiceItem.setOrderCustomerServiceId(orderCustomerService.getId());
                    orderCustomerServiceItem.setType(OrderCustomerServiceItem.Type.RETURN.toString());
                    totalRefundFee = totalRefundFee.add(orderCustomerServiceItem.getRefundFee());

                    OrderItem orderItem = OrderItem.dao.findFirstByOrderIdProductIdProductSpecificationId(order.getId(),
                            orderCustomerServiceItem.getProductId(),
                            orderCustomerServiceItem.getProductSpecificationId());
                    orderItem.setStatus(OrderItem.Status.REFUNDING.toString());
                    orderItem.update();
                }
                Db.batchSave(orderCustomerServiceItems, 10);
            }

            if (orderCustomerServiceItems.size() == order.getOrderItems().size()) {
                logger.info("customer service items size equal orderitems size, add freight to refund fee. {}",
                        orderCustomerService.getServiceNumber());
                totalRefundFee = totalRefundFee.add(order.getFreight());
            }

            //更新退货单/换货单中的退货清单的退款总额（如果是换货单，稍后此refund_fee会被覆盖）
            orderCustomerService.setRefundFee(totalRefundFee);
            orderCustomerService.setSupplementaryFee(BigDecimal.ZERO);
            orderCustomerService.update();

            //创建的是换货单
            if (exchangeOrderCustomerServiceItems != null && !exchangeOrderCustomerServiceItems.isEmpty()) {
                BigDecimal totalExchangeFee = BigDecimal.valueOf(0);
                for (OrderCustomerServiceItem exchangeOrderCustomerServiceItem : exchangeOrderCustomerServiceItems) {
                    exchangeOrderCustomerServiceItem.setOrderCustomerServiceId(orderCustomerService.getId());
                    exchangeOrderCustomerServiceItem.setType(OrderCustomerServiceItem.Type.EXCHANGE.toString());
                    totalExchangeFee = totalExchangeFee.add(exchangeOrderCustomerServiceItem.getFinalPrice());
                }
                Db.batchSave(exchangeOrderCustomerServiceItems, 10);

                BigDecimal delta = totalExchangeFee.subtract(totalRefundFee);
                int compareResult = delta.compareTo(BigDecimal.ZERO);

                if (orderCustomerService.getSupplementaryFee() == null) {
                    if (compareResult >= 0) {
                        orderCustomerService.setRefundFee(BigDecimal.ZERO);
                        orderCustomerService.setSupplementaryFee(delta);
                    } else {
                        orderCustomerService.setRefundFee(BigDecimal.ZERO.subtract(delta));
                        orderCustomerService.setSupplementaryFee(BigDecimal.ZERO);
                    }
                }
                orderCustomerService.update();
            }

            logger.debug("submit async task to auto refund.");
            AsyncRefundCustomerServiceOrderTask task = new AsyncRefundCustomerServiceOrderTask(this, orderCustomerService);
            AsyncTaskKit.submit(task);

            //订单售后服务（即退款/退货）申请成功后发送消息
            new OrderServiceCreatedNotification(order.getUser().getWeixin())
                    .param(OrderServiceCreatedNotification.ORDER_NUMBER, order.getOrderNumber())
                    .param(OrderServiceCreatedNotification.ORDER_PRICE, order.getTotalPrice().toPlainString())
                    .setUrl(ConfigUtils.getFallbackUrl(FALLBACK_URL_ORDER_DETAIL_KEY, order.getOrderNumber()))
                    .send();
            return success("order.customer.service.created");
        }

        return failure(BaseService.getMessage(ret));
    }

    public Ret updateCustomerService(OrderCustomerService orderCustomerService) {
        OrderCustomerService originalOrderCustomerService = OrderCustomerService.dao.findById(orderCustomerService.getId());
        logger.debug("original customer service order is : {}", JsonKit.toJson(originalOrderCustomerService));
        OrderCustomerService.Status originalStatus = OrderCustomerService.Status.CREATED;
        if (originalOrderCustomerService != null) {
            originalStatus = OrderCustomerService.Status.valueOf(originalOrderCustomerService.getStatus());
        }
        OrderCustomerService.Status targetStatus = OrderCustomerService.Status.valueOf(orderCustomerService.getStatus());
        if (originalStatus.transfer(targetStatus)) {
            targetStatus.handle(orderCustomerService);
            orderCustomerService.update();

            // update order item status after customer service status changed.
            if (targetStatus == OrderCustomerService.Status.REFUND_PENDING || targetStatus == OrderCustomerService.Status.RETURN_PENDING) {
                updateOrderItemStatus(orderCustomerService, OrderItem.Status.REFUNDING);
            }
            else if (targetStatus == OrderCustomerService.Status.REFUNDED || targetStatus == OrderCustomerService.Status.EXCHANGED) {
                updateOrderItemStatus(orderCustomerService, OrderItem.Status.REFUNDED);
            }
            else {
                updateOrderItemStatus(orderCustomerService, OrderItem.Status.CREATED);
            }

            return success("order.customer.service.updated");
        }

        return failure("order.customer.service.invalid.status.transfer");
    }

    /**
     * 同意售后服务单
     * @param orderCustomerService
     * @param adminUserName
     * @return
     */
    public Ret agreeCustomerService(OrderCustomerService orderCustomerService, String adminUserName) {
        logger.debug("agree customer service {}", orderCustomerService.getServiceNumber());
        OrderCustomerService.ServiceType serviceType = OrderCustomerService.ServiceType.valueOf(orderCustomerService.getServiceType());
        if (serviceType == OrderCustomerService.ServiceType.REFUND) {
            orderCustomerService.setStatus(OrderCustomerService.Status.REFUND_PENDING.toString());
        }
        else {
            orderCustomerService.setStatus(OrderCustomerService.Status.RETURN_PENDING.toString());
        }
        String content = "同意";
        orderCustomerService.addLog(adminUserName, content);
        orderCustomerService.setResult(content);
        this.updateCustomerService(orderCustomerService);

        // 回退库存
        Order order = orderCustomerService.getOrder();
        Long warehouseId = StoreUtil.getWarehouseId(order.getStoreId());
        User user = order.getUser();
        String note = String.format("用户 %s 订单号 %s 退货单号 %s，回退库存", user.getName(), order.getOrderNumber(), orderCustomerService.getServiceNumber());
        this.decreaseRefundProductSales(user.getId().longValue(), user.getLoginName(), orderCustomerService, note, warehouseId);

        return success("order.customer.service.agree");
    }

    /**
     * 不同意售后
     * @param orderCustomerService
     * @param adminUserName
     * @param content
     * @return
     */
    public Ret disagreeCustomerService(OrderCustomerService orderCustomerService, String adminUserName, String content) {
        orderCustomerService.setStatus(OrderCustomerService.Status.CANCELED.toString());
        orderCustomerService.addLog(adminUserName, "不同意");
        orderCustomerService.setResult(content);
        this.updateCustomerService(orderCustomerService);

        Order order = orderCustomerService.getOrder();
        if (order.getOrderCustomerService().stream()
                .filter(item -> !item.getId().equals(orderCustomerService.getId()))
                .noneMatch(item -> {
                    OrderCustomerService.Status status = OrderCustomerService.Status.valueOf(item.getStatus());
                    return (status == OrderCustomerService.Status.REFUND_PENDING
                            || status == OrderCustomerService.Status.RETURN_PENDING
                            || status == OrderCustomerService.Status.CREATED
                    );
                })) {
            logger.debug("no other customer service order under REFUND_PENDING or RETURN_PENDING. revert order status to {}", order.getPreviousStatus());
            order.setStatus(order.getPreviousStatus());
            this.updateOrder(order);
        }
        return success("order.customer.service.disagree");
    }

    /**
     * 退货收到确认
     * @param orderCustomerService
     * @param adminUserName
     * @param warehouseId
     * @return
     */
    public Ret returnedCustomerService(OrderCustomerService orderCustomerService, String adminUserName, Long warehouseId) {

        Order order = orderCustomerService.getOrder();

        OrderCustomerService.ServiceType serviceType = OrderCustomerService.ServiceType.valueOf(orderCustomerService.getServiceType());
        if (serviceType == OrderCustomerService.ServiceType.RETURN) {
            orderCustomerService.setStatus(OrderCustomerService.Status.REFUND_PENDING.toString());
            order.setStatus(Order.Status.CANCELED_REFUND_PENDING.toString());
        }
        else if (serviceType == OrderCustomerService.ServiceType.EXCHANGE) {
            orderCustomerService.setStatus(OrderCustomerService.Status.DELIVERING.toString());
            order.setStatus(Order.Status.DELIVERED_CONFIRM_PENDING.toString());
        }
        orderCustomerService.addLog(adminUserName, "已收到退货产品");
        Ret ret = this.updateCustomerService(orderCustomerService);
        logger.debug("customer service order update ret={}", ret);
        if (BaseService.isSucceed(ret)) {
            ret = this.updateOrder(order);
            logger.debug("order update ret={}", ret);

            User user = order.getUser();
            String note = String.format("用户 %s 订单号 %s 退货单号 %s，回退库存", user.getName(), order.getOrderNumber(), orderCustomerService.getServiceNumber());
            this.decreaseRefundProductSales(user.getId().longValue(), user.getLoginName(), orderCustomerService, note, warehouseId);
        }
        return success("order.customer.service.returned");
    }

    private void updateOrderItemStatus(OrderCustomerService orderCustomerService, OrderItem.Status status) {
        ////////////////////// for legacy
        if (orderCustomerService.getReturns().isEmpty()) {
            logger.debug("order customer service items is empty, it is legacy version. just set all orderItem to REFUNDED.");
            for (OrderItem orderItem : OrderItem.dao.findByOrderId(orderCustomerService.getOrderId())) {
                orderItem.setStatus(OrderItem.Status.REFUNDED.toString());
                orderItem.update();
            }
            return;
        }
        orderCustomerService.getReturns().forEach(item -> {
            OrderItem orderItem = OrderItem.dao.findFirstByOrderIdProductIdProductSpecificationId(orderCustomerService.getOrderId(),
                    item.getProductId(),
                    item.getProductSpecificationId());
            orderItem.setStatus(status.toString());
            orderItem.update();
        });
    }

    public Ret handleOrderExpired(int orderId) {
        logger.debug("handling Order Expired,orderId = {}", orderId);
        Order order = Order.dao.findById(orderId);
        if (order == null) {
            logger.debug("order not found by id {}", orderId);
            return failure("order.not.found");
        }
        if (Order.Status.CREATED_PAY_PENDING.toString().equals(order.getStatus())) {
            order.setStatus(Order.Status.CLOSED_PAY_TIMEOUT.toString());
            Ret ret = updateOrder(order);
            logger.debug("Ret = {}", ret);
            return ret;
        }
        return success();
    }



    ///////////////////////// implement OrderPayService. 获取订单数据
    @Override
    public Map<String, Object> retrieveToPayOrder(String orderNumber) throws RetrieveOrderException {
        Order order = Order.dao.findByOrderNumber(orderNumber);
        logger.error("order-info :{}",order.toJson());
        if (order == null) {
            logger.error("order not found. orderNumber = {}", orderNumber);
            throw new RetrieveOrderException("order not found. orderNumber = " + orderNumber);
        }
        if (order.getStatus().equals(Order.Status.CREATED_PAY_PENDING.toString())) {
            Map<String, Object> result = new HashMap<>();
            for (Map.Entry<String, Object> entry : order._getAttrsEntrySet()) {
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }
        logger.error("invalid order status {} for orderNumber {}", order.getStatus(), orderNumber);
        throw new RetrieveOrderException("invalid order status " + order.getStatus() + " for orderNumber {}" + orderNumber);
    }

    /**
     * 微信支付支付成功通知
     * @param orderNumber
     * @param paymentType
     * @param tradeNumber
     * @param payAccount 支付帐户，对于微信，就是openid
     * @throws RetrieveOrderException
     */
    @Override
    public void paidNotify(String orderNumber, String paymentType, String tradeNumber, String payAccount) throws RetrieveOrderException {
        Order order = Order.dao.findByOrderNumber(orderNumber);
        if (order == null) {
            throw new RetrieveOrderException("order not found. orderNumber = " + orderNumber);
        }
        // 线下订单，如果用户使用微信支付，那么需要把该订单挂到该用户下面。
        if (StrKit.notBlank(payAccount) && order.getUserId().toString().equals(order.getStoreUserId())) {
            User user = User.dao.findByWeixin(payAccount);
            if (user != null) {
                order.setUserId(user.getId());
            }
        }
        order.setStatus(Order.Status.PAID_CONFIRM_PENDING.toString());
        order.setPaymentType(paymentType);
        order.setTradeNumber(tradeNumber);
        updateOrder(order);
    }
    /////////////////////////

    private void resetVipCoupon(String couponId, String userId, String orderNumber) {
        logger.info("attempting reset vip coupon. couponId = {}, userId = {}, orderNumber = {}", couponId, userId, orderNumber);
        if (StrKit.isBlank(couponId)) {
            logger.info("not couponid found. ignore.");
            return;
        }
        try {
            BasePlugin vipPlugin = ExtPluginHolder.me().get(VipPlugin.class);
            if (vipPlugin.isEnabled()) {
                CouponApi couponApi = new CouponApi();
                couponApi.resetCoupon(couponId, userId, orderNumber);
                logger.info("vip coupon reset. couponId = {}, userId = {}, orderNumber = {}", couponId, userId, orderNumber);
            }
        }
        catch (Exception ex) {
            logger.error("reset coupon error: ", ex.getMessage());
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }

    /**
     * 回退会员积分
     * @param account
     * @param credit
     */
    private void rollbackVipCredit(String account, int credit) {
        try {
            BasePlugin vipPlugin = ExtPluginHolder.me().get(VipPlugin.class);
            if (vipPlugin.isEnabled()) {
                CreditApi creditApi = new CreditApi();
                creditApi.refundAccountCredit(account, credit);
            }
        }
        catch (Exception ex) {
            logger.error("update credit error: ", ex.getMessage());
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }

    /**
     * 更新会员积分
     * @param account
     * @param amount
     */
    public void updateCreditConsume(String account, BigDecimal amount) {
        logger.debug("update credit account = {}, amount = {}", account, amount);
        try {
            if (amount.compareTo(BigDecimal.ZERO) > 0 && ExtPluginHolder.me().get(VipPlugin.class).isEnabled()) {
                logger.debug("vip plugin is enabled.");
                CreditApi creditApi = new CreditApi();
                ApiResult apiResult = creditApi.updateAccountCreditConsume(account, amount);
                logger.debug("update credit result : {}", apiResult.getJson());
            }
        }
        catch (Exception ex) {
            logger.error("update credit error: ", ex.getMessage());
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }

    /**
     * 更新会员成长值
     * @param account
     * @param amount
     */
    public void updatePointConsume(String account, BigDecimal amount) {
        logger.debug("update point account = {}, amount = {}", account, amount);
        try {
            if (amount.compareTo(BigDecimal.ZERO) > 0 && ExtPluginHolder.me().get(VipPlugin.class).isEnabled()) {
                logger.debug("vip plugin is enabled.");
                PointApi pointApi = new PointApi();
                ApiResult apiResult = pointApi.updateAccountPoint(account, amount);
                logger.debug("update point result : {}", apiResult.getJson());
            }
        }
        catch (Exception ex) {
            logger.error("update point error: ", ex.getMessage());
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                logger.error("   {}:{}#{}", stackTraceElement.getFileName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }
}
