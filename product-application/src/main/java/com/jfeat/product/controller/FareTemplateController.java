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

package com.jfeat.product.controller;

import com.google.common.collect.Lists;
import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.pcd.model.Pcd;
import com.jfeat.product.model.CarryMode;
import com.jfeat.product.model.FareTemplate;
import com.jfeat.product.model.InclPostageProviso;
import com.jfeat.product.service.PostageService;
import com.jfeat.product.validator.FareTemplateExistingValidator;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * Created by jackyhuang on 16/8/26.
 */
@RequiresPermissions("fare_template.edit")
public class FareTemplateController extends BaseController {

    private PostageService postageService = Enhancer.enhance(PostageService.class);

    @Before(Flash.class)
    public void index() {
        setAttr("templates", FareTemplate.dao.findAll());
    }

    public void add() {
        setAttr("carryWays", CarryMode.CarryWay.values());
        setAttr("valuationModels", FareTemplate.ValuationModel.values());
        setAttr("inclPostageTypes", InclPostageProviso.InclPostageType.values());
        FareTemplate template = new FareTemplate();
        template.setValuationModel(FareTemplate.ValuationModel.PIECE.getValue());
        template.setIsInclPostage(FareTemplate.InclPostage.YES.getValue());
        template.setIsInclPostageByIf(FareTemplate.InclPostageByIf.NO.getValue());
        setAttr("template", template);
    }

    @Before(Tx.class)
    public void save() {
        FareTemplate fareTemplate = getModel(FareTemplate.class);
        List<CarryMode> carryModes = getModels(CarryMode.class);
        List<InclPostageProviso> inclPostageProvisos = getModels(InclPostageProviso.class);
        postageService.createFareTemplate(fareTemplate, carryModes, inclPostageProvisos);

        redirect("/fare_template");
    }

    @Before(FareTemplateExistingValidator.class)
    public void edit() {
        setAttr("template", FareTemplate.dao.findById(getParaToInt()));
        setAttr("carryWays", CarryMode.CarryWay.values());
        setAttr("valuationModels", FareTemplate.ValuationModel.values());
        setAttr("inclPostageTypes", InclPostageProviso.InclPostageType.values());
    }

    @Before({FareTemplateExistingValidator.class, Tx.class})
    public void update() {
        FareTemplate fareTemplate = getModel(FareTemplate.class);
        List<CarryMode> carryModes = getModels(CarryMode.class);
        List<InclPostageProviso> inclPostageProvisoes = getModels(InclPostageProviso.class);
        postageService.updateFareTemplate(fareTemplate, carryModes, inclPostageProvisoes);
        redirect("/fare_template");
    }

    @Before(FareTemplateExistingValidator.class)
    public void delete() {
        FareTemplate fareTemplate = FareTemplate.dao.findById(getParaToInt());
        if (fareTemplate.isInUsed()) {
            setFlash("message", getRes().get("fare_template.delete.fail.inused"));
            redirect("/fare_template");
            return;
        }
        fareTemplate.delete();
        setFlash("message", getRes().get("fare_template.delete.success"));
        redirect("/fare_template");
    }

    @Before({FareTemplateExistingValidator.class, Tx.class})
    public void duplicate() {
        FareTemplate template = postageService.duplicateFareTemplate(getParaToInt());
        redirect("/fare_template/edit/" + template.getId());
    }

    public void ajaxPcd() {
        renderJson(Pcd.dao.findAllByCache());
    }
}
