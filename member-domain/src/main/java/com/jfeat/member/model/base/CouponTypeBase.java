/*
 *   Copyright (C) 2014-2018 www.kequandian.net
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
package com.jfeat.member.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfeat.core.BaseModel;
import java.math.BigDecimal;
import java.util.Date;

public abstract class CouponTypeBase<M extends CouponTypeBase<?>> extends BaseModel<M> implements IBean {

    /**
     * Table fields 
     */
    public enum Fields {
        ID("id"),
        PRODUCT_ID("product_id"),
        NAME("name"),
        TYPE("type"),
        IS_LIMITED("is_limited"),
        AUTO_GIVE("auto_give"),
        UP_TO("up_to"),
        DISPLAY_NAME("display_name"),
        MONEY("money"),
        DISCOUNT("discount"),
        DESCRIPTION("description"),
        COND("cond"),
        VALID_DAYS("valid_days"),
        TEMPLATE("template"),
        CODE("code"),
        ENABLED("enabled");
        
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

    public void setProductId(Integer var) {
        set(Fields.PRODUCT_ID.toString(), var);
    }

    public Integer getProductId() {
        return (Integer) get(Fields.PRODUCT_ID.toString());
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

    public void setIsLimited(Integer var) {
        set(Fields.IS_LIMITED.toString(), var);
    }

    public Integer getIsLimited() {
        return (Integer) get(Fields.IS_LIMITED.toString());
    }

    public void setAutoGive(Integer var) {
        set(Fields.AUTO_GIVE.toString(), var);
    }

    public Integer getAutoGive() {
        return (Integer) get(Fields.AUTO_GIVE.toString());
    }

    public void setUpTo(Integer var) {
        set(Fields.UP_TO.toString(), var);
    }

    public Integer getUpTo() {
        return (Integer) get(Fields.UP_TO.toString());
    }

    public void setDisplayName(String var) {
        set(Fields.DISPLAY_NAME.toString(), var);
    }

    public String getDisplayName() {
        return (String) get(Fields.DISPLAY_NAME.toString());
    }

    public void setMoney(Integer var) {
        set(Fields.MONEY.toString(), var);
    }

    public Integer getMoney() {
        return (Integer) get(Fields.MONEY.toString());
    }

    public void setDiscount(Integer var) {
        set(Fields.DISCOUNT.toString(), var);
    }

    public Integer getDiscount() {
        return (Integer) get(Fields.DISCOUNT.toString());
    }

    public void setDescription(String var) {
        set(Fields.DESCRIPTION.toString(), var);
    }

    public String getDescription() {
        return (String) get(Fields.DESCRIPTION.toString());
    }

    public void setCond(String var) {
        set(Fields.COND.toString(), var);
    }

    public String getCond() {
        return (String) get(Fields.COND.toString());
    }

    public void setValidDays(Integer var) {
        set(Fields.VALID_DAYS.toString(), var);
    }

    public Integer getValidDays() {
        return (Integer) get(Fields.VALID_DAYS.toString());
    }

    public void setTemplate(String var) {
        set(Fields.TEMPLATE.toString(), var);
    }

    public String getTemplate() {
        return (String) get(Fields.TEMPLATE.toString());
    }

    public void setCode(String var) {
        set(Fields.CODE.toString(), var);
    }

    public String getCode() {
        return (String) get(Fields.CODE.toString());
    }

    public void setEnabled(Integer var) {
        set(Fields.ENABLED.toString(), var);
    }

    public Integer getEnabled() {
        return (Integer) get(Fields.ENABLED.toString());
    }


    ///////////////////////////////////////////////////////////////////////////
    
}
