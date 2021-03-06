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

package com.jfeat.order.api.validator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.StorePlugin;
import com.jfeat.ext.plugin.WmsPlugin;
import com.jfeat.ext.plugin.store.StoreApi;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.ext.plugin.wms.WmsApi;
import com.jfeat.ext.plugin.wms.services.domain.model.Inventory;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.marketing.CheckResult;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.MarketingHolder;
import com.jfeat.member.model.Coupon;
import com.jfeat.order.api.model.OrderEntity;
import com.jfeat.order.model.Order;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;
import com.jfeat.product.purchase.ProductPurchaseEvaluation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.validate.Validator;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jacky on 2/2/16.
 */
public class OrderValidator extends Validator {

    private static final Logger logger = LoggerFactory.getLogger(OrderValidator.class);

    @Override
    protected void validate(Controller controller) {
        setShortCircuit(true);
        RestController restController = (RestController) controller;
        OrderEntity orderEntity = restController.getPostJson(OrderEntity.class);
        Map<Integer, Product> products = new HashMap<>();

        Subject currentUser = SecurityUtils.getSubject();
        ShiroUser shiroUser = (ShiroUser) currentUser.getPrincipal();
        Integer userId = shiroUser.id;
        String account = shiroUser.loginName;

        if (orderEntity.getCoupon_id() != null) {
            Coupon coupon = Coupon.dao.findById(orderEntity.getCoupon_id());
            if (coupon != null && !coupon.getStatus().equals(Coupon.Status.ACTIVATION.toString())) {
                addError("error", "??????????????????????????????");
            }
            restController.setAttr("coupon", coupon);
        }

        //?????????????????????EXPRESS????????????
        Order.DeliveryType deliveryType = Order.DeliveryType.EXPRESS;
        if (StrKit.notBlank(orderEntity.getDelivery_type())) {
            deliveryType = Order.DeliveryType.valueOf(orderEntity.getDelivery_type());
        }
        //????????????????????????, ?????????????????????ipad????????????????????????????????????store_id
        if (StrKit.isBlank(orderEntity.getStore_id()) && deliveryType == Order.DeliveryType.SELF_PICK) {
            addError("error", "?????????????????? - ???????????????????????????????????????????????????");
            return;
        }
        if ("/rest/order/update".equals(getActionKey())) {
            Order order = Order.dao.findByOrderNumber(controller.getPara());
            if (order == null || !userId.equals(order.getUserId())) {
                addError("error", "????????????");
            }
            restController.setAttr("order", order);
        }

        //????????????????????????contact??????????????????????????????
        if ("/rest/order/save".equals(getActionKey()) && deliveryType != Order.DeliveryType.SELF_PICK) {
            OrderEntity.ContactEntity contactEntity = orderEntity.getContact();
            if (contactEntity == null ||
                    StrKit.isBlank(contactEntity.getProvince()) ||
                    StrKit.isBlank(contactEntity.getCity()) ||
                    StrKit.isBlank(contactEntity.getDistrict()) ||
                    StrKit.isBlank(contactEntity.getContact_user()) ||
                    StrKit.isBlank(contactEntity.getPhone())) {
                addError("error", "?????????????????????");
            }
        }

        if (orderEntity.getOrder_items() != null) {
            if (orderEntity.getOrder_items().isEmpty()) {
                addError("error", "???????????????");
            }

            boolean isMarketing = StrKit.notBlank(orderEntity.getMarketing());
            Map<Integer, Marketing> marketings = Maps.newHashMap();

            Map<Integer, Integer> productIdQuantity = Maps.newHashMap();

            //????????????????????????????????????order_items??????????????????????????????????????????1??????????????? ??????????????????????????????????????????
            //????????????????????????????????????
            for (OrderEntity.OrderItemsEntity itemsEntity : orderEntity.getOrder_items()) {
                Integer productId = itemsEntity.getProduct_id();
                productIdQuantity.merge(productId, itemsEntity.getQuantity(), (a, b) -> a + b);
            }

            //??????????????????????????????????????????????????? ??????????????????????????????????????????????????????????????? ????????????
            BigDecimal falseTotalPrice = BigDecimal.ZERO;
            boolean isMarketingOrder = false;  //???????????????????????????

            Long storeId = StrKit.notBlank(orderEntity.getStore_id()) ? Long.parseLong(orderEntity.getStore_id()) : null;
            Long warehouseId = null;
            if (ExtPluginHolder.me().get(StorePlugin.class).isEnabled() && storeId != null) {
                StoreApi storeApi = new StoreApi();
                Store store = storeApi.getStore(storeId);
                warehouseId = store.getWarehouseId();
            }
            restController.setAttr("warehouseId", warehouseId);

            for (OrderEntity.OrderItemsEntity itemEntity : orderEntity.getOrder_items()) {
                Product product = Product.dao.findById(itemEntity.getProduct_id());
                if (product == null) {
                    addError("error", "???????????????");
                    return;
                }

                if (!Product.Status.valueOf(product.getStatus()).equals(Product.Status.ONSELL)) {
                    addError("error", "?????? " + product.getShortName() + " ?????????");
                    return;
                }

                // ?????????????????????????????????
                if (isRegionLimit(product.getRegion(), orderEntity.getContact())) {
                    addError("error", "??????" + product.getShortName() + "???????????????????????????.");
                    return;
                }

                boolean wmsPluginEnabled = ExtPluginHolder.me().get(WmsPlugin.class).isEnabled();
                //??????wmsPlugin?????????????????????????????????wmsPlugin??????api???????????????????????????
                if (product.getIsVirtual() == Product.Virtual.NO.getValue() && wmsPluginEnabled) {
                    ProductSpecification productSpecification = ProductSpecification.dao.findById(itemEntity.getProduct_specification_id());
                    if (productSpecification != null) {
                        if (productSpecification.getSkuId() == null) {
                            addError("error", String.format("??????????????????????????????%s %s????????????SKU???", product.getName(), productSpecification.getName()));
                            return;
                        }
                        WmsApi wmsApi = new WmsApi();
                        List<Inventory> inventories = wmsApi.getInventory(userId.longValue(), account, Long.parseLong(productSpecification.getSkuId()), warehouseId).getRecords();
                        if (inventories == null || inventories.isEmpty() || itemEntity.getQuantity() > inventories.get(0).getValidSku()) {
                            addError("error", String.format("??????????????????????????????%s %s??????????????????", product.getName(), productSpecification.getName()));
                            return;
                        }
                    } else {
                        if (product.getSkuId() == null) {
                            addError("error", String.format("??????????????????????????????%s????????????SKU???", product.getName()));
                            return;
                        }
                        WmsApi wmsApi = new WmsApi();
                        List<Inventory> inventories = wmsApi.getInventory(userId.longValue(), account, Long.parseLong(product.getSkuId()), warehouseId).getRecords();
                        if (inventories == null || inventories.isEmpty() || itemEntity.getQuantity() > inventories.get(0).getValidSku()) {
                            addError("error", String.format("??????????????????????????????%s??????????????????", product.getName()));
                            return;
                        }
                    }
                }
                //????????????????????????????????????
                if (product.getIsVirtual() == Product.Virtual.YES.getValue() || !wmsPluginEnabled) {
                    ProductSpecification productSpecification = ProductSpecification.dao.findById(itemEntity.getProduct_specification_id());
                    if (productSpecification != null
                            && (productSpecification.getStockBalance() <= 0 || itemEntity.getQuantity() > productSpecification.getStockBalance())) {
                        addError("error", "?????? " + product.getShortName() + productSpecification.getName() + " ????????????");
                    }
                    if (productSpecification == null
                            && (product.getStockBalance() <= 0 || itemEntity.getQuantity() > product.getStockBalance())) {
                        addError("error", "?????? " + product.getShortName() + " ????????????");
                    }
                    products.put(product.getId(), product);
                }

                ProductPurchaseEvaluation evaluation = new ProductPurchaseEvaluation();
                boolean canPurchase = evaluation.evaluate(itemEntity.getProduct_id(), userId, productIdQuantity.get(itemEntity.getProduct_id()));
                if (!canPurchase) {
                    addError("error", evaluation.getLastError());
                }

                if (isMarketing && orderEntity.getContact() == null) {
                    addError("error", "???????????????????????????????????????");
                }

                if (isMarketing) {
                    Marketing marketing = MarketingHolder.me().getMarketing(orderEntity.getMarketing(),
                            itemEntity.getMarketing_id(),
                            userId,
                            orderEntity.getContact().getProvince(),
                            orderEntity.getContact().getCity(),
                            orderEntity.getContact().getDistrict());
                    if (marketing == null) {
                        addError("error", "?????????????????????");
                    }
                    if (!marketing.available(Lists.newArrayList(itemEntity.getProduct_id()),
                            Lists.newArrayList(itemEntity.getProduct_specification_id()),
                            Lists.newArrayList(itemEntity.getQuantity()))) {
                        String error = StrKit.notBlank(marketing.getErrorMessage()) ? marketing.getErrorMessage() : "?????????????????????";
                        addError("error", error);
                    }

                    marketings.put(itemEntity.getMarketing_id(), marketing);
                    isMarketingOrder = true;
                    falseTotalPrice = falseTotalPrice.add(marketing.getPrice().multiply(new BigDecimal(itemEntity.getQuantity())));
                }
            }

            if (isMarketingOrder) {
                Marketing mk = MarketingHolder.me().getMarketing(orderEntity.getMarketing(),
                        orderEntity.getOrder_items().get(0).getMarketing_id(),
                        userId,
                        orderEntity.getContact().getProvince(),
                        orderEntity.getContact().getCity(),
                        orderEntity.getContact().getDistrict());
                CheckResult checkResult = mk.checkOrderRequest(falseTotalPrice);
                if (!checkResult.isResult()) {
                    addError("error", checkResult.getMessage());
                }
            }
            restController.setAttr("marketings", marketings);
        }
        restController.setAttr("products", products);
    }

    private boolean isRegionLimit(String productRegion, OrderEntity.ContactEntity contactEntity) {
        logger.debug("region check: productRegion={}, contact={}", productRegion, contactEntity);
        if (StrKit.isBlank(productRegion) || contactEntity == null) {
            logger.debug("product region is not defined or contact entity is null, return not region limited.");
            return false;
        }
        List<String> productRegionList = Arrays.asList(productRegion.split(","));
        String userRegion = contactEntity.getProvince() + "-" + contactEntity.getCity();
        // check province
        if (productRegionList.contains(contactEntity.getProvince())) {
            logger.debug("user contact province is contained in product region, return not region limited.");
            return false;
        }
        if (productRegionList.contains(userRegion)) {
            logger.debug("user contact province-city is contained in product region, return not region limited.");
            return false;
        }
        // it is limited.
        logger.debug("user is region limited.");
        return true;
    }

    @Override
    protected void handleError(Controller controller) {
        RestController restController = (RestController) controller;
        restController.renderFailure(controller.getAttr("error"));
    }
}
