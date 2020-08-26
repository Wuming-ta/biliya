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

import com.google.zxing.WriterException;
import com.jfeat.captcha.CaptchaKit;
import com.jfeat.common.QrcodeConfigHolder;
import com.jfeat.core.RestController;
import com.jfeat.identity.api.model.ProfileEntity;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.UserService;
import com.jfeat.kit.DateKit;
import com.jfeat.util.*;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by jacky on 5/17/16.
 */
@ControllerBind(controllerKey = "/rest/profile")
public class ProfileController extends RestController {

    private UserService userService = Enhancer.enhance(UserService.class);

    /**
     * get profile
     */
    @Override
    @Before(CurrentUserInterceptor.class)
    public void index() {
        User user = getAttr("currentUser");
        user.remove(User.Fields.PASSWORD.toString());
        user.remove(User.Fields.TOKEN_SALT.toString());
        user.remove(User.Fields.SALT.toString());
        String inviterName = null;
        User inviter = user.getInviter();
        if (inviter != null) {
            inviterName = inviter.getName();
        }
        user.put("inviter_name", inviterName);
        if (StrKit.isBlank(user.getInvitationQrcodeUrl())) {
            String invitationQrcode = QrcodeConfigHolder.me().getWxHost() + "/app?invite_code=";
            StringBuilder qrcode = new StringBuilder(invitationQrcode);
            qrcode.append(user.getInvitationCode());
            if (QrcodeConfigHolder.me().getRdCode() > 0) {
                qrcode.append("&rdCode=").append(QrcodeConfigHolder.me().getRdCode());
            }
            user.setInvitationQrcode(qrcode.toString());
            try {
                BufferedImage image = DrawKit.drawQrcode(qrcode.toString(),
                        QrcodeConfigHolder.me().getLogoUrl(),
                        QrcodeConfigHolder.me().getShowAvatar() ? user.getAvatar() : null,
                        QrcodeConfigHolder.me().getInfoUrl(),
                        QrcodeConfigHolder.me().getFooter(),
                        QrcodeConfigHolder.me().getContents());
                String url = DrawKit.upload(image);
                user.setInvitationQrcodeUrl(url);
            } catch (IOException | WriterException e) {
                e.printStackTrace();
            }
            user.setPassword("");
            userService.updateUser(user, null);
        }
        renderSuccess(user);
    }

    /**
     * update profile
     * POST /rest/profile
     * {
     *     "name":"abc",
     *     "real_name": "ABC",
     *     "avatar":"http://xxx",
     *     "sex": 1,  //0 secret, 1 male, 2 female
     *     "birthday": "1999-02-19",
     *     "details": "afsdfasfsfa",
     *     "email": "a@b.com"
     * }
     */
    @Before(CurrentUserInterceptor.class)
    public void save() {
        User currentUser = getAttr("currentUser");
        ProfileEntity profileEntity = getPostJson(ProfileEntity.class);
        currentUser.setEmail(profileEntity.getEmail());
        // 对于旧的商城，可以通过更新profile来更新手机号
        if (!CaptchaKit.isEnabled()) {
            User user = User.dao.findByPhone(profileEntity.getPhone());
            if (user != null && !user.getId().equals(currentUser.getId())) {
                renderFailure("phone.already.exist");
                return;
            }
            currentUser.setPhone(profileEntity.getPhone());
        }
        currentUser.setSex(profileEntity.getSex());
        currentUser.setDetails(profileEntity.getDetails());
        if (StrKit.notBlank(profileEntity.getBirthday())) {
            try {
                currentUser.setBirthday(DateKit.toDate(profileEntity.getBirthday()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (StrKit.notBlank(profileEntity.getReal_name())) {
            currentUser.setRealName(profileEntity.getReal_name());
        }
        if (StrKit.notBlank(profileEntity.getAvatar())) {
            currentUser.setAvatar(profileEntity.getAvatar());
        }
        if (StrKit.notBlank(profileEntity.getName())) {
            currentUser.setName(profileEntity.getName());
        }

        currentUser.setPassword("");
        userService.updateUser(currentUser, null);
        renderSuccessMessage("profile.updated");
    }
}
