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
package com.jfeat.merchant.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfeat.core.BaseModel;
import java.math.BigDecimal;
import java.util.Date;

public abstract class SettledMerchantBase<M extends SettledMerchantBase<?>> extends BaseModel<M> implements IBean {

    /**
     * Table fields 
     */
    public enum Fields {
        ID("id"),
        TYPE_ID("type_id"),
        NAME("name"),
        DESCRIPTION("description"),
        LOGO("logo"),
        ADDRESS("address"),
        PHONE("phone"),
        CONTACT_USER("contact_user"),
        CONTACT_PHONE("contact_phone"),
        CONTACT_EMAIL("contact_email"),
        ID_NUMBER("id_number"),
        ID_FRONT("id_front"),
        ID_BACK("id_back"),
        BUSINESS_LICENSE_NUMBER("business_license_number"),
        BUSINESS_LICENSE_IMAGE("business_license_image"),
        STATUS("status"),
        CREATED_DATE("created_date"),
        APPROVED_DATE("approved_date"),
        QUALITY_RANKING("quality_ranking"),
        ATTITUDE_RANKING("attitude_ranking"),
        EXPRESS_RANKING("express_ranking");
        
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

    public void setTypeId(Integer var) {
        set(Fields.TYPE_ID.toString(), var);
    }

    public Integer getTypeId() {
        return (Integer) get(Fields.TYPE_ID.toString());
    }

    public void setName(String var) {
        set(Fields.NAME.toString(), var);
    }

    public String getName() {
        return (String) get(Fields.NAME.toString());
    }

    public void setDescription(String var) {
        set(Fields.DESCRIPTION.toString(), var);
    }

    public String getDescription() {
        return (String) get(Fields.DESCRIPTION.toString());
    }

    public void setLogo(String var) {
        set(Fields.LOGO.toString(), var);
    }

    public String getLogo() {
        return (String) get(Fields.LOGO.toString());
    }

    public void setAddress(String var) {
        set(Fields.ADDRESS.toString(), var);
    }

    public String getAddress() {
        return (String) get(Fields.ADDRESS.toString());
    }

    public void setPhone(String var) {
        set(Fields.PHONE.toString(), var);
    }

    public String getPhone() {
        return (String) get(Fields.PHONE.toString());
    }

    public void setContactUser(String var) {
        set(Fields.CONTACT_USER.toString(), var);
    }

    public String getContactUser() {
        return (String) get(Fields.CONTACT_USER.toString());
    }

    public void setContactPhone(String var) {
        set(Fields.CONTACT_PHONE.toString(), var);
    }

    public String getContactPhone() {
        return (String) get(Fields.CONTACT_PHONE.toString());
    }

    public void setContactEmail(String var) {
        set(Fields.CONTACT_EMAIL.toString(), var);
    }

    public String getContactEmail() {
        return (String) get(Fields.CONTACT_EMAIL.toString());
    }

    public void setIdNumber(String var) {
        set(Fields.ID_NUMBER.toString(), var);
    }

    public String getIdNumber() {
        return (String) get(Fields.ID_NUMBER.toString());
    }

    public void setIdFront(String var) {
        set(Fields.ID_FRONT.toString(), var);
    }

    public String getIdFront() {
        return (String) get(Fields.ID_FRONT.toString());
    }

    public void setIdBack(String var) {
        set(Fields.ID_BACK.toString(), var);
    }

    public String getIdBack() {
        return (String) get(Fields.ID_BACK.toString());
    }

    public void setBusinessLicenseNumber(String var) {
        set(Fields.BUSINESS_LICENSE_NUMBER.toString(), var);
    }

    public String getBusinessLicenseNumber() {
        return (String) get(Fields.BUSINESS_LICENSE_NUMBER.toString());
    }

    public void setBusinessLicenseImage(String var) {
        set(Fields.BUSINESS_LICENSE_IMAGE.toString(), var);
    }

    public String getBusinessLicenseImage() {
        return (String) get(Fields.BUSINESS_LICENSE_IMAGE.toString());
    }

    public void setStatus(String var) {
        set(Fields.STATUS.toString(), var);
    }

    public String getStatus() {
        return (String) get(Fields.STATUS.toString());
    }

    public void setCreatedDate(Date var) {
        set(Fields.CREATED_DATE.toString(), var);
    }

    public Date getCreatedDate() {
        return (Date) get(Fields.CREATED_DATE.toString());
    }

    public void setApprovedDate(Date var) {
        set(Fields.APPROVED_DATE.toString(), var);
    }

    public Date getApprovedDate() {
        return (Date) get(Fields.APPROVED_DATE.toString());
    }

    public void setQualityRanking(Integer var) {
        set(Fields.QUALITY_RANKING.toString(), var);
    }

    public Integer getQualityRanking() {
        return (Integer) get(Fields.QUALITY_RANKING.toString());
    }

    public void setAttitudeRanking(Integer var) {
        set(Fields.ATTITUDE_RANKING.toString(), var);
    }

    public Integer getAttitudeRanking() {
        return (Integer) get(Fields.ATTITUDE_RANKING.toString());
    }

    public void setExpressRanking(Integer var) {
        set(Fields.EXPRESS_RANKING.toString(), var);
    }

    public Integer getExpressRanking() {
        return (Integer) get(Fields.EXPRESS_RANKING.toString());
    }


    ///////////////////////////////////////////////////////////////////////////
    
}