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

package com.jfeat.order.controller;

import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.order.model.Express;
import com.jfinal.aop.Before;

/**
 * Created by jacky on 5/12/16.
 */
public class ExpressController extends BaseController {

    @Override
    @Before(Flash.class)
    public void index() {
        setAttr("expresses", Express.dao.findAll());
    }

    @Override
    public void add() {

    }

    @Override
    public void save() {
        Express express = getModel(Express.class);
        express.save();
        setFlash("message", getRes().get("express.create.success"));
        redirect("/express");
    }

    @Override
    public void edit() {
        Express express = Express.dao.findById(getParaToInt());
        if (express == null) {
            renderError(404);
            return;
        }
        setAttr("express", express);
    }

    @Override
    public void update() {
        Express express = getModel(Express.class);
        express.update();
        setFlash("message", getRes().get("express.update.success"));
        redirect("/express");
    }

    @Override
    public void delete() {
        Express express = Express.dao.findById(getParaToInt());
        if (express == null) {
            renderError(404);
            return;
        }
        express.delete();
        setFlash("message", getRes().get("express.delete.success"));
        redirect("/express");
    }
}
