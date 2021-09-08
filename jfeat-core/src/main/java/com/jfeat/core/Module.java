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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.kit.SimpleNameStyles;
import com.jfeat.ui.IPrivilegeStrategy;
import com.jfeat.ui.PrivilegeStrategyImpl;
import com.jfinal.aop.Interceptor;
import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.handler.Handler;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.plugin.activerecord.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class Module {

    private static Logger logger = LoggerFactory.getLogger(Module.class);

    private List<Class<? extends Controller>> controllers = Lists.newArrayList();
    @SuppressWarnings("rawtypes")
    private  List<Class<? extends Model>> models = Lists.newArrayList();
    private String controllerSuffix = "Controller";
    private String name;
    private Map<String, IPrivilegeStrategy> privilegeStrategyMap = Maps.newHashMap();
    private List<String> xssExcludedList = Lists.newArrayList();
    
    private JFeatConfig jfeatConfig;


    public Map<String ,IPrivilegeStrategy> getPrivilegeStrategyMap() {
        return privilegeStrategyMap;
    }

    public void setPrivilegeStrategy(String privilegeKey, IPrivilegeStrategy privilegeStrategy) {
        privilegeStrategyMap.put(privilegeKey, privilegeStrategy);
    }

    public List<String> getXssExcludedList() {
        return xssExcludedList;
    }

    public void addXssExcluded(String xssExcluded) {
        this.xssExcludedList.add(xssExcluded);
    }

    public Module(JFeatConfig jfeatConfig) {
        this.jfeatConfig = jfeatConfig;
        name = this.getClass().getName();
        ModuleHolder.me().registerModule(this);
    }
    
    public JFeatConfig getJFeatConfig() {
        return jfeatConfig;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Module other = (Module) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;

        return true;
    }

    @SuppressWarnings("unchecked")
    public Module addController(Class<? extends Controller> clazz) {
        if (!controllers.contains(clazz)) {
            controllers.add(clazz);
        }
        return this;
    }

    public Module removeController(Class<? extends Controller> clazz) {
        if (controllers.contains(clazz)) {
            controllers.remove(clazz);
        }
        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Module addModel(Class<? extends Model> clazz) {
        if (!models.contains(clazz)) {
            models.add(clazz);
        }
        return this;
    }

    /**
     * Check if a plugin is already added into the system.
     * @param plugins
     * @param plugin
     * @return
     */
    protected boolean exists(Plugins plugins, Class<? extends IPlugin> plugin) {
        for (IPlugin p : plugins.getPluginList()) {
            if (p.getClass().getName().equals(plugin.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a handler is already added.
     * @param handlers
     * @param handler
     * @return
     */
    protected boolean exists(Handlers handlers, Class<? extends Handler> handler) {
        for (Handler h : handlers.getHandlerList()) {
            if (h.getClass().getName().equals(handler.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if an interceptor is already added.
     * @param interceptors
     * @param interceptor
     * @return
     */
    protected boolean exists(Interceptors interceptors, Class<? extends Interceptor> interceptor) {
        //for (Interceptor i : interceptors.getGlobalActionInterceptor()) {
        //    if (i.getClass().getName().equals(interceptor.getName())) {
        //        return true;
        //    }
        //}
        return false;
    }

    protected void configPrivilegeStrategy(String key) {
        String propName = name + ".privilege.properties";
        try {
            for (String value : PropKit.use(propName).get(key).split(",")) {
                List<String> param = new LinkedList<>();
                String item = value.trim();
                if (StrKit.isBlank(item)) {
                    break;
                }
                for (String privilege : PropKit.use(propName).get(key + "." + item).split(",")) {
                    param.add(privilege.trim());
                }
                logger.debug("adding privilege: " + item + " - " + param);
                setPrivilegeStrategy(item, new PrivilegeStrategyImpl(param.toArray(new String[0])));
            }
        } catch (Exception ex) {
            logger.warn("****Exception occurred while config privilege strategy. propName={}, error={}", propName, ex.getMessage());
        }
    }

    public void configConstant(Constants me){
        configPrivilegeStrategy("privilege.strategy.menu");
        configPrivilegeStrategy("privilege.strategy.widget");
        configPrivilegeStrategy("privilege.strategy.notification");
    }

    public void configRoute(Routes me){
        bindRoutes(me);
    }

    public void configPlugin(Plugins me){
        bindTables(null);
    }

    public void configInterceptor(Interceptors me){
    }

    public void configHandler(Handlers me){
    }

    public void afterJFinalStart() {
    }

    public void beforeJFinalStop() {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void bindTables(String configName) {
        ActiveRecordPlugin activeRecordPlugin = jfeatConfig.getActiveRecordPlugin(configName);
        if (activeRecordPlugin == null) {
            logger.warn("activeRecordPlugin is null. please check.");
            return;
        }
        TableBind tb;
        for (Class modelClass : models) {
            tb = (TableBind) modelClass.getAnnotation(TableBind.class);
            String tableName;
            if (tb == null) {
                tableName = modelClass.getSimpleName();
                activeRecordPlugin.addMapping(tableName, modelClass);
                logger.debug(" addMapping(" + tableName + ", " + modelClass.getName() + ")");
            } else {
                activeRecordPlugin = jfeatConfig.getActiveRecordPlugin(tb.configName());
                tableName = tb.tableName();
                if (StrKit.notBlank(tb.pkName())) {
                    activeRecordPlugin.addMapping(tableName, tb.pkName(), modelClass);
                    logger.debug(" addMapping(" + tableName + ", " + tb.pkName() + ","
                            + modelClass.getName() + ")");
                } else {
                    activeRecordPlugin.addMapping(tableName, modelClass);
                    logger.debug(" addMapping(" + tableName + ", " + modelClass.getName() + ")");
                }
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void bindRoutes(Routes routes) {
        ControllerBind controllerBind = null;
        for (Class controller : controllers) {
            controllerBind = (ControllerBind) controller.getAnnotation(ControllerBind.class);
            String controllerKey = "";
            String viewPath = "";
            if (controllerBind == null) {
                viewPath = controllerKey = controllerKey(controller);
            } else if (StrKit.isBlank(controllerBind.viewPath())) {
                viewPath = controllerKey = controllerBind.controllerKey();
            } else {
                controllerKey = controllerBind.controllerKey();
                viewPath = controllerBind.viewPath();
            }
            boolean exist = false;
//            for (Map.Entry<String, Class<? extends Controller>> entry : routes.getEntrySet()) {
//                if (entry.getKey().equals(controllerKey)) {
//                    logger.warn(controllerKey + " already added, won't add for controller " + controller);
//                    exist = true;
//                    break;
//                }
//            }
            if (exist == false) {
                routes.add(controllerKey, controller, viewPath);
                logger.debug("routes.add(" + controllerKey + ", " + controller + ","  + viewPath + ")");
            }
        }
    }

    private String controllerKey(Class<Controller> clazz) {
        Preconditions.checkArgument(clazz.getSimpleName().endsWith(controllerSuffix),
                " does not has a @ControllerBind annotation and it's name is not end with " + controllerSuffix);
        String controllerKey = "/" + StrKit.firstCharToLowerCase(clazz.getSimpleName());
        controllerKey = controllerKey.substring(0, controllerKey.indexOf(controllerSuffix));
        return SimpleNameStyles.LOWER_UNDERLINE.name(controllerKey);
    }

}
