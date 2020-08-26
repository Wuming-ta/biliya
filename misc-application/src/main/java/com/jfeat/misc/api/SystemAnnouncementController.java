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
import com.jfeat.misc.model.SystemAnnouncement;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by jackyhuang on 16/10/17.
 */
@ControllerBind(controllerKey = "/rest/system_announcement")
public class SystemAnnouncementController extends RestController {

    public void index() {
        renderSuccess(SystemAnnouncement.dao.findByEnabled());
    }
}
