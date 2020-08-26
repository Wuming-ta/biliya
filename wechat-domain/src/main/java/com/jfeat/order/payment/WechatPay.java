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

package com.jfeat.order.payment;

import com.google.common.collect.Maps;
import com.jfeat.payment.Payment;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackyhuang on 16/9/2.
 */
public class WechatPay implements Payment {

    private static Logger logger = LoggerFactory.getLogger(WechatPay.class);

    public static final String PAYMENT_TYPE = "WECHAT";

    @Override
    public boolean canPay(int userId, double price) {
        return true;
    }

    @Override
    public Ret pay(int userId, double price, String note) {
        return Ret.create("result", false);
    }


    /**
     * 生成支付码
     * @param title
     * @param orderNumber
     * @param totalPrice
     * @param ip
     * @param notifyUrl
     * @return
     */
    @Override
    public Ret prePay(String title, String orderNumber, double totalPrice, String ip, String notifyUrl) {
        if (StrKit.isBlank(orderNumber)) {
            return Ret.create("result", false).put("message", "order.not.found");
        }
        String totalFee = convertPrice(totalPrice);
        HashMap params = new HashMap();
        params.put("product_id", orderNumber);
        params.put("appid", WxConfig.getAppId());
        params.put("mch_id", WxConfig.getPartnerId());
        params.put("body", convertBody(title));
        params.put("detail", title);
        params.put("out_trade_no", orderNumber);
        params.put("total_fee", totalFee);
        params.put("spbill_create_ip", ip);
        params.put("trade_type", PaymentApi.TradeType.NATIVE.name());
        params.put("nonce_str", System.currentTimeMillis() / 1000L + "");
        params.put("notify_url", notifyUrl);
        String sign = PaymentKit.createSign(params, WxConfig.getPartnerKey());
        params.put("sign", sign);
        logger.debug("params={}", params);
        String xmlResult = PaymentApi.pushOrder(params);
        Map result = PaymentKit.xmlToMap(xmlResult);
        logger.debug("PushOrder Result: {}", result);
        String return_code = (String) result.get("return_code");
        StringBuilder message = new StringBuilder();
        message.append(result.get("return_msg"));
        if (!StrKit.isBlank(return_code) && "SUCCESS".equals(return_code)) {
            String result_code = (String) result.get("result_code");
            if (!StrKit.isBlank(result_code) && "SUCCESS".equals(result_code)) {
                String codeUrl = (String) result.get("code_url");
                return Ret.create("result", true).put("code_url", codeUrl);
            }
            message.append(result.get("err_code"));
            message.append(result.get("err_code_des"));
        }
        return Ret.create("result", false).put("message", message.toString());
    }

    @Override
    public Ret refund(int userId,
                      String orderType,
                      String orderOrigin,
                      String orderNumber,
                      String refundNumber,
                      BigDecimal totalFee,
                      BigDecimal refundFee) {
        if (StrKit.isBlank(orderNumber)) {
            return Ret.create("result", false).put("message", "order.not.found");
        }
        String type = WechatPayDefination.ORDER_ORIGINS.getOrDefault(orderOrigin, PaymentApi.TradeType.JSAPI.name());
        HashMap<String, String> params = Maps.newHashMap();
        params.put("appid", WechatPayDefination.getAppId(type));
        params.put("mch_id", WechatPayDefination.getPartnerId(type));
        params.put("out_trade_no", orderType + "_" + orderNumber);
        params.put("out_refund_no", refundNumber);
        String totalFeeStr = convertPrice(totalFee.doubleValue());
        String refundFeeStr = convertPrice(refundFee.doubleValue());
        params.put("total_fee", totalFeeStr);
        params.put("refund_fee", refundFeeStr);
        params.put("op_user_id", WechatPayDefination.getPartnerId(type));
        logger.debug("refund: params = {}", params);
        Map result = PaymentApi.refund(params, WechatPayDefination.getPartnerKey(type), WechatPayDefination.getCert(type));
        logger.debug("refund result = {}", result);
        String return_code = (String) result.get("return_code");
        StringBuilder message = new StringBuilder();
        message.append(result.get("return_msg"));
        if (!StrKit.isBlank(return_code) && "SUCCESS".equals(return_code)) {
            String result_code = (String) result.get("result_code");
            if (!StrKit.isBlank(result_code) && "SUCCESS".equals(result_code)) {
                logger.info("refund success: {}", result);
                return Ret.create("result", true);
            }
            message.append(result.get("err_code"));
            message.append(result.get("err_code_des"));
        }
        logger.error("refund result: {}", result);
        return Ret.create("result", false).put("message", message.toString());
    }

    private static String transferUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    /**
     * 微信提现
     *
     * @param clientIp
     * @return Ret {"result": true, "message": "msg"}
     */
    @Override
    public Ret transfer(String accountNumber, String partnerTradeNumber, BigDecimal amount, String userRealName, String clientIp) {
        Map<String, String> params = Maps.newHashMap();
        params.put("mch_appid", WxConfig.getAppId());
        params.put("mchid", WxConfig.getPartnerId());
        params.put("nonce_str", System.currentTimeMillis() + "");
        params.put("openid", accountNumber);
        params.put("partner_trade_no", partnerTradeNumber);
        params.put("amount", convertPrice(amount.doubleValue()));
        params.put("check_name", "OPTION_CHECK");
        params.put("desc", "积分提现");
        params.put("spbill_create_ip", clientIp);
        params.put("re_user_name", userRealName);
        String sign = PaymentKit.createSign(params, WxConfig.getPartnerKey());
        params.put("sign", sign);
        String xmlStr = HttpUtils.postSSL(transferUrl, PaymentKit.toXml(params), WxConfig.getCertPath(), WxConfig.getPartnerId());
        Map result = PaymentKit.xmlToMap(xmlStr);
        String return_code = (String) result.get("return_code");
        StringBuilder message = new StringBuilder();
        message.append(result.get("return_msg"));
        if (!StrKit.isBlank(return_code) && "SUCCESS".equals(return_code)) {
            String result_code = (String) result.get("result_code");
            if (!StrKit.isBlank(result_code) && "SUCCESS".equals(result_code)) {
                logger.info("transfer success: {}", result);
                return Ret.create("result", true);
            }
            message.append(result.get("err_code"));
            message.append(result.get("err_code_des"));
        }
        logger.error("refund result: {}", result);
        Ret ret = Ret.create("result", false);
        ret.put("message", message.toString());
        return ret;
    }

    @Override
    public String getPaymentType() {
        return PAYMENT_TYPE;
    }

    /**
     * 微信支付要求total_fee字段是整数，单位是分
     *
     * @param price
     * @return
     */
    private String convertPrice(double price) {
        return String.valueOf(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(100)).intValue());
    }

    protected String convertBody(String body) {
        if (body == null) {
            return "PAY";
        }
        int maxLength = 50;
        byte[] bodyBytes = body.getBytes();
        int length = bodyBytes.length;
        if (length > maxLength) {
            byte[] bytes = new byte[maxLength];
            System.arraycopy(bodyBytes, 0, bytes, 0, maxLength);
            return new String(bytes);
        }
        return body;
    }
}