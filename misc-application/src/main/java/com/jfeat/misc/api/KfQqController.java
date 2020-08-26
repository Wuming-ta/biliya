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
import com.jfeat.misc.model.KfQq;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by huangjacky on 16/7/22.
 */
@ControllerBind(controllerKey = "/rest/kf_qq")
public class KfQqController extends RestController {
    public void index() {
        renderSuccess(KfQq.dao.findAllEnabled());
    }
}
