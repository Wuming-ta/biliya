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

package com.jfeat.mall.controller;

import com.jfeat.kit.DateKit;
import com.jfeat.kit.HttpKit;
import com.jfeat.mall.api.CouponApi;
import com.jfeat.mall.api.OrderApi;
import com.jfeat.mall.api.response.CouponResponse;
import com.jfeat.mall.api.response.MessageResponse;
import com.jfeat.mall.api.response.OrderResponse;
import com.jfeat.mall.config.SysConfig;
import com.jfeat.mall.controller.link.MarketingShareLink;
import com.jfeat.mall.controller.link.MarketingShareLinkHolder;
import com.jfeat.mall.interceptor.MerchantInterceptor;
import com.jfeat.mall.interceptor.WeixinInterceptor;
import com.jfeat.ui.MenuInterceptor;
import com.jfeat.mall.config.WxConfig;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.kit.IpKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

/** Weixin payment
 * Created by jacky on 4/25/16.
 */
@ControllerBind(controllerKey = "/payment/wpay", viewPath = "/wpay")
@Clear({MenuInterceptor.class})
public class WPayController extends PayBaseController {

    public static final String WPAY_URL = "/payment/wpay";

    private static final String notify_url = "/payment/wpay/pay_notify";

    @Before({WeixinInterceptor.class, MerchantInterceptor.class})
    public void index() {
        String openid = getAttr("openid");
        if (StrKit.isBlank(openid)) {
            renderError(500);
            return;
        }

        String orderNumber = getPara();
        if (StrKit.isBlank(orderNumber)) {
            renderError(404);
            return;
        }

        OrderApi orderApi = new OrderApi();
        OrderResponse orderResponse = orderApi.getOrder(orderNumber);
        if (!orderResponse.isSucceed()) {
            renderError(404);
            return;
        }
        if (! (orderResponse.getData().getStatus().equals(CREATED_PAY_PENDING)
                || orderResponse.getData().getStatus().equals(PAID_CONFIRM_PENDING)
                || orderResponse.getData().getStatus().equals(CONFIRMED_DELIVER_PENDING)) ) {
            renderError(400);
            return;
        }

        String title = convertBody(orderResponse.getData().getDescription());

        this.setAttr("totalFee", orderResponse.getData().getTotal_price());
        this.setAttr("title", title);
        this.setAttr("orderNumber", orderNumber);
        this.setAttr("payDate", DateKit.today("yyyy-MM-dd hh:mm:ss"));

        if (orderResponse.getData().getStatus().equals(PAID_CONFIRM_PENDING)
                || orderResponse.getData().getStatus().equals(CONFIRMED_DELIVER_PENDING)) {
            //override when it is marketing
            if (isMarketing(orderResponse)) {
                MarketingShareLink marketingShareLink = MarketingShareLinkHolder.me().getMarketingShareLink(orderResponse.getData().getMarketing(),
                                                                                        orderResponse.getData().getMarketing_id());
                setAttr("marketing", true);
                setAttr("jsconfig", signShareConfig());
                setAttr("shareTitle", marketingShareLink.buildTitle());
                setAttr("shareDesc", marketingShareLink.buildDescription());
                setAttr("shareImage", marketingShareLink.buildCover());
                setAttr("link", buildMarketingShareLink(marketingShareLink.buildLink()));
            }
            logger.debug("order already paid, render the page directly. ordernumber = {}", orderNumber);
            return;
        }

        Map result = pushOrder(title,
                orderResponse.getData().getDescription(),
                orderNumber,
                convertPrice(orderResponse.getData().getTotal_price()),
                openid);
        String return_code = (String)result.get("return_code");
        String return_msg = (String)result.get("return_msg");
        if(!StrKit.isBlank(return_code) && "SUCCESS".equals(return_code)) {
            String result_code = (String)result.get("result_code");
            if(!StrKit.isBlank(result_code) && "SUCCESS".equals(result_code)) {
                String prepayId = (String)result.get("prepay_id");
                String jsonStr = genPayJson(prepayId);
                this.setAttr("json", jsonStr);
                logger.debug("json={}", jsonStr);

                setAttr("pocketEnabled", SysConfig.getPocketEnabled());

                setAttr("jsconfig", signShareConfig());
                setAttr("host", WxConfig.getHost());
                setAttr("shareTitle", SysConfig.getPocketShareTitle());
                setAttr("shareDesc", SysConfig.getPocketShareDesc());
                String shareCode = getShareCode(orderNumber);
                setAttr("shareCode", shareCode);
                setAttr("link", buildCouponShareLink(shareCode));

                //override when it is marketing
                if (isMarketing(orderResponse)) {
                    MarketingShareLink marketingShareLink = MarketingShareLinkHolder.me().getMarketingShareLink(orderResponse.getData().getMarketing(), orderResponse.getData().getMarketing_id());
                    setAttr("marketing", true);
                    setAttr("shareTitle", marketingShareLink.buildTitle());
                    setAttr("shareDesc", marketingShareLink.buildDescription());
                    setAttr("shareImage", marketingShareLink.buildCover());
                    setAttr("link", buildMarketingShareLink(marketingShareLink.buildLink()));
                }

                logger.info("rendering the payment html.");
            } else {
                this.renderText((String) result.get("err_code_des"));
            }
        } else {
            this.renderText(return_msg);
        }
    }

    /**
     * 生成公众号支付的jsapi需要的json串
     * @param prepayId
     * @return
     */
    private String genPayJson(String prepayId) {
        HashMap packageParams = new HashMap();
        packageParams.put("appId", WxConfig.getAppId());
        packageParams.put("timeStamp", System.currentTimeMillis() / 1000L + "");
        packageParams.put("nonceStr", System.currentTimeMillis() + "");
        packageParams.put("package", "prepay_id=" + prepayId);
        packageParams.put("signType", "MD5");
        String packageSign = PaymentKit.createSign(packageParams, WxConfig.getPartnerKey());
        packageParams.put("paySign", packageSign);
        String jsonStr = JsonUtils.toJson(packageParams);
        return jsonStr;
    }

    /**
     * 生产预订单
     * @param title
     * @param detail
     * @param orderNumber
     * @param totalFee
     * @param openid
     * @return
     */
    private Map pushOrder(String title, String detail, String orderNumber, String totalFee, String openid) {
        HashMap params = new HashMap();
        params.put("appid", WxConfig.getAppId());
        params.put("mch_id", WxConfig.getPartnerId());
        params.put("body", title);
        params.put("detail", detail);
        params.put("out_trade_no", "Order_" + orderNumber);
        params.put("total_fee", totalFee);
        String ip = IpKit.getRealIp(this.getRequest());
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
        }
        if(StrKit.isBlank(ip)) {
            ip = "127.0.0.1";
        }

        params.put("spbill_create_ip", ip);
        params.put("trade_type", PaymentApi.TradeType.JSAPI.name());
        params.put("nonce_str", System.currentTimeMillis() / 1000L + "");
        params.put("notify_url", WxConfig.getHost() + notify_url);
        params.put("openid", openid);
        String sign = PaymentKit.createSign(params, WxConfig.getPartnerKey());
        params.put("sign", sign);
        logger.debug("params={}", params);
        String xmlResult = PaymentApi.pushOrder(params);
        Map result = PaymentKit.xmlToMap(xmlResult);
        logger.debug("Order Result: {}", result);
        return result;
    }

    @Before({WeixinInterceptor.class, MerchantInterceptor.class})
    public void pocket() {
        keepPara();
        String shareCode = getPara("share_code");
        Integer userId = getAttr("user_id");
        CouponApi couponApi = new CouponApi();
        CouponResponse response = couponApi.take(userId, shareCode, true);
        if (response.isSucceed()) {
            int couponCount = response.getData().getCoupons().size();
            setAttr("coupon_count", couponCount);
            setAttr("jsconfig", signShareConfig());
            setAttr("host", WxConfig.getHost());
            setAttr("shareTitle", SysConfig.getPocketShareTitle());
            setAttr("shareDesc", SysConfig.getPocketShareDesc());
            setAttr("link", buildCouponShareLink(shareCode));
            return;
        }
        redirect("/app");
    }

    public void pay_notify() {
        String xmlMsg = HttpKit.readData(this.getRequest());
        logger.info("pay_notify=" + xmlMsg);
        Map params = PaymentKit.xmlToMap(xmlMsg);
        String result_code = (String)params.get("result_code");
        String totalFee = (String)params.get("total_fee");
        String orderNumber = (String)params.get("out_trade_no");
        String transId = (String)params.get("transaction_id");
        String timeEnd = (String)params.get("time_end");
        if(PaymentKit.verifyNotify(params, WxConfig.getPartnerKey()) && "SUCCESS".equals(result_code)) {
            logger.info("Update order info");
            OrderApi orderApi = new OrderApi();
            String paymentType = "WECHAT";
            String[] array = orderNumber.split("_");
            if (array != null && array.length == 2) {
                orderNumber = array[1];
            }
            MessageResponse response = orderApi.paidNotifyOrder(orderNumber, paymentType, transId);
            if (response.isSucceed()) {
                HashMap xml = new HashMap();
                xml.put("return_code", "SUCCESS");
                xml.put("return_msg", "OK");
                this.renderText(PaymentKit.toXml(xml));
                return;
            }
            else {
                logger.error(response.getMessage());
            }
        }

        this.renderText("");
    }

}
