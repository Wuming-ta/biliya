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

package com.jfeat.ui;

import com.jfeat.core.ModuleHolder;
import com.jfeat.core.RestController;
import com.jfeat.ui.MenuHolder;
import com.jfeat.ui.model.*;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.ext.interceptor.Restful;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.List;

/**
 * Created by jacky on 12/21/14.
 */
public class MenuInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation ai) {
        if (ai.getController().getClass().getSuperclass() != RestController.class) {
            String sessionId = ai.getController().getSession().getId();

            MenuHolder menuHolder = getMenuHolder(sessionId);
            menuHolder.setSelectedMenu(ai.getControllerKey(), ai.getController().getParaMap());
            ai.getController().setAttr("menus", menuHolder.getMenus());

            NotificationHolder notificationHolder = getNotificationHolder(sessionId);
            ai.getController().setAttr("notifications", notificationHolder.getNotifications());
        }
        ai.invoke();
    }

    private MenuHolder getMenuHolder(String sessionId) {
        MenuHolder menuHolder = CacheKit.get("menu", sessionId);
        if (menuHolder == null) {
            menuHolder = new MenuHolder();
            menuHolder.setPrivilegeStrategyMap(ModuleHolder.me().getPrivilegeStrategyMap());
            menuHolder.fetchMenus();
            CacheKit.put("menu", sessionId, menuHolder);
        }
        return menuHolder;
    }

    private NotificationHolder getNotificationHolder(String sessionId) {
        NotificationHolder notificationHolder = CacheKit.get("notification", sessionId);
        if (notificationHolder == null) {
            notificationHolder = new NotificationHolder();
            notificationHolder.setPrivilegeStrategyMap(ModuleHolder.me().getPrivilegeStrategyMap());
            notificationHolder.fetchNotifications();
            CacheKit.put("notification", sessionId, notificationHolder);
        }
        return notificationHolder;
    }
}
