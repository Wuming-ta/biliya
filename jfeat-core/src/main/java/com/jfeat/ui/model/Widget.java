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
import java.util.Date;
import java.util.List;

@TableBind(tableName = "t_widget")
public class Widget extends BaseModel<Widget> {

    /**
     * Only use for query.
     */
    public static Widget dao = new Widget();

    /**
     * Table fields 
     */
    public enum Fields {
        ID("id"),
        NAME("name"),
        URL("url"),
        WIDTH("width"),
        SORT_ORDER("sort_order"),
        DISPLAY_NAME("display_name"),
        VISIBLE("visible");
        
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

    public void setWidth(Integer var) {
        set(Fields.WIDTH.toString(), var);
    }

    public Integer getWidth() {
        return (Integer) get(Fields.WIDTH.toString());
    }

    public void setSortOrder(Integer var) {
        set(Fields.SORT_ORDER.toString(), var);
    }

    public Integer getSortOrder() {
        return (Integer) get(Fields.SORT_ORDER.toString());
    }

    public void setVisible(Integer var) {
        set(Fields.VISIBLE.toString(), var);
    }

    public Integer getVisible() {
        return (Integer) get(Fields.VISIBLE.toString());
    }

    public void setDisplayName(String var) {
        set(Fields.DISPLAY_NAME.toString(), var);
    }

    public String getDisplayName() {
        return (String) get(Fields.DISPLAY_NAME.toString());
    }

    ///////////////////////////////////////////////////////////////////////////


    public List<Widget> findAll() {
        return find("select * from t_widget order by sort_order");
    }
}
