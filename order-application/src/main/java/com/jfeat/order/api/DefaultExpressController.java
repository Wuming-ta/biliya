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

import com.jfeat.core.RestController;
import com.jfeat.order.model.Express;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by huangjacky on 16/7/20.
 */
@ControllerBind(controllerKey = "/rest/default_express")
public class DefaultExpressController extends RestController {
    public void index() {
        renderSuccess(Express.dao.findDefaultExpress());
    }
}
