/*
 * Copyright (C) 2014-2015 by ehngjen @ www.jfeat.com
 *
 *  The program may be used and/or copied only with the written permission
 *  from JFeat.com, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */

package com.jfeat.core;

import com.jfeat.core.handler.CorsHandler;
import com.jfeat.core.handler.RpcApiProxyHandler;
import com.jfeat.core.handler.SessionHandler;
import com.jfeat.core.handler.XssHandler;
import com.jfeat.core.interceptor.ProductNameInterceptor;
import com.jfeat.ext.plugin.validation.ValidationPlugin;
import com.jfeat.ext.plugin.validation.ValidatorInterceptor;
import com.jfeat.flash.FlashKit;
import com.jfeat.kit.lbs.baidu.ApiConfigKit;
import com.jfeat.kit.qiniu.QiniuKit;
import com.jfeat.ui.MenuInterceptor;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Plugins;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.I18nInterceptor;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;

import java.io.File;
import java.util.List;

public class CoreModule extends Module {

    public CoreModule(JFeatConfig jfeatConfig) {
        super(jfeatConfig);
        addModel(com.jfeat.ui.model.Menu.class);
        addModel(com.jfeat.ui.model.Widget.class);
        addModel(com.jfeat.ui.model.Notification.class);
        addController(com.jfeat.ui.IndexController.class);
        addController(com.baidu.ueditor.UeditorController.class);
    }

    @Override
    public void configConstant(Constants me) {
        super.configConstant(me);

        I18n.setDefaultLocale("zh_CN");
        me.setI18nDefaultBaseName("i18n/messages");

        String uploadPath = getJFeatConfig().getProperty("uploadPath");
        String uploadHost = getJFeatConfig().getProperty("uploadHost", "");

        if (StrKit.isBlank(uploadPath)) {
            uploadPath = PathKit.getWebRootPath();
        }
        else {
            File file = new File(uploadPath);
            if (!file.isAbsolute()) {
                uploadPath = PathKit.getWebRootPath() + "/" + uploadPath;
            }
        }
        me.setBaseUploadPath("/");
        PhotoGalleryConstants.me().setHost(uploadHost);
        PhotoGalleryConstants.me().setUploadPath(uploadPath);
        String qiniuAk = getJFeatConfig().getProperty("qiniu.ak");
        String qiniuSk = getJFeatConfig().getProperty("qiniu.sk");
        String qiniuBucket = getJFeatConfig().getProperty("qiniu.bucket");
        String qiniuUrl = getJFeatConfig().getProperty("qiniu.url");
        String qiniuTempDir = getJFeatConfig().getProperty("qiniu.tmpdir", System.getProperty("java.io.tmpdir"));
        if (StrKit.notBlank(qiniuAk) && StrKit.notBlank(qiniuSk)) {
            QiniuKit.me().setAk(qiniuAk)
                    .setSk(qiniuSk)
                    .setBucketName(qiniuBucket)
                    .setUrl(qiniuUrl)
                    .setTmpdir(qiniuTempDir)
                    .init();
        }


        //baidu map config
        ApiConfigKit.me().setAk(getJFeatConfig().getProperty("baidu.lbs.ak"));
        ApiConfigKit.me().setSk(getJFeatConfig().getProperty("baidu.lbs.sk"));

        FlashKit.init();//use session by default.

    }

    @Override
    public void configHandler(Handlers me) {
        super.configHandler(me);
        me.add(new SessionHandler());
        me.add(new ContextPathHandler("base"));
        me.add(new CorsHandler());

        String proxyPrefix = getJFeatConfig().getProperty("rpc.proxy.prefix");
        String proxyHost = getJFeatConfig().getProperty("rpc.proxy.host");
        Integer proxyPort = getJFeatConfig().getPropertyToInt("rpc.proxy.port");
        me.add(new RpcApiProxyHandler(proxyHost, proxyPort, proxyPrefix));

        List<String> xssExcludedList = ModuleHolder.me().getXssExcludedList();
        String excludes = getJFeatConfig().getProperty("xss.exclude");
        if (StrKit.notBlank(excludes)) {
            for (String exclude : excludes.split(";")) {
                if (!xssExcludedList.contains(exclude.trim())) {
                    xssExcludedList.add(exclude.trim());
                }
            }
        }
        me.add(new XssHandler(xssExcludedList));
    }

    @Override
    public void configPlugin(Plugins me) {
        super.configPlugin(me);
        me.add(new ValidationPlugin());
    }

    @Override
    public void configInterceptor(Interceptors me) {
        super.configInterceptor(me);
        if (getJFeatConfig().getPropertyToBoolean("menu.interceptor.enabled", true)) {
            me.add(new MenuInterceptor());
        }
        me.add(new I18nInterceptor());
        me.add(new ValidatorInterceptor());

        ProductNameInterceptor productNameInterceptor = new ProductNameInterceptor();
        productNameInterceptor.setName(getJFeatConfig().getProperty("productName", "Mall"));
        productNameInterceptor.setTheme(getJFeatConfig().getProperty("theme", "default"));
        productNameInterceptor.setVersion(getJFeatConfig().getProperty("version"));
        productNameInterceptor.setLogo(getJFeatConfig().getProperty("logo"));
        me.add(productNameInterceptor);
    }
}
