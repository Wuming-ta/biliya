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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jackyhuang on 16/8/13.
 */
public class PaymentHolder {

    public static final String DUMMY = "DUMMY";

    private static PaymentHolder me = new PaymentHolder();
    private Map<String, Payment> paymentMap = new ConcurrentHashMap<>();
    private Payment dummyPayment;

    private PaymentHolder() {

    }

    public static PaymentHolder me() {
        return me;
    }

    public void register(String paymentType, Payment payment) {
        paymentMap.put(paymentType, payment);
    }

    public Payment getPayment(String paymentType) {
        if (dummyPayment == null) {
            dummyPayment = paymentMap.get(DUMMY);
            if (dummyPayment == null) {
                throw new RuntimeException("dummy payment is not registered.");
            }
        }
        return paymentMap.get(paymentType) == null ? dummyPayment : paymentMap.get(paymentType);
    }
}
