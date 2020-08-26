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

import com.jfeat.common.AuthConfigHolder;
import com.jfeat.core.BaseController;
import com.jfeat.identity.authc.LoginUserStore;
import com.jfeat.identity.model.User;
import com.jfeat.identity.service.DefaultRole;
import com.jfeat.identity.service.PermissionCache;
import com.jfeat.identity.service.UserService;
import com.jfeat.ui.MenuInterceptor;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Enhancer;
import com.jfinal.ext.plugin.shiro.ShiroInterceptor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by ehngjen on 12/16/14.
 */
public class AuthController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(AuthController.class);
    private UserService userService = Enhancer.enhance(UserService.class);

    @Override
    @Clear({ShiroInterceptor.class, MenuInterceptor.class})
    public void index() {
        setAttr("isCaptchaEnabled", AuthConfigHolder.me().isCaptchaEnabled());
        setAttr("isAllowRegisterEnabled", AuthConfigHolder.me().isAllowRegisterEnabled());
        setAttr("bgImage", AuthConfigHolder.me().getBgImage());
        render("login.html");
    }

    @Clear({ShiroInterceptor.class, MenuInterceptor.class})
    public void captcha() {
        renderCaptcha();
    }

    @Clear({ShiroInterceptor.class, MenuInterceptor.class})
    public void login() {
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            setAttr("isCaptchaEnabled", AuthConfigHolder.me().isCaptchaEnabled());
            setAttr("isAllowRegisterEnabled", AuthConfigHolder.me().isAllowRegisterEnabled());
            setAttr("bgImage", AuthConfigHolder.me().getBgImage());
            render("login.html");
        } else {
            redirect("/");
        }
    }

    @Clear({ShiroInterceptor.class, MenuInterceptor.class})
    public void loginForm() {
        if (AuthConfigHolder.me().isCaptchaEnabled() && !validateCaptcha("captcha")) {
            setAttr("message", getRes().get("login.failure.captcha.mismatch"));
            setAttr("isCaptchaEnabled", AuthConfigHolder.me().isCaptchaEnabled());
            setAttr("isAllowRegisterEnabled", AuthConfigHolder.me().isAllowRegisterEnabled());
            setAttr("bgImage", AuthConfigHolder.me().getBgImage());
            render("login.html");
            return;
        }
        Subject currentUser = SecurityUtils.getSubject();
        if (!currentUser.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(getPara("login_name"), getPara("password"));
            token.setRememberMe(getParaToBoolean("rememberme", false));
            try {
                currentUser.login(token);
                //if no exception, that's it, we're done!
                User user = User.dao.findByLoginName(token.getUsername());

                if (User.Status.valueOf(user.getStatus()) != User.Status.NORMAL) {
                    currentUser.logout();
                    setAttr("message", getRes().get("login.failure.locked"));
                    setAttr("isCaptchaEnabled", AuthConfigHolder.me().isCaptchaEnabled());
                    setAttr("isAllowRegisterEnabled", AuthConfigHolder.me().isAllowRegisterEnabled());
                    setAttr("bgImage", AuthConfigHolder.me().getBgImage());
                    render("login.html");
                    return;
                }
                user.updateLastLoginDate();
                PermissionCache.me().cache(user.getLoginName());
                LoginUserStore.me().store(currentUser, currentUser.getSession(true).getId().toString());

                //redirect("/");
                WebUtils.redirectToSavedRequest(getRequest(), getResponse(), "/");
                renderNull();
                return;
            } catch (AuthenticationException | IOException ae) {
                //unexpected condition - error?
                logger.warn(ae.getMessage());
            }
        }

        keepPara("login_name");
        setAttr("message", getRes().get("login.failure.invalid"));
        setAttr("isCaptchaEnabled", AuthConfigHolder.me().isCaptchaEnabled());
        setAttr("isAllowRegisterEnabled", AuthConfigHolder.me().isAllowRegisterEnabled());
        setAttr("bgImage", AuthConfigHolder.me().getBgImage());
        render("login.html");
    }

    @Clear({ShiroInterceptor.class, MenuInterceptor.class})
    public void logout() {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        setAttr("isCaptchaEnabled", AuthConfigHolder.me().isCaptchaEnabled());
        setAttr("isAllowRegisterEnabled", AuthConfigHolder.me().isAllowRegisterEnabled());
        setAttr("bgImage", AuthConfigHolder.me().getBgImage());
        render("login.html");
    }

    /**
     * ajax check
     */
    public void loginNameVerify() {
        String loginName = getPara("login_name");
        if (User.dao.findByLoginName(loginName) == null) {
            renderText("true");
        } else {
            renderText("false");
        }
    }

    @Clear({ShiroInterceptor.class, MenuInterceptor.class})
    public void resetPassword() {
    }

    @Clear({ShiroInterceptor.class, MenuInterceptor.class})
    public void resetPasswordForm() {
        //TODO
    }

    public void reg() {
        if (!AuthConfigHolder.me().isAllowRegisterEnabled()) {
            redirect("/");
            return;
        }
        setAttr("isCaptchaEnabled", AuthConfigHolder.me().isCaptchaEnabled());
    }

    public void regForm() {
        User user=getModel(User.class);
        if (AuthConfigHolder.me().isCaptchaEnabled() && !validateCaptcha("captcha")) {
            setAttr("user",user);
            setAttr("message", getRes().get("login.failure.captcha.mismatch"));
            setAttr("isCaptchaEnabled", AuthConfigHolder.me().isCaptchaEnabled());
            setAttr("bgImage", AuthConfigHolder.me().getBgImage());
            render("reg.html");
            return;
        }
        Integer roleId = DefaultRole.me().getRoleProvider().getDefault().getId();
        Integer[] roles = new Integer[] { roleId };
        userService.createUser(user, roles);
        setAttr("message", getRes().get("identity.user.register.success"));
        setAttr("isCaptchaEnabled", AuthConfigHolder.me().isCaptchaEnabled());
        setAttr("isAllowRegisterEnabled", AuthConfigHolder.me().isAllowRegisterEnabled());
        setAttr("bgImage", AuthConfigHolder.me().getBgImage());
        render("login.html");
    }
}
