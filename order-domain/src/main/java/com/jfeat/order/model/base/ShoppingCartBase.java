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
package com.jfeat.order.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfeat.core.BaseModel;
import java.math.BigDecimal;
import java.util.Date;

public abstract class ShoppingCartBase<M extends ShoppingCartBase<?>> extends BaseModel<M> implements IBean {

    /**
     * Table fields 
     */
    public enum Fields {
        ID("id"),
        USER_ID("user_id"),
        PRODUCT_ID("product_id"),
        PRODUCT_NAME("product_name"),
        COVER("cover"),
        QUANTITY("quantity"),
        PRICE("price"),
        WEIGHT("weight"),
        BULK("bulk"),
        CREATED_DATE("created_date"),
        PRODUCT_SPECIFICATION_ID("product_specification_id"),
        PRODUCT_SPECIFICATION_NAME("product_specification_name"),
        FARE_ID("fare_id"),
        MARKETING("marketing"),
        MARKETING_ID("marketing_id");
        
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

    public void setProductId(Integer var) {
        set(Fields.PRODUCT_ID.toString(), var);
    }

    public Integer getProductId() {
        return (Integer) get(Fields.PRODUCT_ID.toString());
    }

    public void setProductName(String var) {
        set(Fields.PRODUCT_NAME.toString(), var);
    }

    public String getProductName() {
        return (String) get(Fields.PRODUCT_NAME.toString());
    }

    public void setCover(String var) {
        set(Fields.COVER.toString(), var);
    }

    public String getCover() {
        return (String) get(Fields.COVER.toString());
    }

    public void setQuantity(Integer var) {
        set(Fields.QUANTITY.toString(), var);
    }

    public Integer getQuantity() {
        return (Integer) get(Fields.QUANTITY.toString());
    }

    public void setPrice(BigDecimal var) {
        set(Fields.PRICE.toString(), var);
    }

    public BigDecimal getPrice() {
        return (BigDecimal) get(Fields.PRICE.toString());
    }

    public void setWeight(Integer var) {
        set(Fields.WEIGHT.toString(), var);
    }

    public Integer getWeight() {
        return (Integer) get(Fields.WEIGHT.toString());
    }

    public void setBulk(Integer var) {
        set(Fields.BULK.toString(), var);
    }

    public Integer getBulk() {
        return (Integer) get(Fields.BULK.toString());
    }

    public void setCreatedDate(Date var) {
        set(Fields.CREATED_DATE.toString(), var);
    }

    public Date getCreatedDate() {
        return (Date) get(Fields.CREATED_DATE.toString());
    }

    public void setProductSpecificationId(Integer var) {
        set(Fields.PRODUCT_SPECIFICATION_ID.toString(), var);
    }

    public Integer getProductSpecificationId() {
        return (Integer) get(Fields.PRODUCT_SPECIFICATION_ID.toString());
    }

    public void setProductSpecificationName(String var) {
        set(Fields.PRODUCT_SPECIFICATION_NAME.toString(), var);
    }

    public String getProductSpecificationName() {
        return (String) get(Fields.PRODUCT_SPECIFICATION_NAME.toString());
    }

    public void setFareId(Integer var) {
        set(Fields.FARE_ID.toString(), var);
    }

    public Integer getFareId() {
        return (Integer) get(Fields.FARE_ID.toString());
    }

    public void setMarketing(String var) {
        set(Fields.MARKETING.toString(), var);
    }

    public String getMarketing() {
        return (String) get(Fields.MARKETING.toString());
    }

    public void setMarketingId(Integer var) {
        set(Fields.MARKETING_ID.toString(), var);
    }

    public Integer getMarketingId() {
        return (Integer) get(Fields.MARKETING_ID.toString());
    }


    ///////////////////////////////////////////////////////////////////////////
    
}