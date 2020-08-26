package com.jfeat.order.payment;

import com.jfeat.payment.Payment;
import com.jfinal.kit.Ret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Created by jackyhuang on 16/8/13.
 */
public class DummyPayment implements Payment {

    public static final String PAYMENT_TYPE = "DUMMY";

    private static Logger logger = LoggerFactory.getLogger(DummyPayment.class);

    /**
     * 默认
     * @param userId
     * @param price
     * @return
     */
    @Override
    public boolean canPay(int userId, double price) {
        return true;
    }

    @Override
    public Ret prePay(String title, String orderNumber, double totalPrice, String ip, String notifyUrl) {
        throw new RuntimeException("unsupported payment type.");
    }

    @Override
    public Ret pay(int userId, double price, String note) {
        logger.info("dummy payment paying.");
        return Ret.create("result", false);
    }

    @Override
    public Ret refund(int userId, String orderType, String orderOrigin, String orderNumber, String refundNumber, BigDecimal totalFee, BigDecimal refundFee) {
        logger.info("dummy payment refunding.");
        return Ret.create("result", false);
    }

    @Override
    public Ret transfer(String accountNumber, String partnerTradeNumber, BigDecimal amount, String userRealName, String clientIp) {
        Ret ret = Ret.create("result", false);
        ret.put("message", "Dummy payment transfer.");
        return ret;
    }

    @Override
    public String getPaymentType() {
        return PAYMENT_TYPE;
    }
}
