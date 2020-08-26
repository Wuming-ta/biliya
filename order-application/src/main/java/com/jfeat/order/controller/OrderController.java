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
import com.jfeat.config.model.Config;
import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.ext.plugin.*;
import com.jfeat.ext.plugin.store.StoreApi;
import com.jfeat.ext.plugin.wms.WmsApi;
import com.jfeat.ext.plugin.wms.services.domain.model.QueryWarehouseResult;
import com.jfeat.flash.Flash;
import com.jfeat.http.utils.HttpUtils;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfeat.marketing.Marketing;
import com.jfeat.marketing.MarketingHolder;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfeat.order.model.*;
import com.jfeat.order.model.param.OrderParam;
import com.jfeat.order.service.ExpressInfo;
import com.jfeat.order.service.ExpressServiceHolder;
import com.jfeat.order.service.OrderService;
import com.jfeat.pcd.model.Pcd;
import com.jfeat.ui.model.Widget;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.render.excel.PoiRender;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpRequest;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.codec.Base64;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jacky on 3/11/16.
 */
public class OrderController extends BaseController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    private static String[] EXPORT_COLUMNS = {
            "order_number", "status", "created_date", "payment_type", "user_id", "user_name", "user_nickname",
            "contact_user", "address", "phone", "actual_price", "total_price",
            "product_name", "product_id", "product_specification_name",
            "price", "quantity", "final_price", "remark", "barcode", "store_location",
            "mid", "mname"
    };

    @RequiresPermissions(value = { "order.view", "order.menu" }, logical = Logical.OR)
    @Before(Flash.class)
    @Override
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String mname = getPara("mname");
        String marketing = getPara("marketing");
        String status = getPara("status");
        String[] statuses = getParaValues("statuses");
        String uid = getPara("uid");
        String productName = getPara("pName");
        String barcode = getPara("barCode");
        String orderNumber = getPara("orderNumber");
        String contactUser = getPara("contactUser");
        String phone = getPara("phone");
        String paymentType = getPara("paymentType");
        Integer reminder = getParaToInt("reminder");
        String startTime = getPara("startTime");
        String endTime = getPara("endTime");
        Boolean export = getParaToBoolean("export");
        String type = Order.Type.ORDER.toString();
        String deliveryType = getPara("deliveryType");
        if (StrKit.notBlank(startTime)) {
            startTime += " 00:00:00";
        }
        if (StrKit.notBlank(endTime)) {
            endTime += " 23:59:59";
        }
        Boolean queryMarketing = false;
        if (StrKit.notBlank(marketing)) {
            queryMarketing = true;
            if ("all".equalsIgnoreCase(marketing)) {
                marketing = null;
            }
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
                .setStatuses(statuses)
                .setContactUser(contactUser)
                .setPhone(phone)
                .setReminder(reminder)
                .setPaymentType(paymentType)
                .setDeliveryType(deliveryType)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setMname(mname)
                .setProductName(productName)
                .setBarcode(barcode)
                .setMarketing(marketing)
                .setQueryMarketing(queryMarketing)
                .setShowDeleted(true);

        if (export != null) {
            String[] idStr = getPara("ids").split("-");
            List<String> idList = Arrays.asList(idStr);
            List<Order> orders = Order.dao.paginate(orderParam).getList();
            render(export(orders, idList));
            return;
        }

        setAttr("orders", Order.dao.paginate(orderParam));
        setAttr("statuses", Order.Status.values());
        setAttr("paymentTypes", Order.PaymentType.values());
        setAttr("deliveryTypes", Order.DeliveryType.values());
        setAttr("nameMap", MarketingHolder.me().getEnabledNameMap());
        setAttr("mnameList", Order.dao.getMnameList());

        OrderParam countParam = new OrderParam();
        countParam.setType(Order.Type.ORDER.toString());
        countParam.setDeliveryType( Order.DeliveryType.EXPRESS.toString());
        countParam.setShowDeleted(true);
        setAttr("EXPRESS_COUNT", Order.dao.countOrderByCond(countParam));

        countParam.setDeliveryType( Order.DeliveryType.SELF_PICK.toString());
        setAttr("SELF_PICK_COUNT", Order.dao.countOrderByCond(countParam));

        countParam.setDeliveryType( Order.DeliveryType.FLASH.toString());
        setAttr("FLASH_COUNT", Order.dao.countOrderByCond(countParam));

        OrderParam deliverReminderParam = new OrderParam();
        deliverReminderParam.setReminder(Order.DELIVER_REMINDER);
        deliverReminderParam.setShowDeleted(true);
        setAttr("deliverReminderOrderCount", Order.dao.countOrderByCond(deliverReminderParam));
        keepPara();
    }

    private PoiRender export(List<Order> orders, List<String> idList) {
        orders.removeIf(order -> !idList.contains(order.getId().toString()));

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

    @RequiresPermissions(value = { "order.view", "order.menu" }, logical = Logical.OR)
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

        Map<Integer, List<OrderItem>> merchantOrderItemMap = order.getOrderItems().stream().collect(Collectors.groupingBy(OrderItem::getMid));
        Map<Integer, SettledMerchant> merchantMap = new HashMap<>();
        SettledMerchant.dao.findAll().forEach(m -> merchantMap.put(m.getId(), m));
        Map<Integer, List<OrderExpress>> orderExpressMap = order.getOrderExpressList().stream().collect(Collectors.groupingBy(OrderExpress::getMid));

        setAttr("order", order);
        setAttr("expresses", Express.dao.findAllEnabled());
        setAttr("pcds", Pcd.dao.findByParentId(null));
        setAttr("orderProcessLogs", OrderProcessLog.dao.findByOrderId(order.getId()));
        setAttr("nameMap", MarketingHolder.me().getNameMap());
        setAttr("merchantOrderItemMap", merchantOrderItemMap);
        setAttr("merchantMap", merchantMap);
        setAttr("orderExpressMap", orderExpressMap);

        BasePlugin wmsPlugin = ExtPluginHolder.me().get(WmsPlugin.class);
        setAttr("wmsEnabled", wmsPlugin.isEnabled());
        if (wmsPlugin.isEnabled()) {
            WmsApi wmsApi = new WmsApi();
            QueryWarehouseResult warehouseResult = wmsApi.queryWarehouse();
            setAttr("warehouses", warehouseResult.getWarehouses());
        }
        BasePlugin storePlugin = ExtPluginHolder.me().get(StorePlugin.class);
        setAttr("storeEnabled", storePlugin.isEnabled());

        setAttr("wxExpressEnabled", wxaExpressEnabled());

        setAttr("basicAuth", getSysBasicAuth());
    }

    @RequiresPermissions("order.edit")
    public void addOrderExpress() {
        Integer orderId = getParaToInt("order_id");
        Integer expressId = getParaToInt("express_id");
        String expressNumber = getPara("express_number");
        Integer mid = getParaToInt("mid");
        Integer[] itemIds = getParaValuesToInt("item_id");
        logger.info("{}, {}, {}, {}", expressId, expressNumber, mid, itemIds);
        StringBuilder itemIdStr = new StringBuilder();
        if (itemIds != null && itemIds.length > 0) {
            for (int i = 0; i < itemIds.length; i++) {
                itemIdStr.append(itemIds[i]);
                if (i != itemIds.length - 1) {
                    itemIdStr.append(",");
                }
            }
        }
        Order order = Order.dao.findById(orderId);
        Express express = Express.dao.findById(expressId);
        OrderExpress orderExpress = new OrderExpress();
        orderExpress.setMid(mid);
        orderExpress.setExpressCode(express.getCode());
        orderExpress.setExpressCompany(express.getName());
        orderExpress.setExpressNumber(expressNumber);
        orderExpress.setOrderId(orderId);
        orderExpress.setOrderItems(itemIdStr.toString());
        orderExpress.save();
        if (!order.getStatus().equalsIgnoreCase(Order.Status.DELIVERING.toString())) {
            order.setStatus(Order.Status.DELIVERING.toString());
            orderService.updateOrder(order);
        }
        redirect("/order/detail/" + orderId);
    }

    @RequiresPermissions("order.edit")
    public void deleteOrderExpress() {
        Integer expressId = getParaToInt();
        OrderExpress orderExpress = OrderExpress.dao.findById(expressId);
        if (orderExpress != null) {
            Integer orderId = orderExpress.getOrderId();
            orderExpress.delete();
            redirect("/order/detail/" + orderId);
            return;
        }
        redirect("/order");
    }

    @RequiresPermissions("order.edit")
    public void confirm() {
        String returnUrl = getPara("returnUrl", "/order");
        Order order = Order.dao.findById(getParaToInt());
        order.setStatus(Order.Status.CONFIRMED_DELIVER_PENDING.toString());
        Ret ret = orderService.updateOrder(order);
        OrderProcessLog orderProcessLog = new OrderProcessLog();
        orderProcessLog.setProcessDate(new Date());

        setFlash("message", getRes().get(ret.get(OrderService.MESSAGE).toString()));
        redirect(returnUrl);
    }

    @RequiresPermissions("order.edit")
    public void cancel() {
        String returnUrl = getPara("returnUrl", "/order");
        Order order = Order.dao.findById(getParaToInt());
        Order.Status currentStatus = Order.Status.valueOf(order.getStatus());
        Order.Status targetStatus;
        if (currentStatus == Order.Status.CREATED_PAY_PENDING) {
            targetStatus = Order.Status.CLOSED_CANCELED;
        } else if (currentStatus == Order.Status.PAID_CONFIRM_PENDING) {
            targetStatus = Order.Status.CANCELED_REFUND_PENDING;
        } else {
            targetStatus = Order.Status.CANCELED_RETURN_PENDING;
        }
        order.setStatus(targetStatus.toString());
        Ret ret = orderService.updateOrder(order);
        setFlash("message", getRes().get(ret.get(OrderService.MESSAGE).toString()));
        redirect(returnUrl);
    }

    @Override
    @RequiresPermissions("order.delete")
    public void delete() {
        String returnUrl = getPara("returnUrl", "/order");
        Order order = Order.dao.findById(getParaToInt());
        Ret ret = orderService.deleteOrder(order);
        setFlash("message", getRes().get(ret.get(OrderService.MESSAGE).toString()));
        redirect(returnUrl);
    }

    @RequiresPermissions("order.delete")
    public void batchDelete() {
        String returnUrl = getPara("returnUrl", "/order");
        String ids = getPara();
        int successful = 0;
        int total = 0;
        if (StrKit.notBlank(ids)) {
            String[] idsStrArray = ids.split("-");
            total = idsStrArray.length;
            for (int i = 0; i < idsStrArray.length; i++) {
                Integer orderId = Integer.parseInt(idsStrArray[i]);
                Order order = Order.dao.findById(orderId);
                Ret ret = orderService.deleteOrder(order);
                if (BaseService.isSucceed(ret)) {
                    successful++;
                }
            }
        }
        setFlash("message", getRes().format("order.batch.delete.result", total, successful));
        redirect(returnUrl);
    }

    @RequiresPermissions("order.edit")
    public void delivered() {
        String returnUrl = getPara("returnUrl", "/order");
        Order order = Order.dao.findById(getParaToInt());
        order.setStatus(Order.Status.DELIVERED_CONFIRM_PENDING.toString());
        Ret ret = orderService.updateOrder(order);
        setFlash("message", getRes().get(ret.get(OrderService.MESSAGE).toString()));
        redirect(returnUrl);
    }

    /**
     * update contact info
     */
    @RequiresPermissions("order.edit")
    public void updateContact() {
        Order order = Order.dao.findById(getParaToInt());
        if (order == null) {
            renderError(404);
            return;
        }
        Integer provinceId = getParaToInt("province");
        Integer cityId = getParaToInt("city");
        Integer districtId = getParaToInt("district");
        String detail = getPara("detail");
        String phone = getPara("phone");
        String contactUser = getPara("contactUser");
        Pcd province = Pcd.dao.findById(provinceId);
        Pcd city = Pcd.dao.findById(cityId);
        Pcd district = Pcd.dao.findById(districtId);
        order.setProvince(province.getName());
        order.setCity(city.getName());
        order.setDistrict(district.getName());
        order.setDetail(detail);
        order.setContactUser(contactUser);
        order.setPhone(phone);
        Ret ret = orderService.updateOrder(order);
        logger.debug("updateContact ret= {}", ret.getData());
        redirect("/order/detail/" + order.getId() + "#contact");
    }

    /**
     * 分配门店
     * update store info
     */
    @RequiresPermissions("order.edit")
    public void updateStore() {
        String returnUrl = getPara("returnUrl", "/order");
        Order order = Order.dao.findById(getParaToInt());
        if (order == null) {
            renderError(404);
            return;
        }
        String storeId = getPara("storeId");
        String storeCode = getPara("storeCode");
        String storeName = getPara("storeName");
        String storeCover = getPara("storeCover");
        String storeAddress = getPara("storeAddress");
        order.setStoreId(storeId);
        order.setStoreCode(storeCode);
        order.setStoreName(storeName);
        order.setStoreAddress(storeAddress);
        order.setStoreCover(storeCover);
        Ret ret = orderService.updateOrder(order);
        logger.debug("update store ret= {}", ret.getData());
        redirect(urlDecode(returnUrl));
    }

    /**
     * ajax pcd
     */
    public void ajaxPcd() {
        renderJson(Pcd.dao.findByParentId(getParaToInt()));
    }


    /**
     * ajax get express info. return the html snip.
     */
    public void expressInfo() {
        Integer expressId = getParaToInt();
        OrderExpress orderExpress = OrderExpress.dao.findById(expressId);
        if (orderExpress == null) {
            logger.debug("order express not found for expressId {}", expressId);
            return;
        }
        ExpressInfo expressInfo = ExpressServiceHolder.me().getExpressService().queryExpress(orderExpress.getExpressCode(), orderExpress.getExpressNumber());

        if (expressInfo.isSucceed()) {
            setAttr("expressInfo", expressInfo);
        }
    }

    /**
     * ajax get store list, return the html snip.
     */
    public void storeList() {
        Integer pageNum = getParaToInt("pageNum", 1);
        Integer pageSize = getParaToInt("pageSize", 20);
        BasePlugin storePlugin = ExtPluginHolder.me().get(StorePlugin.class);
        if (!storePlugin.isEnabled()) {
            renderText("店铺组件未启用");
            return;
        }
        StoreApi storeApi = new StoreApi();
        setAttr("stores", storeApi.getStoreList(pageNum, pageSize));
        keepPara();
    }

    /**
     * widget
     */
    public void widget() {
        OrderParam deliverReminderParam = new OrderParam();
        deliverReminderParam.setShowDeleted(true);
        deliverReminderParam.setReminder(Order.DELIVER_REMINDER);
        setAttr("deliverReminderOrderCount", Order.dao.countOrderByCond(deliverReminderParam));


        OrderParam orderParam = new OrderParam();
        setAttr("totalOrderCount", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.DELIVERING.toString()
        });
        setAttr("deliveringOrderCount", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.CONFIRMED_DELIVER_PENDING.toString()
        });
        setAttr("deliverPendingOrderCount", Order.dao.countOrderByCond(orderParam));

        orderParam.setStatuses(new String[] {
                Order.Status.CANCELED_REFUND_PENDING.toString(), Order.Status.CANCELED_RETURN_PENDING.toString()
        });
        setAttr("returnRefundOrderCount", Order.dao.countOrderByCond(orderParam));

        Widget widget = Widget.dao.findFirstByField(Widget.Fields.NAME.toString(), "order.overview");
        setAttr("orderWidgetDisplayName", widget.getDisplayName());
        setAttr("orderWidgetName", widget.getName());
    }

    /**
     * notification
     */
    public void returnRefundOrderCount() {
        OrderParam orderParam = new OrderParam();
        orderParam.setStatuses(new String[] {
                Order.Status.CANCELED_REFUND_PENDING.toString(), Order.Status.CANCELED_RETURN_PENDING.toString()
        });
        orderParam.setShowDeleted(true);
        long count = Order.dao.countOrderByCond(orderParam);
        String result = count == 0 ? "" : String.valueOf(count);
        renderText(result);
    }


    /**
     * notification
     */
    public void newOrderCount() {
        OrderParam orderParam = new OrderParam();
        orderParam.setStatuses(new String[] {
                Order.Status.PAID_CONFIRM_PENDING.toString(), Order.Status.CONFIRMED_DELIVER_PENDING.toString()
        });
        orderParam.setShowDeleted(true);
        long count = Order.dao.countOrderByCond(orderParam);
        String result = count == 0 ? "" : String.valueOf(count);
        renderText(result);
    }

    private String urlDecode(String url) {
        return StringEscapeUtils.unescapeHtml4(url);
    }

    private boolean wxaExpressEnabled() {
        Config config = Config.dao.findByKey("wx.express.enabled");
        boolean wxExpressEnabled = false;
        if (config != null) {
            wxExpressEnabled = config.getValueToBoolean();
        }
        return wxExpressEnabled;
    }

    //get request headers
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    private String getSysBasicAuth() {
        String basicAuthUsername = getConfigValue("sys.auth.username", "sys");
        String basicAuthPassword = getConfigValue("sys.auth.password", "sys");
        String basicAuth = Base64.encodeToString((basicAuthUsername + ":" + basicAuthPassword).getBytes());
        return "Basic " + basicAuth;
    }

    private <T> T getConfigValue(String key, T defaultValue) {
        Config config = Config.dao.findByKey(key);
        if (config == null) {
            return defaultValue;
        }
        if (defaultValue instanceof String) {
            return (T) config.getValueToStr();
        }
        if (defaultValue instanceof Integer) {
            return (T) config.getValueToInt();
        }
        if (defaultValue instanceof Boolean) {
            return (T) config.getValueToBoolean();
        }
        if (defaultValue instanceof Float) {
            return (T) config.getValueToFloat();
        }
        return (T) config.getValue();
    }

}
