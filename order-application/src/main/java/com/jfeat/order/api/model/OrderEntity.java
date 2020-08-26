/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.order.api.model;

import java.util.List;

/**
 * Created by jacky on 2/1/16.
 */
public class OrderEntity {

    private ExtEntity ext;

    public ExtEntity getExt() {
        return ext;
    }

    public OrderEntity setExt(ExtEntity ext) {
        this.ext = ext;
        return this;
    }

    /**
     * payment_type : ALIPAY
     * remark : null
     * status : null
     * freight: 10.00
     * contact : {"name":"Mr Huang","phone":"1380000000","zip":"510000","province":"GD","city":"GZ","district":"Tiahne","street":"jianzhong road","detail":"6F"}
     * order_items : [{"product_id":1,"quantity":1}]
     */

    private String delivery_type;
    private String origin;
    private String payment_type;
    private String remark;
    private String status;
    /**
     * name : Mr Huang
     * phone : 1380000000
     * zip : 510000
     * province : GD
     * city : GZ
     * district : Tiahne
     * street : jianzhong road
     * detail : 6F
     */

    private ContactEntity contact;
    /**
     * product_id : 1
     * quantity : 1
     */

    private List<OrderItemsEntity> order_items;
    /**
     * receiving_time : anytime
     * invoice : 1
     * invoice_title : ABC company
     */

    private String receiving_time;
    private int invoice;
    private String invoice_title;
    /**
     * coupon_id : 1
     */

    private Integer coupon_id;
    /**
     * marketing : PIECE-GROUP
     * marketing_id : 2
     */

    private String marketing;

    private Integer pay_credit;

    private Integer mid;
    private String mname;

    private String store_id;
    private String store_name;
    private String store_code;

    private String store_user_id;
    private String store_user_code;
    private String store_user_name;

    public Integer getPay_credit() {
        return pay_credit;
    }

    public OrderEntity setPay_credit(Integer pay_credit) {
        this.pay_credit = pay_credit;
        return this;
    }

    public String getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(String delivery_type) {
        this.delivery_type = delivery_type;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setContact(ContactEntity contact) {
        this.contact = contact;
    }

    public void setOrder_items(List<OrderItemsEntity> order_items) {
        this.order_items = order_items;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public String getRemark() {
        return remark;
    }

    public String getStatus() {
        return status;
    }

    public ContactEntity getContact() {
        return contact;
    }

    public List<OrderItemsEntity> getOrder_items() {
        return order_items;
    }

    public void setReceiving_time(String receiving_time) {
        this.receiving_time = receiving_time;
    }

    public void setInvoice(int invoice) {
        this.invoice = invoice;
    }

    public void setInvoice_title(String invoice_title) {
        this.invoice_title = invoice_title;
    }

    public String getReceiving_time() {
        return receiving_time;
    }

    public int getInvoice() {
        return invoice;
    }

    public String getInvoice_title() {
        return invoice_title;
    }

    public Integer getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(Integer coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getMarketing() {
        return marketing;
    }

    public void setMarketing(String marketing) {
        this.marketing = marketing;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public String getStore_user_id() {
        return store_user_id;
    }

    public void setStore_user_id(String store_user_id) {
        this.store_user_id = store_user_id;
    }

    public String getStore_user_code() {
        return store_user_code;
    }

    public void setStore_user_code(String store_user_code) {
        this.store_user_code = store_user_code;
    }

    public String getStore_user_name() {
        return store_user_name;
    }

    public void setStore_user_name(String store_user_name) {
        this.store_user_name = store_user_name;
    }

    public static class ContactEntity {
        private String contact_user;
        private String phone;
        private String zip;
        private String province;
        private String city;
        private String district;
        private String street;
        private String detail;

        public String getContact_user() {
            return contact_user;
        }

        public void setContact_user(String contact_user) {
            this.contact_user = contact_user;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getPhone() {
            return phone;
        }

        public String getZip() {
            return zip;
        }

        public String getProvince() {
            return province;
        }

        public String getCity() {
            return city;
        }

        public String getDistrict() {
            return district;
        }

        public String getStreet() {
            return street;
        }

        public String getDetail() {
            return detail;
        }
    }

    public static class OrderItemsEntity {
        private int product_id;
        private int quantity;
        /**
         * product_specification_id : 21
         * product_specification_name : red
         */

        private Integer product_specification_id;
        /**
         * marketing : WHOLESALE
         * marketing_id : 1
         */

        private Integer marketing_id;


        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getProduct_id() {
            return product_id;
        }

        public int getQuantity() {
            return quantity;
        }


        public Integer getProduct_specification_id() {
            return product_specification_id;
        }

        public void setProduct_specification_id(Integer product_specification_id) {
            this.product_specification_id = product_specification_id;
        }

        public Integer getMarketing_id() {
            return marketing_id;
        }

        public void setMarketing_id(Integer marketing_id) {
            this.marketing_id = marketing_id;
        }

    }

    public static class ExtEntity {
        private String user_type;
        private Integer discount;
        private Integer cuts;
        private String coupon_type;
        private String coupon_id;

        public String getCoupon_type() {
            return coupon_type;
        }

        public String getCoupon_id() {
            return coupon_id;
        }

        public ExtEntity setCoupon_id(String coupon_id) {
            this.coupon_id = coupon_id;
            return this;
        }

        public String getUser_type() {
            return user_type;
        }

        public ExtEntity setUser_type(String user_type) {
            this.user_type = user_type;
            return this;
        }

        public ExtEntity setCoupon_type(String coupon_type) {
            this.coupon_type = coupon_type;
            return this;
        }

        public Integer getDiscount() {
            return discount;
        }

        public ExtEntity setDiscount(Integer discount) {
            this.discount = discount;
            return this;
        }

        public Integer getCuts() {
            return cuts;
        }

        public ExtEntity setCuts(Integer cuts) {
            this.cuts = cuts;
            return this;
        }

    }
}
