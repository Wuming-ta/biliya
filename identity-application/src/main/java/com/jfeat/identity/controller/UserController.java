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

package com.jfeat.identity.controller;

import com.google.zxing.WriterException;
import com.jfeat.Global;
import com.jfeat.common.AuthConfigHolder;
import com.jfeat.common.QrcodeConfigHolder;
import com.jfeat.core.BaseController;
import com.jfeat.flash.Flash;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.Role;
import com.jfeat.identity.model.User;
import com.jfeat.identity.model.param.UserParam;
import com.jfeat.identity.service.UserService;
import com.jfeat.util.DrawKit;
import com.jfeat.util.DrawProfile;
import com.jfeat.util.UploadImage;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Before(CurrentUserInterceptor.class)
public class UserController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService userService = Enhancer.enhance(UserService.class);

    @Override
    @RequiresPermissions(value = { "identity.view", "sys.user.menu" }, logical = Logical.OR)
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String name = getPara("name");
        String status = getPara("status");
        Integer roleId = getParaToInt("roleId");
        Integer appUser = getParaToInt("appUser", User.APP_USER);
        UserParam param = new UserParam(pageNumber, pageSize);
        param.setAppUser(appUser).setRoleId(roleId).setName(name);
        setAttr("users", User.dao.paginate(param));
        setAttr("roles", Role.dao.findAll());
        setAttr("statuses", User.Status.values());
        setAttr("weixinHosted", Global.isWeixinHosted());
        keepPara();
    }

    @Override
    @RequiresPermissions(value = { "identity.view", "sys.user.menu" }, logical = Logical.OR)
    public void edit() {
        User user = User.dao.findById(getParaToInt());
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
            user = User.dao.findById(getParaToInt()); //retrieve again after updated.
        }
        if (user.getSex() == null) {
            user.setSex(User.Sex.SECRET.getValue());
        }
        setAttr("user", user);
        setAttr("statuses", User.Status.values());
        setAttr("roles", Role.dao.findAll());
        setAttr("immutableFields", AuthConfigHolder.me().getImmutableFields());
        setAttr("weixinHosted", Global.isWeixinHosted());
    }

    @Override
    @RequiresPermissions("identity.edit")
    @Before({Tx.class})
    public void update() {
        User user = getModel(User.class);
        Integer[] roles = getParaValuesToInt("role");
        if (roles == null) {
            roles = new Integer[0];
        }
        user.setAppUser(User.APP_USER);
        userService.updateUser(user, roles);
        setFlash("message", getRes().get("identity.user.edit.success"));

        redirect("/user");
    }

    @Override
    @RequiresPermissions("identity.delete")
    public void delete() {
        Integer userId = getParaToInt();
        User currentUser = getAttr("currentUser");
        if (userId != null && userId.equals(currentUser.getId())){
            String message = getRes().get("identity.user.delete_self");
            logger.debug(message);
            setFlash("message", message);
        }
        else {
            userService.deleteUser(userId);
            setFlash("message", getRes().get("identity.user.delete.success"));
        }
        redirect("/user");
    }

    @RequiresPermissions("identity.edit")
    @Before({Tx.class})
    public void invalidQrcodeUrl() {
        userService.invalidInvitationQrcodeUrl();
        redirect("/user");
    }


    /**
     * ajax check
     */
    public void loginNameVerify() {
        checkUser(User.dao.findByLoginName(getPara("login_name")), getParaToInt("user_id"));
    }
    public void phoneVerify() {
        checkUser(User.dao.findByPhone(getPara("phone")), getParaToInt("user_id"));
    }
    public void weixinVerify() {
        checkUser(User.dao.findByWeixin(getPara("weixin")), getParaToInt("user_id"));
    }
    private void checkUser(User user, Integer userId) {
        User originUser = null;
        if (userId != null) {
            originUser = User.dao.findById(userId);
        }

        if (originUser == null) {
            if (user == null) {
                renderText("true");
            }
            else {
                renderText("false");
            }
            return;
        }

        // originUser is not null

        if (user == null) {
            renderText("true");
            return;
        }

        if (originUser.getId().equals(user.getId())) {
            renderText("true");
        }
        else {
            renderText("false");
        }

    }

}
