/*
 *   Copyright (C) 2014-2017 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

/*
 * This file is automatically generated by tools.
 * It defines fields related to the database table.
 *
 * DON'T EDIT IT. OTHERWIDE IT WILL BE OVERRIDE WHEN RE-GENERATING IF TABLE CHANGE.
 */
package com.jfeat.config.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfeat.core.BaseModel;
import java.math.BigDecimal;
import java.util.Date;

public abstract class ConfigBase<M extends ConfigBase<?>> extends BaseModel<M> implements IBean {

    /**
     * Table fields 
     */
    public enum Fields {
        ID("id"),
        USER_ID("user_id"),
        GROUP_ID("group_id"),
        NAME("name"),
        KEY_NAME("key_name"),
        VALUE_TYPE("value_type"),
        VALUE("value"),
        TYPE("type"),
        VISIBLE("visible"),
        DESCRIPTION("description"),
        READONLY("readonly");
        
        private String name;
        Fields(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
        public String like(Object obj) {
            return new StringBuilder(this.toString()).append(" LIKE ").append(obj).toString();
        }
        public String eq(Object obj) {
            return new StringBuilder(this.toString()).append("=").append(obj).toString();
        }
        public String ge(Object obj) {
            return new StringBuilder(this.toString()).append(">=").append(obj).toString();
        }
        public String lt(Object obj) {
            return new StringBuilder(this.toString()).append("<").append(obj).toString();
        }
        public String gt(Object obj) {
            return new StringBuilder(this.toString()).append(">").append(obj).toString();
        }
        public String le(Object obj) {
            return new StringBuilder(this.toString()).append("<=").append(obj).toString();
        }
        public String isNull() {
            return new StringBuilder(this.toString()).append(" IS NULL").toString();
        }
        public String notNull() {
            return new StringBuilder(this.toString()).append(" NOT NULL").toString();
        }
        public String notEquals(Object obj) {
            return new StringBuilder(this.toString()).append("<>").append(obj).toString();
        }
    }

    public void setId(Integer var) {
        set(Fields.ID.toString(), var);
    }

    public Integer getId() {
        return (Integer) get(Fields.ID.toString());
    }

    public void setUserId(Integer var) {
        set(Fields.USER_ID.toString(), var);
    }

    public Integer getUserId() {
        return (Integer) get(Fields.USER_ID.toString());
    }

    public void setGroupId(Integer var) {
        set(Fields.GROUP_ID.toString(), var);
    }

    public Integer getGroupId() {
        return (Integer) get(Fields.GROUP_ID.toString());
    }

    public void setName(String var) {
        set(Fields.NAME.toString(), var);
    }

    public String getName() {
        return (String) get(Fields.NAME.toString());
    }

    public void setKeyName(String var) {
        set(Fields.KEY_NAME.toString(), var);
    }

    public String getKeyName() {
        return (String) get(Fields.KEY_NAME.toString());
    }

    public void setValueType(String var) {
        set(Fields.VALUE_TYPE.toString(), var);
    }

    public String getValueType() {
        return (String) get(Fields.VALUE_TYPE.toString());
    }

    public void setValue(String var) {
        set(Fields.VALUE.toString(), var);
    }

    public String getValue() {
        return (String) get(Fields.VALUE.toString());
    }

    public void setType(String var) {
        set(Fields.TYPE.toString(), var);
    }

    public String getType() {
        return (String) get(Fields.TYPE.toString());
    }

    public void setVisible(Integer var) {
        set(Fields.VISIBLE.toString(), var);
    }

    public Integer getVisible() {
        return (Integer) get(Fields.VISIBLE.toString());
    }

    public void setDescription(String var) {
        set(Fields.DESCRIPTION.toString(), var);
    }

    public String getDescription() {
        return (String) get(Fields.DESCRIPTION.toString());
    }

    public void setReadonly(Integer var) {
        set(Fields.READONLY.toString(), var);
    }

    public Integer getReadonly() {
        return (Integer) get(Fields.READONLY.toString());
    }


    ///////////////////////////////////////////////////////////////////////////
    
}
