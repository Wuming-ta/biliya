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
@ControllerBind(controllerKey = "/rest/admin/coupon")
public class ConponController extends RestController {

    private CouponService couponService = new CouponService();

    /**
     * 发券
     * POST { "phone": "13800000001", "couponTypeIds": [ 12 ] }
     */
    @Override
    @RequiresPermissions("coupon.edit")
    @Validation(rules = { "phone = required", "couponTypeIds = required" })
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        String phone = (String) map.get("phone");
        User user = User.dao.findByPhone(phone);
        if (user == null) {
            renderFailure("user.not.found");
            return;
        }
        List<Integer> couponTypeIds = (List<Integer>) map.get("couponTypeIds");
        if (couponTypeIds != null && !couponTypeIds.isEmpty()) {
            couponTypeIds.forEach(id -> {
                CouponType couponType = CouponType.dao.findById(id);
                couponService.createCoupon(user.getId(), couponType, Coupon.Source.SYSTEM, true, null);
            });
        }
        renderSuccessMessage("ok");
    }
}
