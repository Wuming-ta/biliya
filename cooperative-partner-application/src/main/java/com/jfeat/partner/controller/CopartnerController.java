package com.jfeat.partner.controller;

import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.partner.model.Copartner;
import com.jfeat.partner.model.CopartnerSettlement;
import com.jfeat.partner.model.base.CopartnerSettlementBase;
import com.jfeat.partner.model.param.CopartnerParam;
import com.jfinal.aop.Before;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/8/22
 */
public class CopartnerController extends BaseController {

    @Override
    @RequiresPermissions("physical.seller.view")
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String name = getPara("name");
        String status = getPara("status");
        CopartnerParam param = new CopartnerParam(pageNumber, pageSize);
        param.setName(name).setStatus(status);
        setAttr("copartners", Copartner.dao.paginate(param));
        keepPara();
    }

    @RequiresPermissions("physical.seller.view")
    @Before(Flash.class)
    public void detail() {
        Copartner copartner = Copartner.dao.findById(getParaToInt());
        if (copartner == null) {
            renderError(404);
            return;
        }

        List<CopartnerSettlement> settlements = CopartnerSettlement.dao.findByField(CopartnerSettlement.Fields.COPARTNER_ID.toString(),
                copartner.getId(),
                null,
                new String[] { CopartnerSettlement.Fields.ID.toString() });

        setAttr("copartner", copartner);
        setAttr("settlements", settlements);
        keepPara();
    }

    @RequiresPermissions("physical.seller.edit")
    @Before(Flash.class)
    public void block() {
        String returnUrl = getPara("returnUrl", "/copartner");

        Copartner copartner = Copartner.dao.findById(getParaToInt());
        if (copartner == null) {
            renderError(404);
            return;
        }

        copartner.setStatus(Copartner.Status.BLOCKED.toString());
        copartner.update();
        redirect(returnUrl);
    }

    @RequiresPermissions("physical.seller.edit")
    @Before(Flash.class)
    public void unblock() {
        String returnUrl = getPara("returnUrl", "/copartner");

        Copartner copartner = Copartner.dao.findById(getParaToInt());
        if (copartner == null) {
            renderError(404);
            return;
        }

        copartner.setStatus(Copartner.Status.NORMAL.toString());
        copartner.update();
        redirect(returnUrl);
    }
}

