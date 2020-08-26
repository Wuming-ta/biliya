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

package com.jfeat.misc.api;

import com.jfeat.core.RestController;
import com.jfeat.misc.model.AboutMall;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by jacky on 5/13/16.
 */
@ControllerBind(controllerKey = "/rest/about_mall")
public class AboutMallController extends RestController {
    public void index() {
        renderSuccess(AboutMall.dao.getDefault());
    }
}
