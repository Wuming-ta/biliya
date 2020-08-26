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

public abstract class PhysicalSettlementProportionBase<M extends PhysicalSettlementProportionBase<?>> extends BaseModel<M> implements IBean {

    /**
     * Table fields 
     */
    public enum Fields {
        ID("id"),
        MIN_AMOUNT("min_amount"),
        MAX_AMOUNT("max_amount"),
        PERCENTAGE("percentage");
        
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

    public void setMinAmount(BigDecimal var) {
        set(Fields.MIN_AMOUNT.toString(), var);
    }

    public BigDecimal getMinAmount() {
        return (BigDecimal) get(Fields.MIN_AMOUNT.toString());
    }

    public void setMaxAmount(BigDecimal var) {
        set(Fields.MAX_AMOUNT.toString(), var);
    }

    public BigDecimal getMaxAmount() {
        return (BigDecimal) get(Fields.MAX_AMOUNT.toString());
    }

    public void setPercentage(BigDecimal var) {
        set(Fields.PERCENTAGE.toString(), var);
    }

    public BigDecimal getPercentage() {
        return (BigDecimal) get(Fields.PERCENTAGE.toString());
    }


    ///////////////////////////////////////////////////////////////////////////
    
}
