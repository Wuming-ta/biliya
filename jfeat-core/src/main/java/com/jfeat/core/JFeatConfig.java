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

import com.jagregory.shiro.freemarker.ShiroTags;
import com.jfeat.core.handler.CorsHandler;
import com.jfeat.core.handler.SessionHandler;
import com.jfeat.ext.plugin.async.AsyncTaskPlugin;
import com.jfeat.flash.FlashKit;
import com.jfeat.ext.plugin.flyway.FlywayPlugin;
import com.jfeat.kit.lbs.baidu.ApiConfigKit;
import com.jfeat.ui.MenuInterceptor;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.i18n.I18nInterceptor;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.FreeMarkerRender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JFeatConfig extends JFinalConfig {

    private static Logger logger = LoggerFactory.getLogger(JFeatConfig.class);

    private Map<String, ActiveRecordPlugin> activeRecordPluginMap = new HashMap<>();
    private Map<String, IDataSourceProvider> dataSourceProviderMap = new HashMap<>();
	
	public JFeatConfig() {
		new CoreModule(this);
	}

    @Override
    public void afterJFinalStart() {

        ModuleHolder.me().afterJFinalStart();
    }

    @Override
    public void beforeJFinalStop() {
        ModuleHolder.me().beforeJFinalStop();
    }

    @Override
    public void configConstant(Constants me) {
        String prop = System.getProperty("jfeat.config.properties", "config.properties");
        File propFile = new File(prop);
        if (propFile.exists() && propFile.isAbsolute()) {
            loadPropertyFile(propFile);
        } else {
            loadPropertyFile(prop);
        }
        me.setDevMode(getPropertyToBoolean("devMode", false));
        String baseViewPath = getProperty("baseViewPath", "/WEB-INF/pages/");
        me.setBaseViewPath(baseViewPath);
        me.setErrorView(401, getProperty("401ErrorView", baseViewPath + "error/401.html"));
        me.setErrorView(403, getProperty("403ErrorView", baseViewPath + "error/403.html"));
        me.setError404View(getProperty("404ErrorView", baseViewPath + "error/404.html"));
        me.setError500View(getProperty("500ErrorView", baseViewPath + "error/500.html"));
        FreeMarkerRender.getConfiguration().setSharedVariable("shiro", new ShiroTags());

        JFeatConfigKit.me().init(this);
        ModuleHolder.me().configConstant(me);
    }

    @Override
    public void configRoute(Routes me) {
        ModuleHolder.me().configRoute(me);
    }

    @Override
    public void configPlugin(Plugins me) {
        if (StrKit.notBlank(getProperty("jdbcUrl"))) {
            DruidPlugin druidPlugin = new DruidPlugin(getProperty("jdbcUrl"), getProperty("user"), getProperty("password").trim(), getProperty("driver"));
            me.add(druidPlugin);

            if (getPropertyToBoolean("flyway.enable", true)) {
                FlywayPlugin flywayPlugin = new FlywayPlugin(druidPlugin);
                me.add(flywayPlugin);
            }

            ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
            arp.setShowSql(getPropertyToBoolean("showSql", false));
            me.add(arp);

            activeRecordPluginMap.put("default", arp);
            dataSourceProviderMap.put("default", druidPlugin);
        }
        else {
            logger.warn("##### jdbcUrl is not set. please check.");
        }

        me.add(new EhCachePlugin());

        me.add(new AsyncTaskPlugin(getPropertyToInt("asyncTaskThreadNumber", 5)));
		
        ModuleHolder.me().configPlugin(me);

        if (logger.isDebugEnabled()) {
            for (IPlugin plugin : me.getPluginList()) {
                logger.debug("configured plugin: " + plugin.getClass().getName());
            }
        }
    }

    @Override
    public void configInterceptor(Interceptors me) {
        ModuleHolder.me().configInterceptor(me);
    }

    @Override
    public void configHandler(Handlers me) {
        ModuleHolder.me().configHandler(me);
    }

    public ActiveRecordPlugin getActiveRecordPlugin(String configName) {
        if (StrKit.notBlank(configName))
            return activeRecordPluginMap.get(configName);
        return activeRecordPluginMap.get("default");
    }

    public IDataSourceProvider getDataSourceProvider(String configName) {
        if (StrKit.notBlank(configName))
            return dataSourceProviderMap.get(configName);
        return  dataSourceProviderMap.get("default");
    }

    public void addDataSourceProvider(String configName, IDataSourceProvider dataSourceProvider) {
        this.dataSourceProviderMap.put(configName, dataSourceProvider);
    }

    public void addActiveRecordPlugin(String name, ActiveRecordPlugin plugin) {
        activeRecordPluginMap.put(name, plugin);
    }
}
