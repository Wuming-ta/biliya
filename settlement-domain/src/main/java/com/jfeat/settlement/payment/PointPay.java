package com.jfeat.settlement.payment;

import com.jfeat.payment.Payment;
import com.jfeat.settlement.model.OwnerBalance;
import com.jfeat.settlement.service.OwnerBalanceService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 使用用户余额进行支付
 * Created by jackyhuang on 16/8/13.
 */
public class PointPay implements Payment {

    public static final String PAYMENT_TYPE = "POINT";

    private static Logger logger = LoggerFactory.getLogger(PointPay.class);

    private OwnerBalanceService ownerBalanceService = Enhancer.enhance(OwnerBalanceService.class);

    @Override
    @Before(Tx.class)
    public boolean canPay(int userId, double price) {
        OwnerBalance ownerBalance = ownerBalanceService.queryBalance(userId);
        if (ownerBalance != null && ownerBalance.getBalance().compareTo(BigDecimal.valueOf(price)) >= 0) {
            return true;
        }
        return false;
    }

    @Override
    public Ret prePay(String title, String orderNumber, double totalPrice, String ip, String notifyUrl) {
        throw new RuntimeException("unsupported payment type.");
    }

    @Override
    @Before(Tx.class)
    public Ret pay(int userId, double price, String note) {
        return Ret.create("result", ownerBalanceService.subtractReward(userId, BigDecimal.valueOf(price)));
    }

    @Override
    @Before(Tx.class)
    public Ret refund(int userId, String orderType, String orderOrigin, String orderNumber, String refundNumber, BigDecimal totalFee, BigDecimal refundFee) {
        logger.info("refunding for userId {}, orderNumber {}. ", userId, orderNumber);
        boolean result = ownerBalanceService.addReward(userId, refundFee);
        logger.info("refund result is {}, refund fee = {}", result, refundFee);
        return Ret.create("result", result).put("message", "update.balance.result.is." + result);
    }

    @Override
    public Ret transfer(String accountNumber, String partnerTradeNumber, BigDecimal amount, String userRealName, String clientIp) {
        throw new RuntimeException("Unsupported Action.");
    }

    @Override
    public String getPaymentType() {
        return PAYMENT_TYPE;
    }
}
