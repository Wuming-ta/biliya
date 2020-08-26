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

package com.jfeat.payment;

import com.jfinal.kit.Ret;

import java.math.BigDecimal;

/**
 * Created by jackyhuang on 16/8/13.
 */
public interface Payment {

    boolean canPay(int userId, double price);

    /**
     * 生成支付码
     * @param orderNumber
     * @return
     */
    Ret prePay(String title, String orderNumber, double totalPrice, String ip, String notifyUrl);

    Ret pay(int userId, double price, String note);

    Ret refund(int userId, String orderType, String orderOrigin, String orderNumber, String refundNumber, BigDecimal totalFee, BigDecimal refundFee);

    Ret transfer(String accountNumber, String partnerTradeNumber, BigDecimal amount, String userRealName, String clientIp);

    String getPaymentType();
}
