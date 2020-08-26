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

package com.jfeat.identity.sys.api;

import com.google.zxing.WriterException;
import com.jfeat.core.PhotoGalleryConstants;
import com.jfeat.core.RestController;
import com.jfeat.identity.authc.AccessToken;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.DefaultRole;
import com.jfeat.identity.service.UserService;
import com.jfeat.identity.subject.AttemptingUpdateInviterSubject;
import com.jfeat.kit.DateKit;
import com.jfeat.kit.Encodes;
import com.jfeat.kit.JsonKit;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.kit.qrcode.QrcodeKit;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacky on 5/3/16.
 */
// use wechat-application api
@Deprecated
@ControllerBind(controllerKey = "/sys/rest/user")
public class UserController extends RestController {

    private UserService userService = Enhancer.enhance(UserService.class);

    /**
     * GET /sys/rest/user/openid
     */
    public void show() {
        String weixin = getPara();
        User user = User.dao.findByWeixin(weixin);
        renderSuccess(user);
    }

    /**
     * 根据微信号添加用户， 返回access_token
     * POST /sys/rest/user
     *
     * {
     *     "name":"abc",
     *     "login_name":"abc".
     *     "weixin":"abc",
     *     "password":"abc",
     *     "invite_code":"xxff",
     *     "invitation_qrcode": "http://www.kequandian.net/app/app?invite_code=", //optional, 邀请链接的前缀URL
     *     "followed": 0,  //optional, 0 关注, 1 未关注
     *     "follow_time": 53235342523424, //optional, 关注时间, integer
     *     "avatar":"http://sfdsfsfs" //optional
     * }
     *
     * return: <pre>
     {
     "statusCode": 0,
     "data": {
        "access_token": "eyJ0b2tlbiI6IjM5NTk0YWQ4OTgyMzdkYTU2YzE2N2Y4MTVjZWMzMTI1NTAwYzQ4N2UiLCJsb2dpbl9uYW1lIjoiYWRtaW4ifQ==",
        "invite_code": "sdfaffesf",
        "user_id": 1,
        "inviter_id": 2
     }
     }
     * </pre>
     */
    public void save() {
        String data = HttpKit.readData(getRequest());
        Map<String, Object> map;
        try {
            map = JsonKit.convertToMap(data);
        } catch (Exception e) {
            e.printStackTrace();
            renderFailure("invalid.input.json");
            return;
        }
        String name = (String) map.get("name");
        String loginName = (String) map.get("login_name");
        String weixin = (String) map.get("weixin");
        String password = (String) map.get("password");
        String inviteCode = (String) map.get("invite_code");
        String avatar = (String) map.get("avatar");
        Integer sex = (Integer) map.get("sex");
        Integer followed = (Integer) map.get("followed");
        String invitationQrcode = (String) map.get("invitation_qrcode");

        User user = User.dao.findByWeixin(weixin);
        if (user == null) {
            user = new User();
            user.setName(name);
            user.setLoginName(loginName);
            user.setWeixin(weixin);
            if (sex != null) {
                user.setSex(sex);
            }
            user.setAppUser(User.APP_USER);
            user.setPassword(password);
            user.setAvatar(avatar);
            user.setInvitationQrcode(invitationQrcode);
            user.resetTokenExpiredDate();
            User inviter = User.dao.findByInvitationCode(inviteCode);
            if (inviter != null) {
                user.setInviterId(inviter.getId());
            }
            if (followed != null) {
                user.setFollowed(followed);
                user.setFollowTime(new Date());
            }

            Integer roleId = DefaultRole.me().getRoleProvider().getDefault().getId();
            Integer[] roles = new Integer[] { roleId };
            Ret ret = userService.createUser(user, roles);
            logger.debug("user saved. ret={}, user={}", ret.getData(), user);
        }
        else {
            if (StrKit.notBlank(name)) {
                user.setName(name);
            }
            if (StrKit.notBlank(avatar)) {
                user.setAvatar(avatar);
            }
            if (followed != null) {
                user.setFollowed(followed);
                user.setFollowTime(new Date());
            }
            if (sex != null) {
                user.setSex(sex);
            }
            user.setPassword("");
            user.resetTokenExpiredDate();
            user.setLastLoginDate(new Date());
            userService.updateUser(user, null);

            // notify the cooperative-partner to update ancestor if necessary
            User inviter = User.dao.findByInvitationCode(inviteCode);
            if (inviter != null && !inviter.getId().equals(user.getId())) {
                new AttemptingUpdateInviterSubject(user.getId(), inviter.getId()).notifyObserver();
            }
        }

        // retrieve again to get accesstoken.
        user = User.dao.findByWeixin(weixin);
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", AccessToken.getAccessToken(user));
        result.put("invite_code", user.getInvitationCode());
        result.put("user_id", user.getId());
        result.put("inviter_id", user.getInviterId());

        renderSuccess(result);
    }

    /**
     * 关注/取关公众号处理事件
     * PUT /sys/rest/user/<weixin>
     *
     * {
     *     "followed": 0 // 0 关注, 1 未关注
     * }
     */
    public void update() {
        String weixin = getPara();
        if (StrKit.isBlank(weixin)) {
            renderFailure("invalid.user");
            return;
        }

        String data = HttpKit.readData(getRequest());
        Map<String, Object> map;
        try {
            map = JsonKit.convertToMap(data);
        } catch (Exception e) {
            e.printStackTrace();
            renderFailure("invalid.input.json");
            return;
        }

        Integer followed = (Integer) map.get("followed");
        if (followed == null) {
            renderFailure("invalid.data");
            return;
        }
        String invitationQrcode = (String) map.get("invitation_qrcode");

        User user = User.dao.findByWeixin(weixin);
        if (user == null) {
            user = new User();
            user.setName(weixin);
            user.setLoginName(weixin);
            user.setWeixin(weixin);
            user.setSex(0);
            user.setPassword(weixin + new Date().getTime());
            user.resetTokenExpiredDate();
            user.setFollowed(followed);
            user.setFollowTime(new Date());
            user.setInvitationQrcode(invitationQrcode);

            Integer roleId = DefaultRole.me().getRoleProvider().getDefault().getId();
            Integer[] roles = new Integer[] { roleId };
            Ret ret = userService.createUser(user, roles);
            logger.debug("user saved. ret={}, user={}", ret, user);
            renderSuccessMessage("ok");
            return;
        }

        user.setFollowed(followed);
        user.setFollowTime(new Date());
        user.setPassword("");
        userService.updateUser(user, null);
        renderSuccessMessage("ok");
    }
}
