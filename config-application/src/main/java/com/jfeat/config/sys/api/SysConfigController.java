/*
 *   Copyright (C) 2014-2016 GIMC
 *
 *    The program may be used and/or copied only with the written permission
 *    from GIMC or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.config.sys.api;

import com.jfeat.config.model.Config;
import com.jfeat.core.RestController;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by jackyhuang on 16/9/2.
 */
@ControllerBind(controllerKey = "/sys/rest/sys_config")
public class SysConfigController extends RestController {
    public void index() {
        renderSuccess(Config.dao.findBySys());
    }
}
