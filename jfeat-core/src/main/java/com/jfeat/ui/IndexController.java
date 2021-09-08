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

import com.jfeat.core.BaseController;
import com.jfeat.core.ModuleHolder;
import com.jfeat.ui.model.Notification;
import com.jfeat.ui.model.Widget;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jacky on 4/23/15.
 */
@ControllerBind(controllerKey = "/")
public class IndexController extends BaseController {

    public void index() {
        Map<String, IPrivilegeStrategy> privilegeStrategyMap = ModuleHolder.me().getPrivilegeStrategyMap();
        List<Widget> allWidgets = Widget.dao.findAll();
        List<Widget> availableWidgets = new LinkedList<>();
        for (Widget widget : allWidgets) {
            IPrivilegeStrategy privilegeStrategy = privilegeStrategyMap.get(widget.getName());
            if (privilegeStrategy == null || privilegeStrategy.isAllowed(widget.getName())) {
                availableWidgets.add(widget);
            }
        }
        setAttr("widgets", availableWidgets);
    }


    public void updateWidgets() {
        {
            String idsStr = getPara("ids_to_hide");
            if (StrKit.notBlank(idsStr)) {
                String[] idsArr = idsStr.split(",");
                for (int i = 0; i < idsArr.length; i++) {
                    Widget widget = Widget.dao.findById(Integer.parseInt(idsArr[i]));
                    widget.setVisible(0);
                    widget.update();
                }
            }
        }
        {
            String idsStr = getPara("ids_to_show");
            if (StrKit.notBlank(idsStr)) {
                String[] idsArr = idsStr.split(",");
                for (int i = 0; i < idsArr.length; i++) {
                    Widget widget = Widget.dao.findById(Integer.parseInt(idsArr[i]));
                    widget.setVisible(1);
                    widget.update();
                }
            }
        }
        {
            String sortOrders = getPara("sort_orders");
            if (StrKit.notBlank(sortOrders)) {
                String[] idAndSortOrderStrArr = sortOrders.split(",");
                for (int i = 0; i < idAndSortOrderStrArr.length; i++) {
                    String[] idAndSortOrderArr = idAndSortOrderStrArr[i].split("-");
                    if (idAndSortOrderArr.length == 2) {
                        Widget widget = Widget.dao.findById(Integer.parseInt(idAndSortOrderArr[0]));
                        widget.setSortOrder(Integer.parseInt(idAndSortOrderArr[1]));
                        widget.update();
                    }
                }
            }
        }
        {
            String displayNames = getPara("display_names");
            if (StrKit.notBlank(displayNames)) {
                String[] idAndDisplayNameStrArr = displayNames.split(",");
                for (int i = 0; i < idAndDisplayNameStrArr.length; i++) {
                    String[] idAndDisplayNameArr = idAndDisplayNameStrArr[i].split("-");
                    if (idAndDisplayNameArr.length == 2) {
                        Widget widget = Widget.dao.findById(Integer.parseInt(idAndDisplayNameArr[0]));
                        widget.setDisplayName(idAndDisplayNameArr[1]);
                        widget.update();
                    }
                }
            }
        }
        {
            String visibles = getPara("visibles");
            if (StrKit.notBlank(visibles)) {
                CacheKit.removeAll("notification");
                String[] idAndVisibleStrArr = visibles.split(",");
                for (int i = 0; i < idAndVisibleStrArr.length; i++) {
                    String[] idAndVisibleArr = idAndVisibleStrArr[i].split("=");
                    if (idAndVisibleArr.length == 2) {
                        Notification notification = Notification.dao.findById(Integer.parseInt(idAndVisibleArr[0]));
                        notification.setDisplayMode(Integer.parseInt(idAndVisibleArr[1]));
                        notification.update();
                    }
                }
            }
        }
        redirect("/");
    }
}
