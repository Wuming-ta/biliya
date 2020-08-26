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

import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.kit.HttpKit;
import com.jfeat.kit.JsonKit;
import com.jfeat.order.model.Order;
import com.jfeat.order.service.OrderService;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.weixin.sdk.kit.PaymentKit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackyhuang on 17/5/6.
 */
@Deprecated
@ControllerBind(controllerKey = "/order_pay_notify")
public class OrderPayNotifyController extends BaseController {

    private OrderService orderService = Enhancer.enhance(OrderService.class);

    public void index() {
        String xmlMsg = HttpKit.readData(this.getRequest());
        logger.info("order_pay_notify=" + xmlMsg);
        Map params = PaymentKit.xmlToMap(xmlMsg);
        String result_code = (String)params.get("result_code");
        String totalFee = (String)params.get("total_fee");
        String orderNumber = (String)params.get("out_trade_no");
        String transId = (String)params.get("transaction_id");
        String timeEnd = (String)params.get("time_end");

        Order order = Order.dao.findByOrderNumber(orderNumber);
        if (order == null) {
            renderText("");
            return;
        }

        if (order.getPaymentType().equals(Order.PaymentType.WECHAT.toString())
                && PaymentKit.verifyNotify(params, WxConfig.getPartnerKey())
                && "SUCCESS".equals(result_code)) {
            logger.info("Update order info");
            if (updateOrder(order, transId)) {
                HashMap xml = new HashMap();
                xml.put("return_code", "SUCCESS");
                xml.put("return_msg", "OK");
                this.renderText(PaymentKit.toXml(xml));
                return;
            }
        }

        this.renderText("");
    }

    private boolean updateOrder(Order order, String tradeNumber) {
        String data = com.jfinal.kit.HttpKit.readData(getRequest());
        Map<String, Object> map;
        try {
            map = JsonKit.convertToMap(data);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        order.setStatus(Order.Status.PAID_CONFIRM_PENDING.toString());
        order.setTradeNumber(tradeNumber);
        Ret ret = orderService.updateOrder(order);
        logger.debug("Order Pay Notify: updateOrder ret = {}", ret.getData());
        return BaseService.isSucceed(ret);
    }
}
