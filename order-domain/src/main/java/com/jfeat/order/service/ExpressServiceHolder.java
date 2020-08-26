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

import com.jfeat.config.model.Config;
import com.jfinal.kit.StrKit;

/**
 * Created by jackyhuang on 16/9/29.
 */
public class ExpressServiceHolder {

    private static final String EXPRESS_CUSTOMER = "express.customer";
    private static final String EXPRESS_KEY = "express.key";

    private static ExpressServiceHolder me = new ExpressServiceHolder();

    public static ExpressServiceHolder me() {
        return me;
    }

    private ExpressServiceHolder() {

    }

    public ExpressService getExpressService() {
        String key = getExpressKey();
        String customer = getExpressCustomer();
        if (StrKit.notBlank(customer) && StrKit.notBlank(key)) {
            return new ExpressProService(key, customer);
        }
        if (StrKit.notBlank(key)){
            return new ExpressBasicService(key);
        }
        throw new RuntimeException("invalid.express.key");
    }

    private String getExpressKey() {
        Config config = Config.dao.findByKey(EXPRESS_KEY);
        if (config == null) {
            return null;
        }
        return config.getValueToStr();
    }

    private String getExpressCustomer() {
        Config config = Config.dao.findByKey(EXPRESS_CUSTOMER);
        if (config == null) {
            return null;
        }
        return config.getValueToStr();
    }
}
