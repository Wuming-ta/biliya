package com.jfeat.api;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/11/6
 */
public class VerifyTest {
    private String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1k/X+9T/7emBVegm2sNx+X35fp3xgCuq3tO8GGo51HfWybX89pAwsJYszXkdFCny7R3oJ7qSXRxi54IsdJVNJxlEoQkKvieuGxIjyNQLw705EQA0TCIGiJzDk22gXVrHmO2Okv8G4O+HEKMiea9qrWNYPXTHCxfrZ90YyYetbBql6LLxVyoMkX/vhQdTG3ww6G/t1ADRa1epHLjm9vrMf4AuYSKAgOflUVHA08gpnWrC5mq90VZIs8P5LyDex3AeNaHVYPlPqa6L10DKJb86IsUuWeiHrz7wlbnrvobsenOrabhAD9PLlCZyVdglVV7O6Vb4cUp24yivqZnlg330QIDAQAB";

    @Test
    @Ignore
    public void test() throws AlipayApiException {
        String notifyJson = "{\"gmt_create\":\"2018-11-06 14:05:46\",\"charset\":\"utf-8\",\"seller_email\":\"smwxsi0343@sandbox.com\",\"subject\":\"MUASKIN熬夜密集修护眼霜 x 1.\",\"sign\":\"OkSjLGe/0zKFSa7/LtnfGXlNNtZtREu6G8CLG+67ZBbbrQsdFeLvUomB1hug2APfqojFaOQr1CmFw09AF7/z0Uboog6ktzUgTEaw93d7PCR3rOrzMRbPLKzWg3//uuFvTmcS+m47BaBL8OZxX/qIGMpoITzwGqwbUaZzgiRQewgqVCuv0r0L/25tk95YBAqz3BAg8+z0RRuBUr/YOPfZ0HaDEKCmM95SqWr9lsEvOKbg0Xoh0vhM393DKEc0nePe4rIqwjW7Aa/T1kBF48xTeUbIUhM6YNsd6a/w5vRMN/El333H2NfaC8mgT6/LnwjNKjIJHYYypouPMzj1BcRrTg==\",\"buyer_id\":\"2088102176804805\",\"invoice_amount\":\"203.00\",\"notify_id\":\"4423de04e91e204d5e52992b4f13122m6a\",\"fund_bill_list\":\"[{\"amount\":\\\"203.00\\\",\\\"fundChannel\\\":\\\"ALIPAYACCOUNT\\\"}]\",\"notify_type\":\"trade_status_sync\",\"trade_status\":\"TRADE_SUCCESS\",\"receipt_amount\":\"203.00\",\"app_id\":\"2016092200567241\",\"buyer_pay_amount\":\"203.00\",\"sign_type\":\"RSA2\",\"seller_id\":\"2088102176709472\",\"gmt_payment\":\"2018-11-06 14:05:52\",\"notify_time\":\"2018-11-06 14:05:53\",\"version\":\"1.0\",\"out_trade_no\":\"Order_18110614050843520\",\"total_amount\":\"203.00\",\"trade_no\":\"2018110622001404800200691588\",\"auth_app_id\":\"2016092200567241\",\"buyer_logon_id\":\"roe***@sandbox.com\",\"point_amount\":\"0.00\"}\n";
        Map<String, String> params = JSONObject.parseObject(notifyJson, Map.class);

        boolean res = AlipaySignature.rsaCheckV1(params, alipayPublicKey, "UTF-8", "RSA2");
        System.out.println(res);
    }

}
