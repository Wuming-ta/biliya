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
import com.jfeat.misc.model.Ad;
import com.jfeat.misc.model.AdGroup;
import com.jfinal.ext.route.ControllerBind;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by jacky on 5/16/16.
 */
@ControllerBind(controllerKey = "/rest/ad")
public class AdController extends RestController {

    public void index() {
        List<AdGroup> list = AdGroup.dao.findAll();
        for (AdGroup group : list) {
            group.put("ads", group.getAvailableAds());
        }
        renderSuccess(list);
    }

    /**
     * GET /rest/ad/<group-name>
     */
    public void show() {
        String groupName = getPara();
        try {
            groupName = URLDecoder.decode(groupName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        AdGroup group = AdGroup.dao.findByName(groupName);
        if (group == null) {
            renderFailure("group.not.found");
            return;
        }
        renderSuccess(group.getAvailableAds());
    }
}
