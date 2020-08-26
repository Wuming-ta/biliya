package com.jfeat.marketing.common.controller;

import com.jfeat.core.BaseController;
import com.jfeat.pcd.model.Pcd;

/**
 * Created by kang on 2017/5/22.
 */
public class RegionSelectController extends BaseController {

    public void ajaxPcd() {
        renderJson(Pcd.dao.findAllByCache());
    }

}
