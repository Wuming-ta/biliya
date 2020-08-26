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

package com.jfeat.misc.controller;

import com.jfeat.core.BaseController;
import com.jfeat.misc.model.KfQq;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by huangjacky on 16/7/22.
 */
public class KfQqController extends BaseController {

    @Override
    @RequiresPermissions("MiscApplication.view")
    public void index() {
        setAttr("qqList", KfQq.dao.findAll());
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void add() {

    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void save() {
        KfQq kfQq = getModel(KfQq.class);
        kfQq.save();
        redirect("/kf_qq");
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void edit() {
        setAttr("qq", KfQq.dao.findById(getParaToInt()));
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void update() {
        KfQq kfQq = getModel(KfQq.class);
        kfQq.update();
        redirect("/kf_qq");
    }

    @Override
    @RequiresPermissions("MiscApplication.delete")
    public void delete() {
        KfQq kfQq = KfQq.dao.findById(getParaToInt());
        kfQq.delete();
        redirect("/kf_qq");
    }
}
