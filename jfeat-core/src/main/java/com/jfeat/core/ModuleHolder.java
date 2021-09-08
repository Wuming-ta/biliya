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

import java.util.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jfeat.ui.IPrivilegeStrategy;
import com.jfinal.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModuleHolder {

    private static ModuleHolder me = new ModuleHolder();
    private static Logger logger = LoggerFactory.getLogger(ModuleHolder.class);

    private List<Module> modules;
    private Map<String, IPrivilegeStrategy> privilegeStrategyMap;
    private List<String> xssExcludedList;

    private ModuleHolder() {
        modules = Lists.newArrayList();
        privilegeStrategyMap = Maps.newHashMap();
        xssExcludedList = Lists.newArrayList();
    }

    public void registerModule(Module module) {
        if (!modules.contains(module)) {
            modules.add(module);
        }
    }

    public Map<String, IPrivilegeStrategy> getPrivilegeStrategyMap() {
        if (privilegeStrategyMap.size() == 0) {
            for (Module module : modules) {
                for (Map.Entry<String, IPrivilegeStrategy> entry : module.getPrivilegeStrategyMap().entrySet()) {
                    IPrivilegeStrategy strategy = privilegeStrategyMap.get(entry.getKey());
                    if (strategy != null) {
                        logger.debug("combining privilege strategy ", entry.getKey());
                        IPrivilegeStrategy privilegeStrategy = entry.getValue();
                        strategy.updatePermission(privilegeStrategy.getPermissions().toArray(new String[0]));
                    }
                    else {
                        privilegeStrategyMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return privilegeStrategyMap;
    }

    public List<String> getXssExcludedList() {
        if (xssExcludedList.size() == 0) {
            for (Module module : modules) {
                xssExcludedList.addAll(module.getXssExcludedList());
            }
        }
        return xssExcludedList;
    }

    public List<Module> getModules() {
        return modules;
    }

    public static ModuleHolder me() {
        return me;
    }


    public void configConstant(Constants me){
        for (Module module : modules) {
            module.configConstant(me);
        }
    }

    public void configRoute(Routes me){
        for (Module module : modules) {
            module.configRoute(me);
        }
    }

    public void configPlugin(Plugins me){
        for (Module module : modules) {
            module.configPlugin(me);
        }
    }

    public void configInterceptor(Interceptors me){
        for (Module module : modules) {
            module.configInterceptor(me);
        }
    }

    public void configHandler(Handlers me){
        for (Module module : modules) {
            module.configHandler(me);
        }
    }

    public void afterJFinalStart() {
        for (Module module : modules) {
            module.afterJFinalStart();
        }
    }

    public void beforeJFinalStop() {
        for (Module module : modules) {
            module.beforeJFinalStop();
        }
    }









//
//    public void registerPlugin(IPlugin... plugins) {
//        for (IPlugin plugin : plugins) {
//            if (!this.plugins.contains(plugin)) {
//                this.plugins.add(plugin);
//            }
//        }
//    }
//
//    public void registerHandler(Handler... handlers) {
//        for (Handler handler : handlers) {
//            if (!this.handlers.contains(handler)) {
//                this.handlers.add(handler);
//            }
//        }
//    }
//
//    @SuppressWarnings({ })
//    public void registerInterceptor(Interceptor... interceptors) {
//        for (Interceptor interceptor : interceptors) {
//            if (!this.interceptors.contains(interceptor)) {
//                this.interceptors.add(interceptor);
//            }
//        }
//    }
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public void registerController(Class<? extends Controller>... clazzes) {
//        for (Class clazz : clazzes) {
//            if (!controllers.contains(clazz)) {
//                controllers.add(clazz);
//            }
//        }
//    }
//
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public void registerModel(Class<? extends Model>... clazzes) {
//        for (Class clazz : clazzes) {
//            if (!models.contains(clazz)) {
//                models.add(clazz);
//            }
//        }
//    }
//
//    public void configHandlers(Handlers handlers) {
//        for (Handler handler : this.handlers) {
//            handlers.add(handler);
//        }
//    }
//
//    public void configInterceptors(Interceptors interceptors) {
//        for (Interceptor interceptor : this.interceptors) {
//            interceptors.add(interceptor);
//        }
//    }
//
//    public void configPlugins(Plugins plugins) {
//        for (IPlugin plugin : this.plugins) {
//            plugins.add(plugin);
//        }
//    }



}
