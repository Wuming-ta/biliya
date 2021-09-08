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

package com.jfeat.ui.model;

import com.jfeat.core.BaseModel;
import com.jfinal.ext.plugin.tablebind.TableBind;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;

@TableBind(tableName = "t_menu")
public class Menu extends BaseModel<Menu> {

    /**
     * Only use for query.
     */
    public static Menu dao = new Menu();

    /**
     * Table fields
     */
    public enum Fields {
        ID("id"),
        NAME("name"),
        URL("url"),
        SORT_ORDER("sort_order"),
        PARENT_ID("parent_id");

        private String name;
        Fields(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    public void setId(Integer var) {
        set(Fields.ID.toString(), var);
    }

    public Integer getId() {
        return (Integer) get(Fields.ID.toString());
    }

    public void setName(String var) {
        set(Fields.NAME.toString(), var);
    }

    public String getName() {
        return (String) get(Fields.NAME.toString());
    }

    public void setUrl(String var) {
        set(Fields.URL.toString(), var);
    }

    public String getUrl() {
        return (String) get(Fields.URL.toString());
    }

    public void setSortOrder(Integer var) {
        set(Fields.SORT_ORDER.toString(), var);
    }

    public Integer getSortOrder() {
        return (Integer) get(Fields.SORT_ORDER.toString());
    }

    public void setParentId(Integer var) {
        set(Fields.PARENT_ID.toString(), var);
    }

    public Integer getParentId() {
        return (Integer) get(Fields.PARENT_ID.toString());
    }


    ///////////////////////////////////////////////////////////////////////////
    private List<Menu> subMenu = new ArrayList<>();
    private boolean selected;
    private boolean allowed;

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<Menu> getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(List<Menu> subMenu) {
        this.subMenu = subMenu;
    }

    public List<Menu> findByParentId(Integer parentId) {
        if (parentId == null)
            return find("select * from t_menu where parent_id is null order by sort_order");
        return find("select * from t_menu where parent_id=? order by sort_order", parentId);
    }
}
