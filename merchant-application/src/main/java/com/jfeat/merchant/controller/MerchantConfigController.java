package com.jfeat.merchant.controller;

import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.merchant.model.SettledMerchant;
import com.jfeat.merchant.model.SettledMerchantSettlementProportion;
import com.jfeat.merchant.model.SettledMerchantType;
import com.jfeat.merchant.model.SettledTerm;
import com.jfeat.order.service.OrderService;
import com.jfinal.aop.Before;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * Created by kang on 2017/3/22.
 */
public class MerchantConfigController extends BaseController {

    @RequiresPermissions("merchant.manage")
    @Before(Flash.class)
    public void index() {
        setAttr("settledTerm", SettledTerm.dao.findDefault());
        setAttr("settledMerchantTypes", SettledMerchantType.dao.findAll());
        setAttr("settlementProportion", SettledMerchantSettlementProportion.dao.getDefault());
        keepPara();
    }

    @RequiresPermissions("merchant.manage")
    public void updateSettledTerm() {
        SettledTerm settledTerm = getModel(SettledTerm.class);
        settledTerm.update();
        redirect("/merchant_config");
    }

    @RequiresPermissions("merchant.manage")
    public void addSettledMerchantType() {

    }

    @RequiresPermissions("merchant.manage")
    public void saveSettledMerchantType() {
        SettledMerchantType settledMerchantType = getModel(SettledMerchantType.class);
        settledMerchantType.save();
        redirect("/merchant_config?active=settled_merchant_type");
    }

    @RequiresPermissions("merchant.manage")
    public void editSettledMerchantType() {
        setAttr("settledMerchantType", SettledMerchantType.dao.findById(getParaToInt()));
    }

    @RequiresPermissions("merchant.manage")
    public void updateSettledMerchantType() {
        SettledMerchantType settledMerchantType = getModel(SettledMerchantType.class);
        settledMerchantType.update();
        redirect("/merchant_config?active=settled_merchant_type");
    }

    @RequiresPermissions("merchant.manage")
    public void deleteSettledMerchantType() {
        SettledMerchantType settledMerchantType = SettledMerchantType.dao.findById(getParaToInt());
        if (settledMerchantType == null) {
            renderError(404);
            return;
        }
        List<SettledMerchant> settledMerchantList = SettledMerchant.dao.findByTypeId(settledMerchantType.getId());
        if (settledMerchantList.size() == 0) {
            settledMerchantType.delete();
        }
        else {
            setFlash("message", getRes().get("merchant.settled_merchant_type.has.children.delete.failed"));
        }
        redirect("/merchant_config?active=settled_merchant_type");
    }

    @RequiresPermissions("merchant.manage")
    public void updateSettlementProportion() {
        SettledMerchantSettlementProportion proportion = getModel(SettledMerchantSettlementProportion.class);
        proportion.update();
        redirect("/merchant_config?active=settled_merchant_settlement_proportion");
    }
}
