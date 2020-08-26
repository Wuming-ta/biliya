package com.jfeat.partner.controller;

import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.partner.model.Agent;
import com.jfeat.partner.model.Alliance;
import com.jfeat.partner.service.AllianceService;
import com.jfeat.pcd.model.Pcd;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

/**
 * @author jackyhuang
 * @date 2019/10/27
 */
public class AllianceController extends BaseController {

    private AllianceService allianceService = Enhancer.enhance(AllianceService.class);

    @Override
    @RequiresPermissions("CooperativePartnerApplication.view")
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        setAttr("alliances", Alliance.dao.paginate(pageNumber, pageSize));
    }

    @RequiresPermissions("CooperativePartnerApplication.view")
    @Before(Flash.class)
    public void assignAllianceShip() {
        String returnUrl = getPara("returnUrl", "/seller");
        Integer userId = getParaToInt("userId");
        Alliance alliance = Alliance.dao.findByUserId(userId);
        Ret ret = allianceService.assignAllianceShip(userId);
        logger.debug("assign alliance ship ret: {}", ret.getData());
        redirect(returnUrl);
    }

    @RequiresPermissions("CooperativePartnerApplication.view")
    @Before(Flash.class)
    public void assignStockholderShip() {
        String returnUrl = getPara("returnUrl", "/seller");
        Integer userId = getParaToInt("userId");
        Alliance alliance = Alliance.dao.findByUserId(userId);
        Ret ret = allianceService.assignStockholderShip(userId);
        logger.debug("assign stockholder ship ret: {}", ret.getData());
        redirect(returnUrl);
    }
}
