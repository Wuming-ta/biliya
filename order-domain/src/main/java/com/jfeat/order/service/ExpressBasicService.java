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
import com.jfeat.core.BaseService;
import com.jfeat.kit.HttpKit;
import com.jfeat.order.model.Express;
import com.jfeat.order.model.Order;
import com.jfinal.kit.Ret;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * kuaidi100 免费版 api
 * Created by jacky on 5/12/16.
 */
public class ExpressBasicService extends ExpressService {

    private static final String EXPRESS_URL = "http://api.kuaidi100.com/api";
    private String key;

    public ExpressBasicService(String key) {
        this.key = key;
    }

    /**
     * 根据快递公司编号和运单号查物流信息
     * @param comCode
     * @param number
     * @return
     */
    @Override
    public ExpressInfo queryExpress(String comCode, String number) {
        Map<String, String> param = new HashMap<>();
        param.put("id", key);
        param.put("com", comCode);
        param.put("nu", number);
        try {
            ExpressInfo expressInfo = HttpKit.get(EXPRESS_URL, ExpressInfo.class, param, null);
            expressInfo.setCompany(getExpressCompany(expressInfo.getCom()));
            return expressInfo;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.toString());
            ExpressInfo expressInfo = new ExpressInfo();
            expressInfo.setStatus("2");
            return expressInfo;
        }
    }

    public void setExpressKey(String key) {
        this.key = key;
    }
}
