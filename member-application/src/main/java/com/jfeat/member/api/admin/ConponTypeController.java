package com.jfeat.member.api.admin;

import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponType;
import com.jfeat.member.service.CouponService;
import com.jfinal.ext.route.ControllerBind;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/9/17
 */
@ControllerBind(controllerKey = "/rest/admin/coupon_type")
public class ConponTypeController extends RestController {

    @Override
    public void index() {
        renderSuccess(CouponType.dao.findEnabled());
    }
}
