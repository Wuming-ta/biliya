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

package com.jfeat.mall.interceptor;

import com.jfeat.common.AccessTokenToken;
import com.jfeat.common.ShiroUser;
import com.jfeat.kit.EmojiFilterKit;
import com.jfeat.kit.Encodes;
import com.jfeat.mall.api.SellerApi;
import com.jfeat.mall.api.UserApi;
import com.jfeat.mall.api.response.CreateUserResponse;
import com.jfeat.mall.api.response.SellerResponse;
import com.jfeat.mall.config.SysConfig;
import com.jfeat.mall.config.WxConfig;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.api.SnsApi;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacky on 5/16/16.
 */

public class WeixinInterceptor implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(WeixinInterceptor.class);
    private static final String STATE = "PP";
    //private Controller controller;

    private boolean isSnsApiBase() {
        return false; //查询用户详细信息 sns_user_info
    }

    private boolean isWxClient(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent").toLowerCase();
        logger.debug("user-agent={}", userAgent);
        return userAgent.indexOf("micromessenger") > 0;
    }

    private void printLog(Controller controller) {
        HttpServletRequest request = controller.getRequest();
        logger.debug("requestURI={}", request.getRequestURI());
        logger.debug("queryString={}", request.getQueryString());
        logger.debug("requestURL={}", request.getRequestURL().toString());
    }

    @Override
    public void intercept(Invocation invocation) {
        Controller controller = invocation.getController();

        printLog(controller);


//        if (!isWxClient(controller.getRequest())) {
//            controller.renderError(500);
//            return;
//        }

        String currentInviteCode = controller.getPara("invite_code");
        Subject subject = SecurityUtils.getSubject();
        boolean needLogin = true;
        if (subject.isAuthenticated()) {
            needLogin = false;
            ShiroUser user = (ShiroUser) subject.getPrincipal();
            logger.debug("user already login. user={}", JsonKit.toJson(user));

            //邀请者为空, 点击了其他人的分享链接, 那么尝试更新其邀请者。
            if (StrKit.notBlank(currentInviteCode) && !currentInviteCode.equals(user.getInviteCode()) && user.getInviterId() == null) {
                logger.debug("invite_code={}, inviter_id is null", currentInviteCode);
                subject.logout();
                if (!attemptLogin(controller, user.getOpenid(), null, null, null, currentInviteCode)) {
                    needLogin = true;
                }
            }
        }

        if (!needLogin) {
            ShiroUser user = (ShiroUser) subject.getPrincipal();
            //没有invite_code查询参数, 添加上去; 和用户的invite_code不一样,替换
            if (StrKit.isBlank(currentInviteCode) || !currentInviteCode.equals(user.getInviteCode())) {
                logger.debug("redirecting with invite code, currentInviteCode={}, userInviteCode={}", currentInviteCode, user.getInviteCode());
                redirectWithInviteCode(controller, user.getInviteCode());
                return;
            }

            String[] fallback = controller.getParaValues("fallback");
            if (fallback != null) {
                logger.debug("replacing fallback url." + fallback[0]);
                redirectWithFallback(controller, fallback[0]);
                return;
            }

            Integer redirectCode = controller.getParaToInt("rdCode");
            if (redirectCode != null && redirectCode != 0 && redirectWithExternalLink(controller, redirectCode)) {
                logger.debug("redirected to external link.");
                return;
            }

            if (!SellerResponse.Data.SELLER.equals(user.getSellerStatus())) {
                logger.debug("user {} 's seller stauts is {}, query again. ", user.getLoginName(), user.getSellerStatus());
                SellerApi sellerApi = new SellerApi();
                String sellerStatus = sellerApi.sellerStatus(user.getId());
                user.setSellerStatus(sellerStatus);
                logger.debug("{} sellership query result is {}", user.getLoginName(), sellerStatus);
            }

            logger.debug("everything is fine. entering the main page.");
            controller.setAttr("user_id", user.getId());
            controller.setAttr("openid", user.getOpenid());
            controller.setAttr("access_token", user.getAccessToken());
            controller.setAttr("sellership", user.getSellerStatus());
            controller.setAttr("invite_code", user.getInviteCode());
            invocation.invoke();
            return;
        }

        // 1. 去微信服务器拿code
        String[] code = controller.getParaValues("code");
        String[] state = controller.getParaValues("state");
        if (!(code != null && state != null && STATE.equals(state[state.length - 1]))) {
            String appId = WxConfig.getAppId();
            String path;
            StringBuilder uri = new StringBuilder(WxConfig.getHost());
            uri.append(invocation.getActionKey());
            if (StrKit.notBlank(controller.getPara())) {
                uri.append("/");
                uri.append(controller.getPara());
            }
            if (StrKit.notBlank(controller.getRequest().getQueryString())) {
                uri.append("?");
                uri.append(controller.getRequest().getQueryString());
            }
            try {
                path = URLEncoder.encode(uri.toString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                controller.renderError(500);
                return;
            }
            String url = SnsAccessTokenApi.getAuthorizeURL(appId, path, STATE, isSnsApiBase());
            logger.debug("get code, redirect to {}", url);
            controller.redirect(url);
            return;
        }

        // 2. 根据code去微信服务器拿access-token
        String appId = WxConfig.getAppId();
        String appSecret = WxConfig.getAppSecret();
        SnsAccessToken accessToken = SnsAccessTokenApi.getSnsAccessToken(appId, appSecret, code[code.length - 1]);
        logger.debug("get access-token, result={}", accessToken.getJson());
        if (accessToken.getErrorCode() != null) {
            controller.renderError(500);
            return;
        }

        // 3. 去微信服务器拿用户信息
        String openid = accessToken.getOpenid();
        ApiResult result = SnsApi.getUserInfo(accessToken.getAccessToken(), accessToken.getOpenid());
        logger.debug("userInfo={}", result);

        if (result.isSucceed()) {
            String username = EmojiFilterKit.replaceEmoji(result.getStr("nickname"));
            String avatar = result.getStr("headimgurl");
            Integer sex = result.getInt("sex");
            if (attemptLogin(controller, openid, username, avatar, sex, currentInviteCode)) {
                return;
            }
        }

        // there is something wrong...
        controller.renderError(500);
    }

    private boolean redirectWithExternalLink(Controller controller, Integer redirectCode) {
        logger.debug("atempting redirect to external link.");
        String url = SysConfig.getWxInvitationQrRedirectUrl();
        if (StrKit.notBlank(url)) {
            logger.debug("redirecting to {}", url);
            controller.redirect(url);
            return true;
        }
        return false;
    }

    private void redirectWithFallback(Controller controller, String fallback) {
        StringBuilder fallbackUrl = new StringBuilder("#/");
        String separator = "";
        for (String param : fallback.split("-")) {
            fallbackUrl.append(separator);
            fallbackUrl.append(param);
            separator = "/";
        }
        StringBuffer url = controller.getRequest().getRequestURL();
        Map<String, String[]> queryString = new HashMap<>(controller.getParaMap());
        if (queryString.containsKey("fallback")) {
            queryString.remove("fallback");
        }
        queryString.remove("fb_redirect");
        queryString.put("fb_redirect", new String[] {"true"});
        String finalUrl = UrlUtil.buildUrlWithQueryString(url.toString(), queryString) + fallbackUrl;
        logger.debug("redirect to url: {}", finalUrl);
        controller.redirect(finalUrl);
    }

    private void redirectWithInviteCode(Controller controller, String myInviteCode) {
        StringBuffer url = controller.getRequest().getRequestURL();
        Map<String, String[]> queryString = new HashMap<>(controller.getParaMap());
        if (queryString.containsKey("code")) {
            queryString.remove("code");
        }
        if (queryString.containsKey("state")) {
            queryString.remove("state");
        }
        queryString.remove("invite_code");
        queryString.put("invite_code", new String[] {myInviteCode});
        String finalUrl = UrlUtil.buildUrlWithQueryString(url.toString(), queryString);
        logger.debug("redirect to url: {}", finalUrl);
        controller.redirect(finalUrl);
    }

    private boolean attemptLogin(Controller controller, String openid, String username, String avatar, Integer sex, String currentInviteCode) {
        CreateUserResponse.Data userData = getUser(openid, username, avatar, sex, currentInviteCode, WxConfig.getHost());
        String token = userData.getAccessToken();
        String myInviteCode = userData.getInviteCode();
        SellerApi sellerApi = new SellerApi();
        String sellerStatus = sellerApi.sellerStatus(userData.getUser_id());
        if (login(userData.getUser_id(), token, myInviteCode, sellerStatus, userData.getInviter_id())) {
            logger.debug("{} login success.", username);
            redirectWithInviteCode(controller, myInviteCode);
            return true;
        } else {
            logger.error("{} login failed.", username);
            return false;
        }
    }

    /**
     *
     * @param openid
     * @param username
     * @param avatar
     * @return access token
     */
    private CreateUserResponse.Data getUser(String openid, String username, String avatar, Integer sex, String inviteCode, String urlPrefix) {
        String invitationQrcode = urlPrefix + "/app?invite_code=";
        UserApi userApi = new UserApi();
        CreateUserResponse response = userApi.createUser(username,
                openid, //login_name
                openid, //weixin
                openid + new Date().getTime(),
                inviteCode,
                avatar,
                sex,
                invitationQrcode);
        logger.debug("create user result = {}", JsonKit.toJson(response));
        return response.getData();
    }

    private boolean login(Integer userId, String token, String myInviteCode, String sellerStatus, Integer inviterId) {

        //auto login
        try {
            String str = new String(Encodes.decodeBase64(token));
            Map<String, Object> map = com.jfeat.kit.JsonKit.convertToMap(str);
            String loginName = (String) map.get("login_name");
            String tokenString = (String) map.get("token");
            Subject currentUser = SecurityUtils.getSubject();
            AccessTokenToken kqdToken = new AccessTokenToken(userId, loginName, tokenString, token, myInviteCode, sellerStatus, inviterId);
            currentUser.login(kqdToken);
            //if no exception, that's it, we're done!
            return true;
        } catch (AuthenticationException ae) {
            //unexpected condition - error?
            logger.warn(ae.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
