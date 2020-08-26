package com.jfeat.mall.controller;

import com.jfeat.kit.DateKit;
import com.jfeat.mall.api.CouponApi;
import com.jfeat.mall.api.OrderApi;
import com.jfeat.mall.api.UserApi;
import com.jfeat.mall.api.response.*;
import com.jfeat.mall.config.SysConfig;
import com.jfeat.mall.config.WxConfig;
import com.jfeat.mall.controller.link.MarketingShareLink;
import com.jfeat.mall.controller.link.MarketingShareLinkHolder;
import com.jfeat.mall.interceptor.MerchantInterceptor;
import com.jfeat.mall.interceptor.WeixinInterceptor;
import com.jfeat.ui.MenuInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.math.BigDecimal;

/**
 * Created by jackyhuang on 16/8/15.
 */
@ControllerBind(controllerKey = "/payment/ppay", viewPath = "/ppay")
@Clear({MenuInterceptor.class})
public class PPayController extends PayBaseController {

    @Before({WeixinInterceptor.class, MerchantInterceptor.class})
    public void index() {
        String openid = getAttr("openid");
        if (StrKit.isBlank(openid)) {
            renderError(500);
            return;
        }

        String orderNumber = getPara();
        if (StrKit.isBlank(orderNumber)) {
            renderError(404);
            return;
        }

        UserApi userApi = new UserApi();
        UserResponse userResponse = userApi.getUser(openid);
        if (!userResponse.isSucceed()) {
            renderError(500);
            return;
        }

        OrderApi orderApi = new OrderApi();
        OrderResponse orderResponse = orderApi.getOrder(orderNumber);
        if (!orderResponse.isSucceed()) {
            renderError(500);
            return;
        }

        if (orderResponse.getData().getUser_id() != userResponse.getData().getId()) {
            renderError(500);
            return;
        }

        if (! (orderResponse.getData().getStatus().equals(CREATED_PAY_PENDING)
                || orderResponse.getData().getStatus().equals(PAID_CONFIRM_PENDING)
                || orderResponse.getData().getStatus().equals(CONFIRMED_DELIVER_PENDING)) ) {
            renderError(400);
            return;
        }

        setAttr("orderNumber", orderNumber);
        setAttr("payDate", DateKit.today("yyyy-MM-dd HH:mm:ss"));
        setAttr("totalFee", convertPrice(orderResponse.getData().getTotal_price()));
        setAttr("title", convertBody(orderResponse.getData().getDescription()));

        if (orderResponse.getData().getStatus().equals(CREATED_PAY_PENDING)) {
            MessageResponse response = orderApi.payOrder(orderNumber);
            if (!response.isSucceed()) {
                setAttr("failure", true);
                return;
            }
        }

        setAttr("success", true);
        setAttr("pocketEnabled", SysConfig.getPocketEnabled());

        setAttr("jsconfig", signShareConfig());
        setAttr("host", WxConfig.getHost());
        setAttr("shareTitle", SysConfig.getPocketShareTitle());
        setAttr("shareDesc", SysConfig.getPocketShareDesc());
        setAttr("shareImage", WxConfig.getHost() + POCKET_IMAGE);
        String shareCode = getShareCode(orderNumber);
        setAttr("shareCode", shareCode);
        setAttr("link", buildCouponShareLink(shareCode));

        //override when it is marketing
        if (isMarketing(orderResponse)) {
            MarketingShareLink marketingShareLink = MarketingShareLinkHolder.me().getMarketingShareLink(orderResponse.getData().getMarketing(), orderResponse.getData().getMarketing_id());
            setAttr("marketing", true);
            setAttr("shareTitle", marketingShareLink.buildTitle());
            setAttr("shareDesc", marketingShareLink.buildDescription());
            setAttr("shareImage", marketingShareLink.buildCover());
            setAttr("link", buildMarketingShareLink(marketingShareLink.buildLink()));
        }
    }

    @Before({WeixinInterceptor.class, MerchantInterceptor.class})
    public void pocket() {
        keepPara();
        String shareCode = getPara("share_code");
        Integer userId = getAttr("user_id");
        CouponApi couponApi = new CouponApi();
        CouponResponse response = couponApi.take(userId, shareCode, true);
        if (response.isSucceed()) {
            int couponCount = response.getData().getCoupons().size();
            setAttr("coupon_count", couponCount);
            setAttr("jsconfig", signShareConfig());
            setAttr("host", WxConfig.getHost());
            setAttr("shareTitle", SysConfig.getPocketShareTitle());
            setAttr("shareDesc", SysConfig.getPocketShareDesc());
            setAttr("link", buildCouponShareLink(shareCode));
            return;
        }
        redirect("/app");
    }

    @Override
    protected String convertPrice(double price) {
        Integer rate = SysConfig.getPointExchangeRate();
        return String.valueOf(BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(rate)).intValue());
    }
}
