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

package com.jfeat.mall.controller;

import com.jfeat.mall.api.CouponApi;
import com.jfeat.mall.api.response.CouponResponse;
import com.jfeat.mall.config.SysConfig;
import com.jfeat.mall.config.WxConfig;
import com.jfeat.mall.interceptor.MerchantInterceptor;
import com.jfeat.mall.interceptor.WeixinInterceptor;
import com.jfeat.ui.MenuInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.JsonKit;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jacky on 5/5/16.
 */
@Clear({MenuInterceptor.class})
public class AppController extends ApiController {

    private static Logger logger = LoggerFactory.getLogger(AppController.class);

    private void config() {
        setAttr("api_url", SysConfig.getApiUrl());
        setAttr("wpay_url", WxConfig.getHost() + WPayController.WPAY_URL);
        setAttr("followus_url", SysConfig.getFollowUsUrl());
        setAttr("can_apply_crown", SysConfig.canApplyCrownship());
        setAttr("apply_physical_crown_text", SysConfig.getApplyPhysicalCrownText());
        setAttr("apply_physical_seller_text", SysConfig.getApplyPhysicalSellerText());
        setAttr("show_product_category_menu", SysConfig.showProductCategoryMenu());
    }

    @Before({WeixinInterceptor.class, MerchantInterceptor.class})
    public void index() {
        config();
    }

    /**
     * get http://ip/app/coupon?share_code=xxx
     * 点击链接领取优惠券
     */
    @Before({WeixinInterceptor.class, MerchantInterceptor.class})
    public void coupon() {
        String shareCode = getPara("share_code");
        Integer userId = getAttr("user_id");
        logger.debug("clicking coupon share link: user_id = {}, share_code = {}", userId, shareCode);
        CouponApi couponApi = new CouponApi();
        CouponResponse response = couponApi.take(userId, shareCode, false);
        logger.debug("take coupon result: {}", JsonKit.toJson(response));
        redirect("/app");
    }

//    /**
//     * FOR TESTING
//     */
//    public void pay_test() {
//        createToken("paytoken");
//    }
//    public void pay_test_save() {
//        String orderNumber = getPara("orderNumber");
//        if (validateToken("paytoken") && StrKit.notBlank(orderNumber)) {
//            OrderApi api = new OrderApi();
//            api.paidNotifyOrder(orderNumber, "WECHAT", "TEST" + new Date().getTime());
//        }
//        redirect("/app/pay_test");
//    }
}
