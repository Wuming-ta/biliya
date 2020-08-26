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

package com.jfeat.order.api;

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.order.service.ExpressBasicService;
import com.jfeat.order.service.ExpressServiceHolder;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;

/**
 * Created by jacky on 5/13/16.
 * @see com.jfeat.order.api.OrderExpressController
 * @author jackyhuang
 */
@Deprecated
@ControllerBind(controllerKey = "/rest/express_info")
public class ExpressInfoController extends RestController {

    /**
     * GET /rest/express_info?order_number=xxx
     */
    public void index() {
        String orderNumber = getPara("order_number");
        //ExpressService.setExpressKey("5c939702fc86a217");
        Ret ret = ExpressServiceHolder.me().getExpressService().queryExpress(orderNumber);
        if (BaseService.isSucceed(ret)) {
            renderSuccess(ret.get(BaseService.DATA));
            return;
        }
        renderFailure("cannot.find.express.info");
    }
}
