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

public abstract class CouponOverdueBase<M extends CouponOverdueBase<?>> extends BaseModel<M> implements IBean {

    /**
     * Table fields 
     */
    public enum Fields {
        ID("id"),
        USER_ID("user_id"),
        COUPON_ID("coupon_id"),
        END_TIME("end_time");
        
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

    public void setUserId(Integer var) {
        set(Fields.USER_ID.toString(), var);
    }

    public Integer getUserId() {
        return (Integer) get(Fields.USER_ID.toString());
    }

    public void setCouponId(Integer var) {
        set(Fields.COUPON_ID.toString(), var);
    }

    public Integer getCouponId() {
        return (Integer) get(Fields.COUPON_ID.toString());
    }

    public void setEndTime(Date var) {
        set(Fields.END_TIME.toString(), var);
    }

    public Date getEndTime() {
        return (Date) get(Fields.END_TIME.toString());
    }


    ///////////////////////////////////////////////////////////////////////////
    
}