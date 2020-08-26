package com.jfeat.partner.api;

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.partner.model.Copartner;
import com.jfeat.partner.model.Seller;
import com.jfeat.partner.service.CopartnerService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/8/22
 */
@ControllerBind(controllerKey = "/rest/copartner")
public class CopartnerController extends RestController {

    private CopartnerService copartnerService = Enhancer.enhance(CopartnerService.class);

    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        Seller seller = Seller.dao.findByUserId(currentUser.getId());
        if (seller == null || !copartnerService.isCopartner(seller.getId())) {
            renderFailure("not.a.copartner");
            return;
        }
        Copartner copartner = Copartner.dao.findBySellerId(seller.getId());
        List<Seller> children = copartner.getChildren();
        copartner.put("children", children);
        copartner.put("children_count", children.size());
        renderSuccess(copartner);
    }

    /**
     * 申请成为合伙人
     */
    @Override
    @Before({CurrentUserInterceptor.class })
    @Validation(rules = { "phone = required", "name = required", "address = required" })
    public void save() {
        User currentUser = getAttr("currentUser");
        Map<String, Object> map = convertPostJsonToMap();
        String phone = (String) map.get("phone");
        String name = (String) map.get("name");
        String address = (String) map.get("address");

        Seller seller = Seller.dao.findByUserId(currentUser.getId());
        if (seller == null || !(seller.isCrownShip() && seller.isPhysicalSeller()) ) {
            logger.debug("user is not seller. or is not physical crown.");
            renderFailure("not.physical.crown");
            return;
        }
        if (copartnerService.isCopartner(seller.getId())) {
            renderFailure("already.copartner");
            return;
        }

        if (copartnerService.isApplying(currentUser.getId())) {
            renderFailure("already.applying");
            return;
        }

        Ret ret = copartnerService.apply(seller.getId(), name, phone, address);
        if (!BaseService.isSucceed(ret)) {
            renderFailure(BaseService.getMessage(ret));
            return;
        }

        renderSuccessMessage("ok");
    }
}
