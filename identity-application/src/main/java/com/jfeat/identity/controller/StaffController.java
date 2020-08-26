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
import com.jfeat.common.QrcodeConfigHolder;
import com.jfeat.core.BaseController;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.StorePlugin;
import com.jfeat.ext.plugin.store.StoreApi;
import com.jfeat.ext.plugin.store.bean.Assistant;
import com.jfeat.ext.plugin.store.bean.Store;
import com.jfeat.flash.Flash;
import com.jfeat.identity.authc.LoginUserStore;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfeat.identity.model.Role;
import com.jfeat.identity.model.User;
import com.jfeat.identity.model.param.UserParam;
import com.jfeat.identity.service.UserService;
import com.jfeat.util.DrawProfile;
import com.jfeat.util.UploadImage;
import com.jfinal.aop.Before;
import com.jfinal.aop.Enhancer;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Before(CurrentUserInterceptor.class)
public class StaffController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(StaffController.class);
    private UserService userService = Enhancer.enhance(UserService.class);

    @Override
    @RequiresPermissions(value = { "identity.view", "sys.staff.menu" }, logical = Logical.OR)
    @Before(Flash.class)
    public void index() {
        Integer pageNumber = getParaToInt("pageNumber", 1);
        Integer pageSize = getParaToInt("pageSize", 50);
        String name = getPara("name");
        String status = getPara("status");
        Integer roleId = getParaToInt("roleId");
        Integer appUser = getParaToInt("appUser", User.ADMIN_USER);
        UserParam param = new UserParam(pageNumber, pageSize);
        param.setAppUser(appUser).setRoleId(roleId).setName(name).setStatus(status);
        Page<User> userPage = User.dao.paginate(param);
        Map<Integer, List<String>> loginUserSessionMap = LoginUserStore.me().getUserSessionMap();
        userPage.getList().forEach(user -> {
            List<String> sessionIds = loginUserSessionMap.getOrDefault(user.getId(), new ArrayList<>());
            user.put("online", sessionIds.size());
        });
        setAttr("users", userPage);
        setAttr("roles", Role.dao.findAll());
        setAttr("statuses", User.Status.values());

        keepPara();
    }

    @Override
    @RequiresPermissions("identity.edit")
    public void add() {
        setAttr("user", new User());
        setAttr("statuses", User.Status.values());
        setAttr("roles", Role.dao.findAll());
    }

    @Override
    @RequiresPermissions("identity.edit")
    @Before(Tx.class)
    public void save() {
        User user = getModel(User.class);
        Integer[] roles = getParaValuesToInt("role");
        String inviteCode = getPara("inviteCode");
        if (StrKit.notBlank(inviteCode)) {
            User inviter = User.dao.findByInvitationCode(inviteCode);
            if (inviter != null) {
                user.setInviterId(inviter.getId());
            }
        }
        user.setAppUser(User.ADMIN_USER);
        userService.createUser(user, roles);
        setFlash("message", getRes().get("identity.user.create.success"));
        redirect("/staff");
    }

    @Override
    @RequiresPermissions(value = { "identity.view", "sys.staff.menu" }, logical = Logical.OR)
    public void edit() {
        User user = User.dao.findById(getParaToInt());
        setAttr("user", user);
        setAttr("statuses", User.Status.values());
        setAttr("roles", Role.dao.findAll());
    }

    @Override
    @RequiresPermissions("identity.edit")
    @Before({Tx.class})
    public void update() {
        User user = getModel(User.class);
        user.setAppUser(User.ADMIN_USER);
        Integer[] roles = getParaValuesToInt("role");
        if (roles == null) {
            roles = new Integer[0];
        }
        List<Role> currentRoles = user.getRoles();
        List<Integer> newRoles = new ArrayList<>(Arrays.asList(roles));
        boolean shouldForceLogout = currentRoles.stream().anyMatch(role -> !newRoles.contains(role.getId()));
        if (shouldForceLogout) {
            LoginUserStore.me().forceLogout(user.getId());
        }
        userService.updateUser(user, roles);
        setFlash("message", getRes().get("identity.user.edit.success"));

        redirect("/staff");
    }

    @Override
    @RequiresPermissions("identity.delete")
    public void delete() {
        Integer userId = getParaToInt();
        String redirectUrl = "/staff";
        if (userId == null) {
            redirect(redirectUrl);
            return;
        }
        User currentUser = getAttr("currentUser");
        if (userId.equals(currentUser.getId())) {
            String message = getRes().get("identity.user.delete_self");
            logger.debug(message);
            setFlash("message", message);
            redirect(redirectUrl);
            return;
        }

        if (ExtPluginHolder.me().get(StorePlugin.class).isEnabled()) {
            StoreApi storeApi = new StoreApi();
            Assistant assistant = storeApi.queryAssistant(userId.longValue());
            if (StrKit.notBlank(assistant.getCode())) {
                assistant = storeApi.getAssistant(assistant.getId());
                if (assistant.getStores() != null && !assistant.getStores().isEmpty()) {
                    String message = getRes().format("identity.user.delete_is_assistant", assistant.getStores().get(0).getName());
                    logger.debug(message);
                    setFlash("message", message);
                    redirect(redirectUrl);
                    return;
                }
            }
        }

        userService.deleteUser(userId);
        setFlash("message", getRes().get("identity.user.delete.success"));

        redirect(redirectUrl);
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
