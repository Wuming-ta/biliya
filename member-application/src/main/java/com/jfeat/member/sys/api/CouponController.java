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

package com.jfeat.member.sys.api;

import com.google.common.collect.Maps;
import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.identity.model.User;
import com.jfeat.member.model.Coupon;
import com.jfeat.member.model.CouponTakenRecord;
import com.jfeat.member.service.CouponStrategyService;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

import java.util.List;
import java.util.Map;

/**
 * Created by jackyhuang on 16/11/29.
 */
@ControllerBind(controllerKey = "/sys/rest/coupon")
public class CouponController extends RestController {

    private CouponStrategyService strategyService = new CouponStrategyService();

    /**
     * 根据分享代码领取优惠券
     * POST /sys/rest/coupon
     * <p/>
     * {
     * "code": "fsfefaf",
     * "user_id": 12,
     * "clear_notify": true
     * }
     * <p/>
     * return:
     {
     "status_code": 0,
     "data": {
     "coupon_taken_records": [
     {
     "share_id": 1,
     "user_id": 1,
     "name": "Administrator",
     "created_date": "2016-11-28 10:43:08",
     "message": "又有券可用了。",
     "coupon_value": 6
     }
     ],
     "coupons": [
     {
     "code": "1982dbcf-442a-4111-923f-6b20b67eb31e",
     "description": null,
     "type": "ORDER",
     "display_name": "式",
     "valid_date": "2016-12-01",
     "money": 4,
     "user_id": 1,
     "name": "aaaa",
     "created_date": "2016-11-28",
     "id": 1,
     "status": "ACTIVATION"
     },
     {
     "code": "0d0cf6c8-58c4-4df7-91e3-a14e74046b97",
     "description": null,
     "type": "ORDER",
     "display_name": "33",
     "valid_date": "2016-12-02",
     "money": 2,
     "user_id": 1,
     "name": "发啊发",
     "created_date": "2016-11-28",
     "id": 2,
     "status": "ACTIVATION"
     }
     ],
     "coupon_value": 6
     }
     }
     */
    public void save() {
        Map<String, Object> map = convertPostJsonToMap();
        String shareCode = (String) map.get("code");
        Integer userId = (Integer) map.get("user_id");
        Boolean clearNotify = (Boolean) map.get("clear_notify");
        if (StrKit.isBlank(shareCode)) {
            renderFailure("code.is.required");
            return;
        }

        User user = User.dao.findById(userId);
        if (user == null) {
            renderFailure("invalid.user");
            return;
        }

        Ret ret = strategyService.userTakeCouponByShareCode(shareCode, user.getId());
        Map<String, Object> result = Maps.newHashMap();
        if (BaseService.isSucceed(ret)) {
            if (clearNotify != null && clearNotify) {
                //分享红包后会有红包提示弹出, 那么再进入首页就不应该再提示了。
                strategyService.doCouponNotify(userId);
            }
            List<Coupon> coupons = ret.get("coupons");
            for (Coupon coupon : coupons) {
                coupon.remove(Coupon.Fields.COND.toString());
                coupon.remove(Coupon.Fields.ATTRIBUTE.toString());
            }
            result.put("coupons", coupons);
            CouponTakenRecord couponTakenRecord = ret.get("coupon_taken_record");
            result.put("coupon_value", couponTakenRecord.getCouponValue());
            List<CouponTakenRecord> couponTakenRecords = strategyService.findCouponTakenRecord(shareCode);
            result.put("coupon_taken_records", couponTakenRecords);
            renderSuccess(result);
            return;
        }
        logger.debug("ret = {}", ret.getData());

        result.put("message", BaseService.getMessage(ret));
        result.put("coupon_taken_records", strategyService.findCouponTakenRecord(shareCode));
        renderSuccess(result);
    }
}
