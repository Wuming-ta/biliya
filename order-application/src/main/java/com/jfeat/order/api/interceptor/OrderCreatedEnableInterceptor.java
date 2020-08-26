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

package com.jfeat.order.api.interceptor;

import com.jfeat.config.model.Config;
import com.jfeat.core.RestController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * Created by jackyhuang on 16/11/8.
 */
public class OrderCreatedEnableInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {
        Config config = Config.dao.findByKey("mall.order_created_enable");
        if (config == null || (config.getValueToBoolean() != null && config.getValueToBoolean())) {
            invocation.invoke();
            return;
        }

        if (invocation.getController() instanceof RestController) {
            RestController c = (RestController) invocation.getController();
            c.renderFailure("create.order.disabled");
        }
    }
}
