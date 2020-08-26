package com.jfeat.service;

import com.jfeat.core.Service;
import com.jfeat.service.exception.RetrieveOrderException;

import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/5/19
 */
public interface PayService extends Service {
    /**
     *
     * @param orderNumber
     * @return { "total_price": 111, "description": "xxff" }
     * @throws RetrieveOrderException
     */
    public Map<String, Object> retrieveToPayOrder(String orderNumber) throws RetrieveOrderException;

    /**
     *
     * @param orderNumber
     * @param paymentType
     * @param tradeNumber
     * @param payAccount 支付帐户，对于微信，就是openid
     * @throws RetrieveOrderException
     */
    public void paidNotify(String orderNumber, String paymentType, String tradeNumber, String payAccount) throws RetrieveOrderException;
}
