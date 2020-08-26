package com.jfeat.member.service;

import com.jfeat.member.model.Wallet;
import com.jfeat.payment.Payment;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.Ret;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * 使用钱包支付
 * @author jackyhuang
 * @date 2018/9/14
 */
public class WalletPay implements Payment {

    private static Logger logger = LoggerFactory.getLogger(WalletPay.class);

    public static final String PAYMENT_TYPE = "WALLET";

    private WalletService walletService = Enhancer.enhance(WalletService.class);

    @Override
    public boolean canPay(int userId, double price) {
        return true;
    }

    @Override
    public Ret prePay(String title, String orderNumber, double totalPrice, String ip, String notifyUrl) {
        throw new RuntimeException("unsupported payment type");
    }

    @Override
    public Ret pay(int userId, double price, String note) {
        return walletService.pay(userId, BigDecimal.valueOf(price), note);
    }

    /**
     * 退款
     * @param userId
     * @param orderType
     * @param orderNumber
     * @param refundNumber
     * @param totalFee
     * @param refundFee
     * @return
     */
    @Override
    public Ret refund(int userId, String orderType, String orderOrigin, String orderNumber, String refundNumber, BigDecimal totalFee, BigDecimal refundFee) {
        return walletService.refund(userId, totalFee, refundFee, refundNumber);
    }

    /**
     * 转入钱包
     * @param accountNumber userId
     * @param partnerTradeNumber not used.
     * @param amount
     * @param userRealName not used.
     * @param clientIp not used.
     * @return
     */
    @Override
    public Ret transfer(String accountNumber, String partnerTradeNumber, BigDecimal amount, String userRealName, String clientIp) {
        return walletService.transfer(Integer.parseInt(accountNumber), amount);
    }

    @Override
    public String getPaymentType() {
        return PAYMENT_TYPE;
    }
}
