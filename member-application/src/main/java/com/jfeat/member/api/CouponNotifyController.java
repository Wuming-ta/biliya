package com.jfeat.member.api;

import com.google.common.collect.Maps;
import com.jfeat.core.RestController;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.UserCouponNotify;
import com.jfeat.member.service.CouponService;
import com.jfeat.member.service.CouponStrategyService;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;

import java.util.Map;

/**
 * Created by kang on 2016/11/28.
 */
@ControllerBind(controllerKey = "/rest/coupon_notify")
public class CouponNotifyController extends RestController {
    private CouponStrategyService couponStrategyService = new CouponStrategyService();
    private CouponService couponService = new CouponService();

    /**
     * 查看该用户有没有未激活的优惠券需要通知
     * GET /rest/coupon_notify
     *
     * Return:
     * {
     *     "status_code": 1,
     *     "data": {
     *         "notify": true,  //前端需要通知用户
     *         "new_user": true, //新注册用户
     *         "coupon_count": 2, //当notify为true时才返回
     *         "coupon_value": 34, //当notify为true时才返回
     *         "is_user_followed": true, //用户是否关注公众号
     *         "has_unread_coupon": true, //表示用户有未读优惠券, 这时'个人中心'需要显示红点
     *         "activition_coupons": [
     *           {
     *               "id": 1,
     *               "name": "Coupon1",
     *               "type": "MARKETING_PIECE_GROUP",
     *               "status": "ACTIVATION"
     *           }
     *         ]
     *     }
     * }
     */
    @Before(CurrentUserInterceptor.class)
    public void index() {
        User currentUser = getAttr("currentUser");
        UserCouponNotify userCouponNotify = UserCouponNotify.dao.findFirstByUserId(currentUser.getId());
        Map<String, Object> result = Maps.newHashMap();
        result.put("is_user_followed", currentUser.getFollowed() == User.INFOLLOW_SUBSCRIBE);
        result.put("has_unread_coupon", false);
        result.put("notify", false);
        result.put("new_user", true);
        if (userCouponNotify != null) {
            result.put("coupon_count", userCouponNotify.getCouponCount());
            result.put("coupon_value", userCouponNotify.getCouponValue());
            result.put("notify", userCouponNotify.shouldNotify());
            result.put("new_user", userCouponNotify.getNotifyDate() == null);
            result.put("has_unread_coupon", userCouponNotify.getCouponCount() > 0);
        }
        result.put("activation_coupons", couponService.findActivationCoupon(currentUser.getId()));
        couponStrategyService.doCouponNotify(currentUser.getId());
        renderSuccess(result);
    }
}
