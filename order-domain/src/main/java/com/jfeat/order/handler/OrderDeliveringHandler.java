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

package com.jfeat.order.handler;

import com.jfeat.config.model.Config;
import com.jfeat.ext.plugin.BasePlugin;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.JsonKit;
import com.jfeat.ext.plugin.WmsPlugin;
import com.jfeat.ext.plugin.wms.WmsApi;
import com.jfeat.ext.plugin.wms.services.domain.model.AffectedApiResult;
import com.jfeat.identity.model.User;
import com.jfeat.observer.Observer;
import com.jfeat.observer.Subject;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderItem;
import com.jfeat.order.service.OrderService;
import com.jfeat.product.model.Product;
import com.jfeat.product.model.ProductSpecification;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackyhuang on 2018/1/15.
 */
public class OrderDeliveringHandler implements Observer {

    private static Logger logger = LoggerFactory.getLogger(OrderDeliveringHandler.class);
    private static final String AUTO_DELIVER_ORDER_KEY = "mall.auto_deliver_order";

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    @Override
    public void invoke(Subject subject, int event, Object param) {
        if (subject instanceof Order && event == Order.EVENT_ORDER_DELIVERING) {
            Order order = Order.dao.findById(((Order) subject).getId());
            logger.debug("handling delivering order {}.", order);

            BasePlugin wmsPlugin = ExtPluginHolder.me().get(WmsPlugin.class);
            logger.debug("wms enabled = {}", wmsPlugin.isEnabled());
            if(wmsPlugin.isEnabled()) {
                logger.debug("WmsPlugin is enabled, notify the delivered to update SKU");
                User user = order.getUser();
                List<OrderItem> orderItemList = order.getOrderItems();
                List<Long> skuIds = new ArrayList<>();
                List<Integer> amounts = new ArrayList<>();
                List<Long> warehouseIds = new ArrayList<>();
                for (OrderItem orderItem : orderItemList) {
                    if (StrKit.notBlank(orderItem.getSkuId())) {
                        skuIds.add(Long.valueOf(orderItem.getSkuId()));
                        Long warehouseId = null;
                        if (orderItem.getWarehouseId() != null && !"null".equalsIgnoreCase(orderItem.getWarehouseId())) {
                            warehouseId = Long.valueOf(orderItem.getWarehouseId());
                        }
                        warehouseIds.add(warehouseId);
                        amounts.add(orderItem.getQuantity());
                    }
                }

                logger.debug("skuids = {}, warehouseIds = {}", JsonKit.toJson(skuIds), JsonKit.toJson(warehouseIds));

                if (!skuIds.isEmpty()) {
                    WmsApi wmsApi = new WmsApi();
                    AffectedApiResult result = wmsApi.deliveredNotify(user.getId().longValue(),
                            user.getLoginName(),
                            user.getName(),
                            order.getOrderNumber(),
                            skuIds.toArray(new Long[0]),
                            amounts.toArray(new Integer[0]),
                            warehouseIds.toArray(new Long[0]),
                            null);
                    logger.debug("result = {}", JsonKit.toJson(result));
                }
            }

            try {
                Config config = Config.dao.findByKey(AUTO_DELIVER_ORDER_KEY);
                if (config != null && config.getValueToBoolean()) {
                    logger.debug("auto deliver order, going to change status to DELIVERED_CONFIRM_PENDING, orderNumber = {}", order.getOrderNumber());
                    Order.Status targetStatus = Order.Status.DELIVERED_CONFIRM_PENDING;
                    order.setStatus(targetStatus.toString());
                    Ret ret = orderService.updateOrder(order);
                    logger.debug("Order {}: change status to {}, result = {}", order.getOrderNumber(), targetStatus, ret.getData());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex.getMessage());
                logger.error(ex.toString());
                for (StackTraceElement element : ex.getStackTrace()) {
                    logger.error("    {}:{} {}", element.getFileName(), element.getLineNumber(), element.getMethodName());
                }
            }
        }
    }
}
