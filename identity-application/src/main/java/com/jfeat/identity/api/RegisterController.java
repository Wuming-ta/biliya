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

package com.jfeat.identity.api;

import com.jfeat.captcha.CaptchaKit;
import com.jfeat.core.BaseService;
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.DefaultRole;
import com.jfeat.identity.service.UserService;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * register user.
 * Created by ehngjen on 9/2/2015.
 */
@ControllerBind(controllerKey = "/rest/register")
public class RegisterController extends RestController {

    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);

    /**
     * post {
     * "username": "login name",
     * "password": "the_password",
     * "openid": "xxxx",  //weixin openid
     * "unionid": "xxx",  // weixin unionid
     * "phone": "phone number",
     * "captcha": "123456",
     * "invite_code": "sfdsfsdf"
     * }
     */
    @Validation(rules = { "password = required" })
    @Before({Tx.class})
    @Override
    public void save() {
        Map<String, Object> maps = convertPostJsonToMap();
        String openid = (String) maps.get("openid");
        String unionid = (String) maps.get("unionid");
        String loginName = (String) maps.get("username");
        String phone = (String) maps.get("phone");
        String password = (String) maps.get("password");
        String captcha = (String) maps.get("captcha");
        String inviteCode = (String) maps.get("invite_code");

        User user = User.dao.findByLoginName(loginName);
        if (user != null) {
            renderFailure("username.already.exist");
            return;
        }
        user = User.dao.findByPhone(phone);
        if (user != null) {
            renderFailure("phone.already.exist");
            return;
        }

        if (StrKit.notBlank(phone) && StrKit.isBlank(captcha)) {
            renderFailure("captcha.is.required");
            return;
        }

        if (StrKit.notBlank(phone) && StrKit.notBlank(captcha) && !CaptchaKit.verifyCode(phone, captcha)) {
            renderFailure("captcha.invalid");
            return;
        }

        if (StrKit.isBlank(loginName)) {
            loginName = phone;
        }

        User newUser = new User();
        newUser.setLoginName(loginName);
        newUser.setName(loginName);
        newUser.setPhone(phone);
        newUser.setPassword(password);
        newUser.setAppUser(User.APP_USER);

        if (StrKit.notBlank(inviteCode)) {
            User inviter = User.dao.findByInvitationCode(inviteCode);
            if (inviter != null) {
                newUser.setInviterId(inviter.getId());
            }
        }

        UserService userService = new UserService();
        Integer roleId = DefaultRole.me().getRoleProvider().getDefault().getId();
        Integer[] roles = new Integer[] { roleId };
        Ret ret = userService.createUser(newUser, roles);

        if (BaseService.isSucceed(ret)) {
            renderSuccessMessage("register.success");
        }
        else {
            renderFailure("register.failure");
        }
    }
}
