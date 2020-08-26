/*
 *   Copyright (C) 2014-2019 www.kequandian.net
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
package com.jfeat.partner.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfeat.core.BaseModel;
import java.math.BigDecimal;
import java.util.Date;

public abstract class SettlementProportionBase<M extends SettlementProportionBase<?>> extends BaseModel<M> implements IBean {

    /**
     * Table fields 
     */
    public enum Fields {
        ID("id"),
        NAME("name"),
        TYPE("type"),
        PROPORTION("proportion"),
        TURNOVER_QUOTA("turnover_quota"),
        LEVEL("level"),
        VISIBLE("visible");
        
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
            return new StringBuilder(this.toString()).append(" IS NOT NULL").toString();
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

    public void setName(String var) {
        set(Fields.NAME.toString(), var);
    }

    public String getName() {
        return (String) get(Fields.NAME.toString());
    }

    public void setType(String var) {
        set(Fields.TYPE.toString(), var);
    }

    public String getType() {
        return (String) get(Fields.TYPE.toString());
    }

    public void setProportion(String var) {
        set(Fields.PROPORTION.toString(), var);
    }

    public String getProportion() {
        return (String) get(Fields.PROPORTION.toString());
    }

    public void setTurnoverQuota(Integer var) {
        set(Fields.TURNOVER_QUOTA.toString(), var);
    }

    public Integer getTurnoverQuota() {
        return (Integer) get(Fields.TURNOVER_QUOTA.toString());
    }

    public void setLevel(Integer var) {
        set(Fields.LEVEL.toString(), var);
    }

    public Integer getLevel() {
        return (Integer) get(Fields.LEVEL.toString());
    }

    public void setVisible(Integer var) {
        set(Fields.VISIBLE.toString(), var);
    }

    public Integer getVisible() {
        return (Integer) get(Fields.VISIBLE.toString());
    }


    ///////////////////////////////////////////////////////////////////////////
    
}
