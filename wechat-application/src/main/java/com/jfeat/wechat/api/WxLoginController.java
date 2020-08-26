package com.jfeat.wechat.api;

import com.jfeat.common.AuthConfigHolder;
import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.authc.AccessToken;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.DefaultRole;
import com.jfeat.identity.service.PermissionCache;
import com.jfeat.identity.service.UserService;
import com.jfeat.identity.subject.AttemptingUpdateInviterSubject;
import com.jfeat.wechat.config.WxConfig;
import com.jfeat.wechat.sdk.api.AuthResult;
import com.jfeat.wechat.sdk.api.SnsApi;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.kit.RandomKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信端登录
 * @author jackyhuang
 * @date 2018/5/23
 */
@ControllerBind(controllerKey = "/rest/")
public class WxLoginController extends RestController {

    private UserService userService = Enhancer.enhance(UserService.class);

    @Override
    @Validation(rules = { "access_token = required, openid = required" })
    public void save() {
        Map<String, Object> maps = convertPostJsonToMap();
        String accessToken = (String) maps.get("access_token");
        String openid = (String) maps.get("openid");
        String unionid = (String) maps.get("unionid");
        String nickname = (String) maps.get("nickname");
        String avatar = (String) maps.get("avatar");
        Integer sex = (Integer) maps.get("sex");
        String invitationCode = (String) maps.get("invitation_code");
        String storeCode = (String) maps.get("store_code");
        String assistantCode = (String) maps.get("assistant_code");
        String cabinCode = (String) maps.get("cabin_code");

        AuthResult authResult = SnsApi.validateAccessToken(accessToken, openid);
        if (!authResult.isSucceed()) {
            logger.error("wx login failed. code = {}, error = {}", authResult.getErrorCode(), authResult.getErrorMsg());
            renderFailure(authResult.getErrorMsg());
            return;
        }

        User user = null;
        if (StrKit.notBlank(unionid)) {
            user = User.dao.findByWxUnionid(unionid);
            logger.debug("user is {} for unionid {}", JsonKit.toJson(user), unionid);
        }
        if (user == null) {
            user = User.dao.findByWeixin(openid);
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
            newUser.setName(openid);
            newUser.setWeixin(openid);
            newUser.setWxUnionid(unionid);
            newUser.setAvatar(avatar);
            newUser.setSex(0);
            newUser.setStoreCode(storeCode);
            newUser.setAssistantCode(assistantCode);
            newUser.setCabinCode(cabinCode);
            if (StrKit.notBlank(avatar)) {
                newUser.setAvatar(avatar);
            }
            if (sex != null) {
                newUser.setSex(sex);
            }
            if (StrKit.notBlank(nickname)) {
                newUser.setName(nickname);
                newUser.setWechatName(nickname);
            }
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

            user = User.dao.findByWeixin(openid);
        }
        else {
            user.resetTokenExpiredDate();
            user.setLastLoginDate(new Date());

            //set to blank as we are not going to change password.
            user.setPassword("");
            user.setWeixin(openid);
            user.setWxUnionid(unionid);
            if (StrKit.notBlank(avatar)) {
                user.setAvatar(avatar);
            }
            if (sex != null) {
                user.setSex(sex);
            }
            if (StrKit.notBlank(nickname)) {
                user.setName(nickname);
                user.setWechatName(nickname);
            }

            if (StrKit.notBlank(storeCode) && StrKit.isBlank(user.getStoreCode())) {
                user.setStoreCode(storeCode);
            }
            if (StrKit.notBlank(assistantCode) && StrKit.isBlank(user.getAssistantCode())) {
                user.setAssistantCode(assistantCode);
            }
            if (StrKit.notBlank(cabinCode) && StrKit.isBlank(user.getCabinCode())) {
                user.setCabinCode(cabinCode);
            }
            user.update();
        }

        User originInviter = null;
        if (user.getInviterId() != null) {
            originInviter = user.getInviter();
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

        user = User.dao.findByWeixin(openid);
        user.put("unionid", unionid);
        user.put("openid", openid);
        user.put("access_token", AccessToken.getAccessToken(user));
        if (originInviter != null) {
            user.put("origin_inviter_id", originInviter.getId());
            user.put("origin_inviter_name", originInviter.getName());
            user.put("origin_inviter_invitation_code", originInviter.getInvitationCode());
        }

        PermissionCache.me().cache(user.getLoginName());

        renderSuccess(user);
    }
}
