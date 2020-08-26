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

package com.jfeat.order.service;

import com.jfeat.kit.HttpKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jackyhuang on 16/9/29.
 */
public class ExpressProService extends ExpressService {
    private static final String EXPRESS_URL = "http://poll.kuaidi100.com/poll/query.do";

    public ExpressProService(String key, String customer) {
        setKey(key);
        setCustomer(customer);
    }

    @Override
    public ExpressInfo queryExpress(String comCode, String number) {

        Map<String, String> param = new HashMap<String, String>();
        param.put("com", comCode);
        param.put("num", number);
        String key = getKey();
        String customer = getCustomer();
        String data = JsonKit.toJson(param) + key + customer;
        String sign = MD5.encode(data);
        Map<String, String> params = new HashMap<String, String>();
        params.put("customer", customer);
        params.put("sign", sign);
        params.put("param", JsonKit.toJson(param));
        ExpressInfo expressInfo = HttpKit.postForm(EXPRESS_URL, params, ExpressInfo.class);
        expressInfo.setCompany(getExpressCompany(expressInfo.getCom()));
        if (StrKit.notBlank(expressInfo.getReturnCode())) {
            expressInfo.setStatus("2");
        }
        if ("200".equals(expressInfo.getStatus())) {
            expressInfo.setStatus("1");
        }
        return expressInfo;
    }

    private String key;
    private String customer;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }


    public static class MD5 {
        // 获得MD5摘要算法的 MessageDigest 对象
        private static MessageDigest _mdInst = null;
        private static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        private static MessageDigest getMdInst() {
            if (_mdInst == null) {
                try {
                    _mdInst = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            return _mdInst;
        }

        public final static String encode(String s) {
            try {
                byte[] btInput = s.getBytes();
                // 使用指定的字节更新摘要
                getMdInst().update(btInput);
                // 获得密文
                byte[] md = getMdInst().digest();
                // 把密文转换成十六进制的字符串形式
                int j = md.length;
                char str[] = new char[j * 2];
                int k = 0;
                for (int i = 0; i < j; i++) {
                    byte byte0 = md[i];
                    str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                    str[k++] = hexDigits[byte0 & 0xf];
                }
                return new String(str);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
