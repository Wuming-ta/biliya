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
import com.jfeat.misc.model.Faq;
import com.jfeat.misc.model.FaqType;
import com.jfinal.aop.Before;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * Created by jingfei on 2016/5/11.
 */
public class FaqController extends BaseController {

    @Override
    @RequiresPermissions("MiscApplication.view")
    @Before(Flash.class)
    public void index(){
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        Integer typeId = getParaToInt("typeId");
        setAttr("types",FaqType.dao.findAll());
        setAttr("faqs", Faq.dao.paginate(pageNumber, pageSize, typeId));
        keepPara();
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void add(){
        setAttr("faq", new Faq());
        setAttr("types", FaqType.dao.findAll());
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void save() {
        Faq faq = getModel(Faq.class);
        faq.save();
        setFlash("message", getRes().get("misc.faq.create.success"));
        redirect("/faq");
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void edit(){
        int faqId = getParaToInt();
        Faq faq = Faq.dao.findById(faqId);
        setAttr("faq", faq);
        setAttr("types", FaqType.dao.findAll());
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void update() {
        Faq faq = getModel(Faq.class);
        faq.update();
        setFlash("message", getRes().get("misc.faq.update.success"));
        redirect("/faq");
    }

    @Override
    @RequiresPermissions("MiscApplication.edit")
    public void delete(){
        Integer faqId = getParaToInt();
        Faq.dao.deleteById(faqId);
        setFlash("message", getRes().get("misc.faq.delete.success"));
        redirect("/faq");
    }

    @RequiresPermissions("MiscApplication.view")
    @Before(Flash.class)
    public void type(){
        setAttr("types",FaqType.dao.findAll());
    }

    @RequiresPermissions("MiscApplication.edit")
    public void updateType() {
        FaqType faqType = getModel(FaqType.class);
        faqType.update();
        setFlash("message", getRes().get("misc.faqType.update.success"));
        redirect("/faq/type");
    }

    @RequiresPermissions("MiscApplication.edit")
    public void saveType() {
        FaqType faqType = getModel(FaqType.class);
        faqType.save();
        setFlash("message", getRes().get("misc.faqType.create.success"));
        redirect("/faq/type");
    }

    @RequiresPermissions("MiscApplication.edit")
    public void deleteType(){
        Integer faqTypeId = getParaToInt();
        FaqType.dao.deleteById(faqTypeId);
        setFlash("message", getRes().get("misc.faqType.delete.success"));
        redirect("/faq/type");
    }

}
