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

package com.jfeat.pcd.api;

import com.jfeat.core.RestController;
import com.jfeat.pcd.model.Pcd;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.util.List;

/**
 * Created by jacky on 2/24/16.
 */
@ControllerBind(controllerKey = "/rest/pcd")
public class PcdController extends RestController {

    /**
     * GET /rest/pcd?all=true&province=XX&city=XXX
     */
    public void index() {
        if (StrKit.notBlank(getPara("all"))) {
            renderSuccess(Pcd.dao.findAllByCache());
            return;
        }

        String province = getPara("province");
        String city = getPara("city");
        List<Pcd> list;
        if (StrKit.notBlank(city)) {
            list = Pcd.dao.findByNameWildcard(city, Pcd.CITY);
        }
        else if (StrKit.notBlank(province)) {
            list = Pcd.dao.findByNameWildcard(province, Pcd.PROVINCE);
        }
        else {
            renderSuccess(Pcd.dao.findRoot());
            return;
        }
        for (Pcd pcd : list) {
            pcd.put("area_list", pcd.getAreaList());
        }
        renderSuccess(list);
    }
}
