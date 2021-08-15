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

public abstract class OrderItemBase<M extends OrderItemBase<?>> extends BaseModel<M> implements IBean {

    /**
     * Table fields 
     */
    public enum Fields {
        ID("id"),
        ORDER_ID("order_id"),
        PRODUCT_ID("product_id"),
        PRODUCT_NAME("product_name"),
        QUANTITY("quantity"),
        PRICE("price"),
        FINAL_PRICE("final_price"),
        STATUS("status"),
        COST_PRICE("cost_price"),
        COVER("cover"),
        PARTNER_LEVEL_ZONE("partner_level_zone"),
        PRODUCT_SPECIFICATION_NAME("product_specification_name"),
        PRODUCT_SPECIFICATION_ID("product_specification_id"),
        WEIGHT("weight"),
        BULK("bulk"),
        BARCODE("barcode"),
        STORE_LOCATION("store_location"),
        MARKETING("marketing"),
        MARKETING_ID("marketing_id"),
        MARKETING_DESCRIPTION("marketing_description"),
        SKU_ID("sku_id"),
        WAREHOUSE_ID("warehouse_id"),
        MID("mid");
        
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

    public void setOrderId(Integer var) {
        set(Fields.ORDER_ID.toString(), var);
    }

    public Integer getOrderId() {
        return (Integer) get(Fields.ORDER_ID.toString());
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

    public void setFinalPrice(BigDecimal var) {
        set(Fields.FINAL_PRICE.toString(), var);
    }

    public BigDecimal getFinalPrice() {
        return (BigDecimal) get(Fields.FINAL_PRICE.toString());
    }

    public void setStatus(String var) {
        set(Fields.STATUS.toString(), var);
    }

    public String getStatus() {
        return (String) get(Fields.STATUS.toString());
    }

    public void setCostPrice(BigDecimal var) {
        set(Fields.COST_PRICE.toString(), var);
    }

    public BigDecimal getCostPrice() {
        return (BigDecimal) get(Fields.COST_PRICE.toString());
    }

    public void setCover(String var) {
        set(Fields.COVER.toString(), var);
    }

    public String getCover() {
        return (String) get(Fields.COVER.toString());
    }

    public void setPartnerLevelZone(Integer var) {
        set(Fields.PARTNER_LEVEL_ZONE.toString(), var);
    }

    public Integer getPartnerLevelZone() {
        return (Integer) get(Fields.PARTNER_LEVEL_ZONE.toString());
    }

    public void setProductSpecificationName(String var) {
        set(Fields.PRODUCT_SPECIFICATION_NAME.toString(), var);
    }

    public String getProductSpecificationName() {
        return (String) get(Fields.PRODUCT_SPECIFICATION_NAME.toString());
    }

    public void setProductSpecificationId(Integer var) {
        set(Fields.PRODUCT_SPECIFICATION_ID.toString(), var);
    }

    public Integer getProductSpecificationId() {
        return (Integer) get(Fields.PRODUCT_SPECIFICATION_ID.toString());
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

    public void setBarcode(String var) {
        set(Fields.BARCODE.toString(), var);
    }

    public String getBarcode() {
        return (String) get(Fields.BARCODE.toString());
    }

    public void setStoreLocation(String var) {
        set(Fields.STORE_LOCATION.toString(), var);
    }

    public String getStoreLocation() {
        return (String) get(Fields.STORE_LOCATION.toString());
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

    public void setMarketingDescription(String var) {
        set(Fields.MARKETING_DESCRIPTION.toString(), var);
    }

    public String getMarketingDescription() {
        return (String) get(Fields.MARKETING_DESCRIPTION.toString());
    }

    public void setSkuId(String var) {
        set(Fields.SKU_ID.toString(), var);
    }

    public String getSkuId() {
        return (String) get(Fields.SKU_ID.toString());
    }

    public void setWarehouseId(String var) {
        set(Fields.WAREHOUSE_ID.toString(), var);
    }

    public String getWarehouseId() {
        return (String) get(Fields.WAREHOUSE_ID.toString());
    }

    public void setMid(Integer var) {
        set(Fields.MID.toString(), var);
    }

    public Integer getMid() {
        return (Integer) get(Fields.MID.toString());
    }


    ///////////////////////////////////////////////////////////////////////////
    
}