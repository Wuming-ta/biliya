package com.jfeat.wechat.api;

import com.jfeat.common.AuthConfigHolder;
import com.jfeat.core.BaseController;
import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.http.utils.HttpUtils;
import com.jfeat.identity.authc.AccessToken;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.DefaultRole;
import com.jfeat.identity.service.PermissionCache;
import com.jfeat.identity.service.UserService;
import com.jfeat.identity.subject.AttemptingUpdateInviterSubject;
import com.jfeat.kit.EmojiFilterKit;
import com.jfeat.util.DrawKit;
import com.jfeat.wechat.api.fix.FixWxaUserApi;
import com.jfeat.wechat.config.WechatConfig;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.kit.RandomKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import com.jfinal.wxaapp.api.WxaQrcodeApi;
import com.jfinal.wxaapp.api.WxaUserApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/5/18
 */
@ControllerBind(controllerKey = "/rest/login_wxa")
public class WxaLoginController extends RestController {

    private static Logger logger = LoggerFactory.getLogger(WxaLoginController.class);

    private UserService userService = Enhancer.enhance(UserService.class);

    /**
     * POST /rest/login_wxa
     * 1. 通过 wx.login 拿到code
     * 2。 通过 wx.getUserInfo 拿到 encryptedData, iv
     */
    @Override
    @Validation(rules = { "code = required" })
    public void save() {
        Map<String, Object> maps = convertPostJsonToMap();
        String code = (String) maps.get("code");
        String encryptedData = (String) maps.get("encryptedData");
        String iv = (String) maps.get("iv");
        String invitationCode = (String) maps.get("inviteCode");

        // 1. 根据code 取 sessionkey
        WxaUserApi wxaUserApi = new FixWxaUserApi();
        ApiResult sessionKeyResult = wxaUserApi.getSessionKey(code);
        System.out.println(sessionKeyResult);
        logger.debug("jscode2session result = {}", JsonKit.toJson(sessionKeyResult));

        // 返回{"session_key":"nzoqhc3OnwHzeTxJs+inbQ==","expires_in":2592000,"openid":"oVBkZ0aYgDMDIywRdgPW8-joxXc4"}
        if (!sessionKeyResult.isSucceed()) {
            logger.error("jscode2session failed: errorCode = {}, errorMsg = {}", sessionKeyResult.getErrorCode(), sessionKeyResult.getErrorMsg());
            renderFailure("invalid.code");
            return;
        }

        String openid = sessionKeyResult.get("openid");
        String sessionKey = sessionKeyResult.get("session_key");

        User user = User.dao.findByWxaOpenid(openid);
        if (user != null) {
            logger.debug("user found for wxa openid = {}", openid);
            renderSuccess(loginSuccess(user));
            return;
        }

        if (StrKit.isBlank(encryptedData) || StrKit.isBlank(iv)) {
            logger.debug("user not found for wxa openid {}, and encryptedData/iv is empty. then return 4001 for auth required.", openid);
            Map<String, Object> result = new HashMap<>();
            result.put("status_code", 4001);
            result.put("message", "auth.required");
            this.renderJson(result);
            return;
        }

        // 2。 根据sesionkey和encryptedData取userinfo
        ApiResult userInfoResult = wxaUserApi.getUserInfo(sessionKey, encryptedData, iv);
        logger.debug("getuserInfo result = {}", JsonKit.toJson(userInfoResult));

        if (!userInfoResult.isSucceed()) {
            logger.error("getUserInfo failed: errorCode = {}, errorMsg = {}", userInfoResult.getErrorCode(), userInfoResult.getErrorMsg());
            renderFailure("get.user.info.failure");
            return;
        }

        String unionid = userInfoResult.get("unionId");
        String avatar = userInfoResult.get("avatarUrl");
        String username = EmojiFilterKit.replaceEmoji(userInfoResult.get("nickName"));
        Integer sex = userInfoResult.get("gender");

        if (StrKit.notBlank(unionid)) {
            user = User.dao.findByWxUnionid(unionid);
            logger.debug("user is {} for unionid {}", JsonKit.toJson(user), unionid);
        }
        if (user == null) {
            user = User.dao.findByWxaOpenid(openid);
            logger.debug("user is {} for openid {}", JsonKit.toJson(user), openid);
        }
        if (user == null) {
            logger.info("User not found for openid {}", openid);
            if (!WxConfig.getAutoReg()) {
                Map<String, Object> result = new HashMap<>();
                Map<String, Object> data = new HashMap<>();
                data.put("unionid", unionid);
                data.put("openid", openid);
                result.put("status_code", 1);
                result.put("data", data);
                result.put("message", "user.not.found");
                this.renderJson(result);
                return;
            }

            User newUser = new User();
            newUser.setLoginName(openid);
            newUser.setAvatar(avatar);
            newUser.setName(StrKit.notBlank(username) ? username : openid);
            newUser.setWxaOpenid(openid);
            newUser.setWxUnionid(unionid);
            newUser.setSex(sex);
            newUser.setAppUser(User.APP_USER);
            newUser.setPassword(RandomKit.randomMD5Str());
            Integer roleId = DefaultRole.me().getRoleProvider().getDefault().getId();
            Integer[] roles = new Integer[] { roleId };
            Ret ret = userService.createUser(newUser, roles);
            if (!BaseService.isSucceed(ret)) {
                logger.error("register failure, openid={}, ret={}", openid, ret.getData());
                renderFailure("register.failure");
                return;
            }
            logger.info("auto reg user: {}", newUser);

            user = User.dao.findByWxaOpenid(openid);
        }

        // 还没有邀请人，且邀请码不是自己的
        if (user.getInviterId() == null
                && StrKit.notBlank(invitationCode)
                && !invitationCode.equals(user.getInvitationCode())) {

            // 配置项配置了允许点邀请链接后重新绑定 true
            // 或者 配置false 且 用户手机号为空
            if (AuthConfigHolder.me().isAllowInviterReassign() || StrKit.isBlank(user.getPhone())) {
                User inviter = User.dao.findByInvitationCode(invitationCode);
                User u = inviter;
                boolean found = false;
                while (u != null && !found) {
                    if (u.getId().equals(user.getId())) {
                        found = true;
                    }
                    u = u.getInviter();
                }
                if (inviter != null && !found) {
                    logger.debug("AttemptingUpdateInviter: userId = {}, inviterId = {}", user.getId(), inviter.getId());
                    new AttemptingUpdateInviterSubject(user.getId(), inviter.getId()).notifyObserver();
                }
            }
        }

        if (StrKit.isBlank(user.getWxaOpenid())) {
            user.setWxaOpenid(openid);
        }
        if (StrKit.isBlank(user.getWxUnionid())) {
            user.setWxUnionid(unionid);
        }
        renderSuccess(loginSuccess(user));
    }

    private Map<String, Object> loginSuccess(User user) {
        user.resetTokenExpiredDate();
        user.setLastLoginDate(new Date());
        //set to blank as we are not going to change password.
        user.setPassword("");
        String invitationUrlPrefix = WechatConfig.me().getInvitationUrlPrefix();
        if (StrKit.isBlank(user.getInvitationQrcodeUrl()) && StrKit.notBlank(invitationUrlPrefix)) {
            String qrCode = invitationUrlPrefix + "?inviteCode=" + user.getInvitationCode();
            try {
                //带参二维码只有 100000 个，请谨慎调用
                WxaQrcodeApi wxaQrcodeApi = new WxaQrcodeApi();
                InputStream inputStream = wxaQrcodeApi.createQrcode(qrCode);
                String qrCodeUrl = DrawKit.upload(inputStream);
                user.setInvitationQrcodeUrl(qrCodeUrl);
                user.setInvitationQrcode(qrCode);
            } catch (IOException ex) {
                logger.error("create qrcode error. " + ex.getMessage());
            }
        }
        user.updateWithoutNotify();

        user = User.dao.findById(user.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("openid", user.getWxaOpenid());
        result.put("unionid", user.getWxUnionid());
        result.put("phone", user.getPhone());
        result.put("access_token", AccessToken.getAccessToken(user));

        PermissionCache.me().cache(user.getLoginName());
        return result;
    }
}
