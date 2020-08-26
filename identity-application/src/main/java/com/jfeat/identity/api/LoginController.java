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
import com.jfeat.core.RestController;
import com.jfeat.ext.plugin.validation.Validation;
import com.jfeat.identity.model.Role;
import com.jfeat.identity.model.User;
import com.jfeat.identity.authc.AccessToken;
import com.jfeat.identity.service.PermissionCache;
import com.jfeat.identity.service.UserService;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.*;

/**
 * Created by ehngjen on 9/2/2015.
 */
@ControllerBind(controllerKey = "/rest/login")
public class LoginController extends RestController {

    private UserService userService = Enhancer.enhance(UserService.class);

    /**
     * post {
     * "username": "login_name or phone or weixin",
     * "password": "the_password",
     * "captcha": "123456"
     * }
     * return: <pre>
     {
         "statusCode": 1,
         "data": {
            "access_token": "eyJ0b2tlbiI6IjM5NTk0YWQ4OTgyMzdkYTU2YzE2N2Y4MTVjZWMzMTI1NTAwYzQ4N2UiLCJsb2dpbl9uYW1lIjoiYWRtaW4ifQ=="
         }
     }
     * </pre>
     */
    @Before({Tx.class})
    @Override
    public void save() {
        Map<String, Object> maps = convertPostJsonToMap();
        String username = (String) maps.get("username");
        String password = (String) maps.get("password");
        String phone = (String) maps.get("phone");
        String captcha = (String) maps.get("captcha");

        // phone, captcha
        if (StrKit.notBlank(phone) && StrKit.notBlank(captcha) && !CaptchaKit.verifyCode(phone, captcha)) {
            renderFailure("captcha.invalid");
            return;
        }

        User user = User.dao.findByLoginName(username);
        if (user == null) {
            user = User.dao.findByPhone(username);
        }

        if (StrKit.notBlank(phone)) {
            user =  User.dao.findByPhone(phone);
        }

        if (user == null) {
            renderFailure("invalid.username");
            return;
        }

        // username, password
        if (StrKit.isBlank(phone) && (StrKit.isBlank(password) || !user.verifyPassword(password))) {
            renderFailure("incorrect.password");
            return;
        }

        // phone, password
        if (StrKit.notBlank(phone) && StrKit.isBlank(captcha) && (StrKit.isBlank(password) || !user.verifyPassword(password))) {
            renderFailure("incorrect.password");
            return;
        }

        //user.resetTokenSalt();
        user.resetTokenExpiredDate();
        user.setLastLoginDate(new Date());

        Map<String, Object> result = new HashMap<>();
        result.put("access_token", AccessToken.getAccessToken(user));

        //set to blank as we are not going to change password.
        user.setPassword("");
        user.update();

        List<String> permissions = new ArrayList<>();
        for (Role role : user.getRoles()) {
            permissions.addAll(role.getPermissionList());
        }
        result.put("permissions", permissions);

        PermissionCache.me().cache(user.getLoginName(), permissions);

        renderSuccess(result);
    }
}
