package com.jfeat.wechat.api;

import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.authc.AccessToken;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.DefaultRole;
import com.jfeat.identity.service.UserService;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.kit.RandomKit;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jackyhuang
 * @date 2018/5/18
 */
@ControllerBind(controllerKey = "/rest/register_wxa")
public class WxaRegisterController extends RestController {

    private UserService userService = Enhancer.enhance(UserService.class);

    /**
     * POST /rest/register_wxa
     * {
     *    "openid": "1345",
     *    "phone": "13800000000",
     *    "captcha": "134565",
     *    "unionid": "1234",
     *    "avatar": "xx",
     *    "name": "xx",
     *    "sex": 1,
     *    "invite_code": "xxxyy"
     *  }
     */
    @Override
    @Validation(rules = { "openid = required", "phone = required", "captcha = required" })
    public void save() {
        Map<String, Object> maps = convertPostJsonToMap();
        String unionid = (String) maps.get("unionid");
        String openid = (String) maps.get("openid");
        String phone = (String) maps.get("phone");
        String avatar = (String) maps.get("avatar");
        String name = (String) maps.get("name");
        String inviteCode = (String) maps.get("invite_code");
        Integer sex = (Integer) maps.get("sex");
        String captcha = (String) maps.get("captcha");

        User user = null;
        if (StrKit.notBlank(unionid)) {
            user = User.dao.findByWxUnionid(unionid);
        }
        if (user != null) {
            logger.debug("found user {} for unionid {}", user, unionid);
            if (StrKit.notBlank(user.getPhone()) && !phone.equals(user.getPhone())) {
                renderFailure("phone.not.equals");
                return;
            }
            if (StrKit.notBlank(user.getWxaOpenid())) {
                renderFailure("openid.already.exist");
                return;
            }
            updateUser(user, phone, openid, unionid, avatar, name);
            logger.debug("wxa register successful.");
            renderSuccess(genAccessToken(phone));
            return;
        }

        logger.debug("not user found for unionid {}, now check with phone {}", unionid, phone);
        user = User.dao.findByPhone(phone);
        if (user != null) {
            logger.debug("user {} found for phone {}, ", user, phone);
            updateUser(user, phone, openid, unionid, avatar, name);
            renderSuccess(genAccessToken(phone));
            return;
        }

        logger.debug("not user found for phone {}, now check with openid {}", phone, openid);
        user = User.dao.findByWxaOpenid(openid);
        if (user != null) {
            renderFailure("wxa.already.exist");
            return;
        }

        logger.debug("not user found for openid {}, now create a new one.", openid);
        User newUser = new User();
        newUser.setLoginName(phone);
        newUser.setName(StrKit.notBlank(name) ? name : phone);
        newUser.setAvatar(avatar);
        newUser.setSex(sex);
        newUser.setPhone(phone);
        newUser.setWxaOpenid(openid);
        newUser.setWxUnionid(unionid);
        newUser.setAppUser(User.APP_USER);
        newUser.setPassword(RandomKit.randomMD5Str());

        HttpServletRequest request = getRequest();
        String serverName = request.getServerName();
        newUser.setDomain(serverName);

        User inviter = User.dao.findByInvitationCode(inviteCode);
        if (inviter != null) {
            newUser.setInviterId(inviter.getId());
        }

        Integer roleId = DefaultRole.me().getRoleProvider().getDefault().getId();
        Integer[] roles = new Integer[] { roleId };
        Ret ret = userService.createUser(newUser, roles);
        if (!BaseService.isSucceed(ret)) {
            logger.error("register failure, openid={}, phone={}, ret={}", openid, phone, ret.getData());
            renderFailure("register.failure");
            return;
        }

        renderSuccess(genAccessToken(phone));
    }

    private void updateUser(User user, String phone, String openid, String unionid, String avatar, String name) {
        user.setPhone(phone);
        user.setLoginName(StrKit.notBlank(phone) ? phone : openid);
        user.setWxaOpenid(openid);
        user.setWxUnionid(unionid);
        user.setName(StrKit.notBlank(name) ? name : phone);
        user.setAvatar(avatar);
        user.setPassword("");
        userService.updateUser(user, null);
    }

    private Map<String, Object> genAccessToken(String phone) {
        User user = User.dao.findByPhone(phone);
        Map<String, Object> result = new HashMap<>();
        result.put("unionid", user.getWxUnionid());
        result.put("openid", user.getWxaOpenid());
        result.put("access_token", AccessToken.getAccessToken(user));
        return result;
    }
}
