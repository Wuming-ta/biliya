package com.jfeat.marketing.wholesale.controller;

import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.marketing.wholesale.model.WholesaleCategory;
import com.jfinal.aop.Before;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by kang on 2017/6/2.
 */
public class WholesaleCategoryController extends BaseController {

    @Before(Flash.class)
    @RequiresPermissions("marketing.wholesale.view")
    public void index() {
        setAttr("wholesaleCategories", WholesaleCategory.dao.findAll());
        keepPara();
    }

    @RequiresPermissions("marketing.wholesale.edit")
    public void save() {
        WholesaleCategory wholesaleCategory = getModel(WholesaleCategory.class);
        wholesaleCategory.save();
        setFlash("message", getRes().get("marketing.wholesale.wholesale_category.create.success"));
        redirect("/wholesale_category");
    }

    @RequiresPermissions("marketing.wholesale.edit")
    public void edit() {
        setAttr("wholesaleCategory", WholesaleCategory.dao.findById(getParaToInt()));
        keepPara();
    }

    @RequiresPermissions("marketing.wholesale.edit")
    public void update() {
        WholesaleCategory wholesaleCategory = getModel(WholesaleCategory.class);
        wholesaleCategory.update();
        setFlash("message", getRes().get("marketing.wholesale.wholesale_category.update.success"));
        redirect("/wholesale_category");
    }

    @RequiresPermissions("marketing.wholesale.delete")
    public void delete() {
        new WholesaleCategory().deleteById(getParaToInt());
        setFlash("message", getRes().get("marketing.wholesale.wholesale_category.delete.success"));
        redirect("/wholesale_category");
    }


}
