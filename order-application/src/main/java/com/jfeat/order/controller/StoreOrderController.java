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

package com.jfeat.order.controller;

import com.google.common.collect.Lists;
import com.jfeat.core.BaseController;
import com.jfeat.ext.plugin.BasePlugin;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.WmsPlugin;
import com.jfeat.ext.plugin.wms.WmsApi;
import com.jfeat.ext.plugin.wms.services.domain.model.QueryWarehouseResult;
import com.jfeat.flash.Flash;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.MarketingHolder;
import com.jfeat.order.model.Express;
import com.jfeat.order.model.Order;
import com.jfeat.order.model.OrderItem;
import com.jfeat.order.model.OrderProcessLog;
import com.jfeat.order.model.param.OrderParam;
import com.jfeat.order.service.OrderService;
import com.jfeat.pcd.model.Pcd;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.render.excel.PoiRender;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;

/**
 * Created by jacky on 3/11/16.
 */
public class StoreOrderController extends BaseController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    private static String[] EXPORT_COLUMNS = {
            "order_number", "status", "created_date", "payment_type", "user_id", "user_name", "user_nickname",
            "contact_user", "address", "phone", "actual_price", "total_price",
            "product_name", "product_id", "product_specification_name",
            "price", "quantity", "final_price", "remark", "barcode", "store_location",
            "mid", "mname"
    };

    @Override
    @RequiresPermissions(value = { "order.view", "store_order.menu" }, logical = Logical.OR)
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String mname = getPara("mname");
        String marketing = getPara("marketing");
        String status = getPara("status");
        String uid = getPara("uid");
        String orderNumber = getPara("orderNumber");
        String contactUser = getPara("contactUser");
        String phone = getPara("phone");
        String paymentType = getPara("paymentType");
        String deliveryType = getPara("deliveryType");
        Integer reminder = getParaToInt("reminder");
        String startTime = getPara("startTime");
        String endTime = getPara("endTime");
        Boolean export = getParaToBoolean("export");
        String type = Order.Type.STORE_ORDER.toString();
        if (StrKit.notBlank(startTime)) {
            startTime += " 00:00:00";
        }
        if (StrKit.notBlank(endTime)) {
            endTime += " 23:59:59";
        }

        Integer userId = null;
        if (StrKit.notBlank(uid)) {
            User user = User.dao.findByUid(uid);
            if (user != null) {
                userId = user.getId();
            } else {
                userId = 0;
            }
        }
        OrderParam orderParam = new OrderParam(pageNumber, pageSize);
        orderParam.setType(type)
                .setUserId(userId)
                .setOrderNumber(orderNumber)
                .setStatus(status)
                .setContactUser(contactUser)
                .setPhone(phone)
                .setReminder(reminder)
                .setPaymentType(paymentType)
                .setDeliveryType(deliveryType)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setMarketing(marketing)
                .setMname(mname)
                .setShowDeleted(true);
        if (export != null) {
            String[] idStr = getPara("ids").split("-");
            List<String> idList = Arrays.asList(idStr);
            List<Order> orders = Order.dao.paginate(orderParam).getList();
            render(export(orders, idList));
            return;
        }
        setAttr("orders", Order.dao.paginate(orderParam));
        setAttr("statuses", Order.StoreOrderStatus.values());
        setAttr("paymentTypes", Order.PaymentType.values());
        setAttr("nameMap", MarketingHolder.me().getEnabledNameMap());
        setAttr("mnameList", Order.dao.getMnameList());
        setAttr("deliveryTypes", Order.DeliveryType.values());
        keepPara();
    }

    private PoiRender export(List<Order> orders, List<String> idList) {
        Iterator<Order> iter = orders.iterator();
        while (iter.hasNext()) {
            Order order = iter.next();
            if (!idList.contains(order.getId().toString())) {
                iter.remove();
            }
        }

        String[] headersArray = new String[EXPORT_COLUMNS.length];
        for (int i = 0; i < headersArray.length; i++) {
            String header = EXPORT_COLUMNS[i];
            try {
                header = getRes().get("order.export." + EXPORT_COLUMNS[i].toLowerCase());
            } catch (MissingResourceException exception) {
                // do nothing
            }
            headersArray[i] = header;
        }
        List<Record> records = Lists.newArrayList();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Order order : orders) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Record record = new Record();
                record.set("order_number", order.getOrderNumber());
                record.set("status", getRes().get("order.status." + order.getStatus().toLowerCase()));
                record.set("created_date", simpleDateFormat.format(order.getCreatedDate()));
                record.set("payment_type", order.getPaymentType() != null ? getRes().get("order.payment_type." + order.getPaymentType().toLowerCase()) : "");
                User user = order.getUser();
                record.set("user_id", user.getId());
                record.set("user_name", user.getName());
                record.set("user_nickname", user.getName());
                record.set("contact_user", order.getContactUser());
                record.set("address", order.getAddress());
                record.set("phone", order.getPhone());
                record.set("actual_price", order.getTotalPrice());
                record.set("total_price", order.getTotalPrice());
                record.set("product_name", orderItem.getProductName());
                record.set("product_id", orderItem.getProductId());
                record.set("product_specification_name", orderItem.getProductSpecificationName());
                record.set("price", orderItem.getPrice());
                record.set("quantity", orderItem.getQuantity());
                record.set("final_price", orderItem.getFinalPrice());
                record.set("remark", order.getRemark());
                record.set("barcode", orderItem.getBarcode());
                record.set("store_location", orderItem.getStoreLocation());
                record.set("mid", order.getMid());
                record.set("mname", order.getMname());
                records.add(record);
            }
        }

        String fileName = "orders-" + DateKit.today("yyyyMMddHHmmss") + ".xls";
        return PoiRender.me(records).fileName(fileName).sheetName("sheet").headers(headersArray).columns(EXPORT_COLUMNS);
    }


    @RequiresPermissions(value = { "order.view", "store_order.menu" }, logical = Logical.OR)
    @Before(Flash.class)
    public void detail() {
        Integer orderId = getParaToInt();
        String orderNumber = getPara("order_number");
        Order order;
        if (StrKit.notBlank(orderNumber)) {
            order = Order.dao.findByOrderNumber(orderNumber);
        } else {
            order = Order.dao.findById(orderId);
        }
        Marketing marketing = MarketingHolder.me().getMarketing(order.getMarketing(),
                order.getMarketingId(),
                order.getUserId(),
                order.getProvince(),
                order.getCity(),
                order.getDistrict());
        if (marketing != null) {
            order.put("marketing_admin_url", marketing.getAdminUrl());
        }
        setAttr("order", order);
        setAttr("expresses", Express.dao.findAllEnabled());
        setAttr("pcds", Pcd.dao.findByParentId(null));
        setAttr("orderProcessLogs", OrderProcessLog.dao.findByOrderId(order.getId()));
        setAttr("nameMap", MarketingHolder.me().getNameMap());

        BasePlugin wmsPlugin = ExtPluginHolder.me().get(WmsPlugin.class);
        setAttr("wmsEnabled", wmsPlugin.isEnabled());
        if (wmsPlugin.isEnabled()) {
            WmsApi wmsApi = new WmsApi();
            QueryWarehouseResult warehouseResult = wmsApi.queryWarehouse();
            setAttr("warehouses", warehouseResult.getWarehouses());
        }
    }
}
