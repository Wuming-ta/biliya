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

package com.jfeat.mall.config;

import com.jfeat.mall.api.SysConfigApi;
import com.jfeat.mall.api.response.SysConfigResponse;
import com.jfinal.kit.StrKit;

/**
 * Created by jackyhuang on 16/9/2.
 */
public class SysConfig extends BaseConfig {

    public static String getWxInvitationQrRedirectUrl() {
        return getResponseData("mall.invitationqr.redirecturl").getValue();
    }

    public static Boolean getPocketEnabled() {
        String value = getResponseData("coupon.share_link_enabled").getValue();
        if (StrKit.isBlank(value)) {
            value = "false";
        }
        return Boolean.valueOf(value);
    }

    public static Integer getPointExchangeRate() {
        return Integer.valueOf(getResponseData("mall.point_exchange_rate").getValue());
    }

    public static String getFollowUsUrl() {
        return getResponseData("follow.us.url").getValue();
    }

    public static String getApiUrl() {
        return getResponseData("misc.api_url").getValue();
    }

    public static String getPocketShareTitle() {
        return getResponseData("coupon.pocket_share_title").getValue();
    }

    public static String getPocketShareDesc() {
        return getResponseData("coupon.pocket_share_desc").getValue();
    }

    public static Boolean canApplyCrownship() {
        String value = getResponseData("partner.can_apply_crown").getValue();
        if (StrKit.isBlank(value)) {
            value = "false";
        }
        return Boolean.valueOf(value);
    }

    public static String getApplyPhysicalCrownText() {
        return getResponseData("partner.apply_crown_text").getValue();
    }

    public static String getApplyPhysicalSellerText() {
        return getResponseData("partner.create_physical_seller_text").getValue();
    }

    public static Boolean showProductCategoryMenu() {
        String value = getResponseData("mall.show_product_category_menu").getValue();
        if (StrKit.isBlank(value)) {
            value = "false";
        }
        return Boolean.valueOf(value);
    }

    public static Boolean allowPlatformSale() {
        String value = getResponseData("merchant.allow_platform_sale").getValue();
        if (StrKit.isBlank(value)) {
            value = "true";
        }
        return Boolean.valueOf(value);
    }
}
