package com.jfeat.alipay.service;

import com.alipay.api.AlipayApiException;
import com.jfeat.alipay.config.AlipayKit;
import com.jfeat.payment.Payment;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * @author jackyhuang
 * @date 2018/11/12
 */
public class AlipayPay implements Payment {

    private static Logger logger = LoggerFactory.getLogger(AlipayPay.class);

    public static final String PAYMENT_TYPE = "ALIPAY";

    @Override
    public boolean canPay(int userId, double price) {
        return true;
    }

    @Override
    public Ret pay(int userId, double price, String note) {
        return Ret.create("result", false);
    }

    @Override
    public Ret prePay(String title, String orderNumber, double totalPrice, String ip, String notifyUrl) {
        return null;
    }

    @Override
    public Ret refund(int userId, String orderType, String orderOrigin, String orderNumber, String refundNumber, BigDecimal totalFee, BigDecimal refundFee) {
        logger.debug("Alipay refunding. orderType={}, orderNumber={}, refundNumber={}, totalFee={}, refundFee={}", orderType, orderNumber, refundNumber, totalFee, refundFee);
        if (StrKit.isBlank(orderNumber)) {
            return Ret.create("result", false).put("message", "order.not.found");
        }
        String outTradeNo = orderType + "_" + orderNumber;
        try {
            boolean res = AlipayKit.refund(outTradeNo, refundNumber, refundFee);
            logger.debug("refund outTradeNo={}, refundNum={}, result = {}", outTradeNo, refundNumber, res);
            return Ret.create("result", res);
        } catch (AlipayApiException e) {
            logger.error("Refund error. message={}, errCode={}, errMsg={}", e.getMessage(), e.getErrCode(), e.getErrMsg());
            return Ret.create("result", false).put("message", e.getMessage());
        }
    }

    @Override
    public Ret transfer(String accountNumber, String partnerTradeNumber, BigDecimal amount, String userRealName, String clientIp) {
        throw new RuntimeException("unsupported operation");
    }

    @Override
    public String getPaymentType() {
        return PAYMENT_TYPE;
    }
}
