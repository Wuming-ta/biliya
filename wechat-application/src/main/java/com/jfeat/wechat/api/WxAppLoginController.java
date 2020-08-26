package com.jfeat.wechat.api;

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.authc.AccessToken;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.DefaultRole;
import com.jfeat.identity.service.PermissionCache;
import com.jfeat.identity.service.UserService;
import com.jfeat.kit.EmojiFilterKit;
import com.jfeat.wechat.config.WxConfig;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.kit.RandomKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.api.SnsApi;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 移动应用app使用微信登录
 * @author jackyhuang
 * @date 2018/10/29
 */
@ControllerBind(controllerKey = "/rest/login_wxapp")
public class WxAppLoginController extends RestController {

    private UserService userService = Enhancer.enhance(UserService.class);

    /**
     * POST /rest/login_wxapp
     */
    @Override
    @Validation(rules = {"code = required"})
    public void save() {
        Map<String, Object> maps = convertPostJsonToMap();
        String code = (String) maps.get("code");
        String appId = WxConfig.getAppAppId();
        String appSecret = WxConfig.getAppAppSecret();
        SnsAccessToken accessToken = SnsAccessTokenApi.getSnsAccessToken(appId, appSecret, code);
        logger.debug("get access-token, result={}", accessToken.getJson());
        if (accessToken.getErrorCode() != null) {
            renderFailure(accessToken.getErrorCode());
            return;
        }

        // 3. 去微信服务器拿用户信息
        String openid = accessToken.getOpenid();
        ApiResult userInfoResult = SnsApi.getUserInfo(accessToken.getAccessToken(), openid);
        logger.debug("userInfo={}", userInfoResult);
        if (!userInfoResult.isSucceed()) {
            logger.error("wxapp login failed. code = {}, error = {}", userInfoResult.getErrorCode(), userInfoResult.getErrorMsg());
            renderFailure(userInfoResult.getErrorMsg());
            return;
        }

        String unionid = userInfoResult.getStr("unionid");
        String username = EmojiFilterKit.replaceEmoji(userInfoResult.getStr("nickname"));
        String avatar = userInfoResult.getStr("headimgurl");
        Integer sex = userInfoResult.getInt("sex");

        boolean isNewUser = false;
        User user = null;
        if (StrKit.notBlank(unionid)) {
            user = User.dao.findByWxUnionid(unionid);
            logger.debug("user is {} for unionid {}", JsonKit.toJson(user), unionid);
        }
        if (user == null) {
            user = User.dao.findByWxappOpenid(openid);
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

            isNewUser = true;
            User newUser = new User();
            newUser.setLoginName(openid);
            newUser.setName(openid);
            newUser.setWxappOpenid(openid);
            newUser.setWxUnionid(unionid);
            newUser.setAvatar(avatar);
            newUser.setSex(0);
            if (StrKit.notBlank(avatar)) {
                newUser.setAvatar(avatar);
            }
            if (sex != null) {
                newUser.setSex(sex);
            }
            if (StrKit.notBlank(username)) {
                newUser.setName(username);
                newUser.setWechatName(username);
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

            user = User.dao.findByWxappOpenid(openid);
        }

        if (!isNewUser && (user.getLastLoginDate() == null || System.currentTimeMillis() - user.getLastLoginDate().getTime() > 60 * 1000)) {
            user.resetTokenExpiredDate();
            user.setLastLoginDate(new Date());
        }
        //set to blank as we are not going to change password.
        user.setPassword("");
        user.setWxappOpenid(openid);
        user.setWxUnionid(unionid);
        if (StrKit.notBlank(avatar)) {
            user.setAvatar(avatar);
        }
        if (sex != null) {
            user.setSex(sex);
        }
        if (StrKit.notBlank(username)) {
            user.setName(username);
            user.setWechatName(username);
        }

        user.update();

        user = User.dao.findByWxappOpenid(openid);
        user.put("unionid", unionid);
        user.put("openid", openid);
        user.put("access_token", AccessToken.getAccessToken(user));

        PermissionCache.me().cache(user.getLoginName());

        renderSuccess(user);
    }
}
