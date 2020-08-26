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
import com.jfeat.flash.Flash;
import com.jfeat.misc.model.CustomerServiceType;
import com.jfeat.misc.model.Feedback;
import com.jfinal.aop.Before;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by huangjacky on 16/6/17.
 */
public class CustomerServiceTypeController extends BaseController {

    @Override
    @RequiresPermissions(value = { "MiscApplication.view" }, logical = Logical.OR)
    @Before(Flash.class)
    public void index() {
        setAttr("types", CustomerServiceType.dao.findAll());
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void save() {
        CustomerServiceType type = getModel(CustomerServiceType.class);
        type.save();
        setFlash("message", getRes().get("misc.customer.service.type.create.success"));
        redirect("/customer_service_type");
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void update() {
        CustomerServiceType type = getModel(CustomerServiceType.class);
        type.update();
        setFlash("message", getRes().get("misc.customer.service.type.update.success"));
        redirect("/customer_service_type");
    }

    @Override
    @RequiresPermissions("MiscApplication.delete")
    public void delete(){
        CustomerServiceType type = CustomerServiceType.dao.findById(getParaToInt());
        if (type == null) {
            renderError(404);
            return;
        }
        type.delete();
        setFlash("message", getRes().get("misc.customer.service.type.delete.success"));
        redirect("/customer_service_type");
    }

}
