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

import com.jfeat.ui.model.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jacky on 12/21/14.
 */
public class MenuHolder implements Serializable {
    private static Logger logger = LoggerFactory.getLogger(MenuHolder.class);
    private List<Menu> menus;
    private Map<String, IPrivilegeStrategy> privilegeStrategyMap;

    public MenuHolder() {
        privilegeStrategyMap = new LinkedHashMap<>();
    }

    public void setSelectedMenu(String controllerKey, Map<String, String[]> params) {
        setSelection(menus, controllerKey, params);
    }

    /**
     * mark menu selected if it is selected or its submenu is selected.
     *
     * @param menuList
     * @param controllerKey
     * @return
     */
    private boolean setSelection(List<Menu> menuList, String controllerKey, Map<String, String[]> params) {
        boolean result = false;
        for (Menu menu : menuList) {
            menu.setSelected(false);
            String controllerKeySub = controllerKey.substring(1, controllerKey.length());
            if (menu.getUrl() != null) {
                if ((menu.getUrl().equals("") && controllerKeySub.equals(""))
                        || (!menu.getUrl().equals("") && controllerKeySub.equals(menu.getUrl()))
                        || isMatch(controllerKeySub, menu.getUrl(), params)) {
                    menu.setSelected(true);
                    result = true;
                }
            } else {
                boolean res = setSelection(menu.getSubMenu(), controllerKey, params);
                menu.setSelected(res);
            }
        }
        return result;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                    if (strAllParam.contains("#")) {
                        strAllParam = strAllParam.split("#")[0];
                    }
                }
            }
        }

        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    private static Map<String, String> parseUrl(String URL) {
        Map<String, String> mapRequest = new HashMap<>();

        String[] arrSplit = null;

        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组 www.2cto.com
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (!"".equals(arrSplitEqual[0])) {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * @param controllerKeySub ext
     * @param menuUrl          ext?route=xyz
     * @param params           route=xyz&appUser=0&roleId=1
     * @return
     */
    public boolean isMatch(String controllerKeySub, String menuUrl, Map<String, String[]> params) {
        if (params == null || params.isEmpty()) {
            return false;
        }
        if (menuUrl.contains("?")) {
            String[] menuArray = menuUrl.split("\\?");
            if (menuArray[0].equals(controllerKeySub)) {
                Map<String, String> queryParam = parseUrl(menuUrl);
                for (String key : queryParam.keySet()) {
                    if (params.containsKey(key)) {
                        String queryValue = queryParam.get(key);
                        String[] paramValues = params.get(key);
                        if (paramValues != null && paramValues.length > 0) {
                            for (String v : paramValues) {
                                if (queryValue.equals(v)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void fetchMenus() {
        if (menus == null) {
            List<Menu> menuList = Menu.dao.findByParentId(null);
            for (Menu menu : menuList) {
                menu.setAllowed(true);
                IPrivilegeStrategy privilegeStrategy = privilegeStrategyMap.get(menu.getName());
                if (privilegeStrategy != null) {
                    if (!privilegeStrategy.isAllowed(menu.getName())) {
                        menu.setAllowed(false);
                    }
                }
                retrieveSubMenu(privilegeStrategy, menu);
                List<Menu> subMenus = menu.getSubMenu();
                if (subMenus != null && !subMenus.isEmpty()) {
                    menu.setAllowed(menu.getSubMenu().stream().anyMatch(Menu::isAllowed));
                }
            }
            menus = menuList;
            if (logger.isDebugEnabled()) {
                displayMenu(0, menus);
            }
        }
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public Map<String, IPrivilegeStrategy> getPrivilegeStrategyMap() {
        return privilegeStrategyMap;
    }

    public void setPrivilegeStrategyMap(Map<String, IPrivilegeStrategy> privilegeStrategyMap) {
        this.privilegeStrategyMap = privilegeStrategyMap;
    }

    private void retrieveSubMenu(IPrivilegeStrategy privilegeStrategy, Menu menu) {
        List<Menu> menuList = Menu.dao.findByParentId(menu.getId());
        menu.setSubMenu(menuList);
        for (Menu subMenu : menuList) {
            logger.debug("menu=" + subMenu.getName());
            subMenu.setAllowed(true);
            if (privilegeStrategy != null) {
                if (!privilegeStrategy.isAllowed(subMenu.getName())) {
                    subMenu.setAllowed(false);
                }
            }
            // if subMenu is allowed, then its parent menu should be visible.
            if (subMenu.isAllowed()) {
                menu.setAllowed(true);
            }
            retrieveSubMenu(privilegeStrategy, subMenu);
        }
    }

    private void displayMenu(int indent, List<Menu> menuList) {
        for (Menu menu : menuList) {
            int tempIndent = indent;
            StringBuilder builder = new StringBuilder();
            while (tempIndent-- > 0) {
                builder.append("    ");
            }
            builder.append("menu=");
            builder.append(menu.getName());
            builder.append(", allowed=");
            builder.append(menu.isAllowed());
            logger.debug(builder.toString());
            displayMenu(indent + 1, menu.getSubMenu());
        }
    }
}
