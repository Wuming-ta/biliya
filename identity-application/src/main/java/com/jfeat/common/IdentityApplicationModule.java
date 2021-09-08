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
package com.jfeat.common;

import com.jfeat.captcha.CaptchaKit;
import com.jfeat.core.JFeatConfig;
import com.jfeat.core.Module;
import com.jfeat.ext.plugin.ExtPluginHolder;
import com.jfeat.ext.plugin.StorePlugin;
import com.jfeat.identity.api.model.PhoneCaptchaWhitelist;
import com.jfeat.identity.filter.sys.SysAuthorizationProviderConfigFileImpl;
import com.jfeat.identity.filter.sys.SysRealm;
import com.jfeat.identity.interceptor.CurrentUserInterceptor;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.plugin.shiro.ShiroInterceptor;
import com.jfinal.ext.plugin.shiro.ShiroPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IdentityApplicationModule extends Module {

    private static Logger logger = LoggerFactory.getLogger(IdentityApplicationModule.class);

    // shiro plugin need it.
    private Routes routes;

    private String cacheName;

    public IdentityApplicationModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        IdentityApplicationModelMapping.mapping(this);

        // 1. register your controllers
        // addController(YourDefinedController.class);
        addController(com.jfeat.identity.controller.AuthController.class);
        addController(com.jfeat.identity.controller.UserController.class);
        addController(com.jfeat.identity.controller.StaffController.class);
        addController(com.jfeat.identity.controller.RoleController.class);
        addController(com.jfeat.identity.controller.ProfileController.class);
        addController(com.jfeat.identity.controller.UserJoinNotifyController.class);

        addController(com.jfeat.identity.api.UserInfoController.class);
        addController(com.jfeat.identity.api.UserController.class);
        addController(com.jfeat.identity.api.StaffController.class);
        addController(com.jfeat.identity.api.RoleController.class);
        addController(com.jfeat.identity.api.VerifyAccessTokenController.class);
        addController(com.jfeat.identity.api.ForgetPasswordController.class);
        addController(com.jfeat.identity.api.PhoneController.class);
        addController(com.jfeat.identity.api.PasswordController.class);
        addController(com.jfeat.identity.api.LoginController.class);
        addController(com.jfeat.identity.api.LogoutController.class);
        addController(com.jfeat.identity.api.RegisterController.class);
        addController(com.jfeat.identity.api.ProfileController.class);
        addController(com.jfeat.identity.sys.api.UserController.class);


        // 3. config the module you dependencied.
        // new YouDependenciedModule(jfeatConfig);
        new ConfigDomainModule(jfeatConfig);
        new IdentityDomainModule(jfeatConfig);

    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);
        SysAuthorizationProviderConfigFileImpl sysAuthorizationProvider = new SysAuthorizationProviderConfigFileImpl();
        sysAuthorizationProvider.setUserName(getJFeatConfig().getProperty("sys.auth.username", "sys"));
        sysAuthorizationProvider.setPassword(getJFeatConfig().getProperty("sys.auth.password", "sys"));
        Collection<String> allowIps = new ArrayList<>();
        for (String ip : getJFeatConfig().getProperty("sys.auth.allowips", "").split(",")) {
            allowIps.add(ip.trim());
        }
        sysAuthorizationProvider.setAllowIps(allowIps);
        SysRealm.defaultSysAuthorizationProvider = sysAuthorizationProvider;

        Integer contentCount = getJFeatConfig().getPropertyToInt("qrcode.text.content.count", 0);
        String[] contents = new String[contentCount];
        for (int i = 0; i < contentCount; i++) {
            contents[i] = getJFeatConfig().getProperty("qrcode.text.content." + i);
        }
        String footer = getJFeatConfig().getProperty("qrcode.text.footer");
        String infoUrl = getJFeatConfig().getProperty("qrcode.text.infoUrl");
        String logoUrl = getJFeatConfig().getProperty("qrcode.text.logoUrl");
        Boolean showAvatar = getJFeatConfig().getPropertyToBoolean("qrcode.text.showAvatar", true);
        Integer rdCode = getJFeatConfig().getPropertyToInt("qrcode.rdCode", 1);
        QrcodeConfigHolder.me().setRdCode(rdCode);
        QrcodeConfigHolder.me().setContents(contents);
        QrcodeConfigHolder.me().setFooter(footer);
        QrcodeConfigHolder.me().setLogoUrl(logoUrl);
        QrcodeConfigHolder.me().setInfoUrl(infoUrl);
        QrcodeConfigHolder.me().setShowAvatar(showAvatar);

        AuthConfigHolder.me().setMergeUserEnabled(getJFeatConfig().getPropertyToBoolean(AuthConfigHolder.MERGE_USER_CONFIG_NAME, true));
        AuthConfigHolder.me().setCaptchaEnabled(getJFeatConfig().getPropertyToBoolean(AuthConfigHolder.CAPTCHA_ENABLED_CONFIG_NAME, true));
        AuthConfigHolder.me().setAllowRegisterEnabled(getJFeatConfig().getPropertyToBoolean(AuthConfigHolder.ALLOW_REGISTER_CONFIG_NAME, true));
        AuthConfigHolder.me().setBgImage(getJFeatConfig().getProperty(AuthConfigHolder.BG_IMAGE_CONFIG_NAME));
        AuthConfigHolder.me().setImmutableFields(getJFeatConfig().getProperty(AuthConfigHolder.IMMUTABLE_FIELDS_CONFIG_NAME, ""));
        AuthConfigHolder.me().setAllowInviterReassign(getJFeatConfig().getPropertyToBoolean(AuthConfigHolder.ALLOW_INVITER_REASSIGN, true));

        Boolean smsCaptchaEnabled = getJFeatConfig().getPropertyToBoolean("sms.captcha.enabled", false);
        if (smsCaptchaEnabled) {
            CaptchaKit.init(new CaptchaServiceDummyImpl());
        }

        StorePlugin storePlugin = new StorePlugin(
                getJFeatConfig().getPropertyToBoolean("ext.store.enabled", false),
                getJFeatConfig().getProperty("ext.store.api.host", getJFeatConfig().getProperty("ext.api.host")),
                getJFeatConfig().getProperty("ext.store.jwt.key", getJFeatConfig().getProperty("ext.jwt.key")));
        ExtPluginHolder.me().start(StorePlugin.class, storePlugin);

        
        // 增加手机测试白名单
        String smsCaptchaWhitelist = getJFeatConfig().getProperty("sms.captcha.whitelist", "");
        {
            final String WHITELIST_CAPTCHA = "000000";
            for (String phone : smsCaptchaWhitelist.split(",")) {
                PhoneCaptchaWhitelist.getInstance().register(phone, WHITELIST_CAPTCHA);
            }
        }

    }

    @Override
    public void configRoute(Routes me) {
        super.configRoute(me);
        this.routes = me;
    }

    @Override
    public void configPlugin(Plugins me) {
        super.configPlugin(me);
        if (!exists(me, ShiroPlugin.class)) {
            ShiroPlugin shiroPlugin = new ShiroPlugin(this.routes);
            me.add(shiroPlugin);
        }
    }

    @Override
    public void configInterceptor(Interceptors me) {
        super.configInterceptor(me);
        if (!exists(me, ShiroInterceptor.class)) {
            me.add(new ShiroInterceptor());
        }
        if (!exists(me, CurrentUserInterceptor.class)) {
            me.add(new CurrentUserInterceptor());
        }
    }
}
