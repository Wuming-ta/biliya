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

package com.jfeat.mall.api.response;

import java.util.List;

/**
 * Created by jacky on 5/4/16.
 */
public class OrderResponse extends ApiResponse {

    private String message;
    private Data data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {

        /**
         * detail : 6F
         * phone : 1380000000
         * contact_user : Mr Huang
         * remark : afaf
         * invoice : 1
         * street : jianzhong road
         * trade_number : 23423
         * deal_date : 2016-04-30 23:22:22
         * city : GZ
         * id : 3
         * cover : http://112.74.26.228:8000/p/516c02b5e8ceb745b6dd61b6e77b3e17.png
         * confirm_date : 2016-05-03 11:11:11
         * description : 超效洁净护理洗衣液2.5L【全国包邮】 x 1.
         * province : GD
         * order_items : [{"id":5,"cover":"http://112.74.26.228:8000/p/516c02b5e8ceb745b6dd61b6e77b3e17.png","price":34.8,"cost_price":20,"final_price":34.8,"product_id":1,"status":"CREATED","product_name":"超效洁净护理洗衣液2.5L【全国包邮】","quantity":1,"order_id":3}]
         * user_id : 1
         * district : Tiahne
         * deliver_date : 2016-05-01 22:22:22
         * created_date : 2016-04-26 10:27:56
         * order_number : 0000000101461637676506360
         * zip : 510000
         * status : CREATED_PAY_PENDING
         * invoice_title : ABC company
         * receiving_time : anytime
         * deliver_order_number : 24234242
         * total_price : 34.8
         * freight : 0.0
         * pay_date : 2016-05-01 04:11:11
         * payment_type : ALIPAY
         * marketing : PIECE-GROUP
         * marketing_id : 1
         * marketing_description : 拼团活动XXX
         */

        private String detail;
        private String phone;
        private String contact_user;
        private String remark;
        private int invoice;
        private String street;
        private String trade_number;
        private String deal_date;
        private String city;
        private int id;
        private String cover;
        private String confirm_date;
        private String description;
        private String province;
        private int user_id;
        private String district;
        private String deliver_date;
        private String created_date;
        private String order_number;
        private String zip;
        private String status;
        private String invoice_title;
        private String receiving_time;
        private String deliver_order_number;
        private double total_price;
        private double freight;
        private String pay_date;
        private String payment_type;
        private String marketing;
        private Integer marketing_id;
        private String marketing_description;

        /**
         * id : 5
         * cover : http://112.74.26.228:8000/p/516c02b5e8ceb745b6dd61b6e77b3e17.png
         * price : 34.8
         * cost_price : 20.0
         * final_price : 34.8
         * product_id : 1
         * status : CREATED
         * product_name : 超效洁净护理洗衣液2.5L【全国包邮】
         * quantity : 1
         * order_id : 3
         */
        private List<OrderItemsEntity> order_items;

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setContact_user(String contact_user) {
            this.contact_user = contact_user;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public void setInvoice(int invoice) {
            this.invoice = invoice;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public void setTrade_number(String trade_number) {
            this.trade_number = trade_number;
        }

        public void setDeal_date(String deal_date) {
            this.deal_date = deal_date;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public void setConfirm_date(String confirm_date) {
            this.confirm_date = confirm_date;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public void setDeliver_date(String deliver_date) {
            this.deliver_date = deliver_date;
        }

        public void setCreated_date(String created_date) {
            this.created_date = created_date;
        }

        public void setOrder_number(String order_number) {
            this.order_number = order_number;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setInvoice_title(String invoice_title) {
            this.invoice_title = invoice_title;
        }

        public void setReceiving_time(String receiving_time) {
            this.receiving_time = receiving_time;
        }

        public void setDeliver_order_number(String deliver_order_number) {
            this.deliver_order_number = deliver_order_number;
        }

        public void setTotal_price(double total_price) {
            this.total_price = total_price;
        }

        public void setFreight(double freight) {
            this.freight = freight;
        }

        public void setPay_date(String pay_date) {
            this.pay_date = pay_date;
        }

        public void setPayment_type(String payment_type) {
            this.payment_type = payment_type;
        }

        public void setOrder_items(List<OrderItemsEntity> order_items) {
            this.order_items = order_items;
        }

        public String getDetail() {
            return detail;
        }

        public String getPhone() {
            return phone;
        }

        public String getContact_user() {
            return contact_user;
        }

        public String getRemark() {
            return remark;
        }

        public int getInvoice() {
            return invoice;
        }

        public String getStreet() {
            return street;
        }

        public String getTrade_number() {
            return trade_number;
        }

        public String getDeal_date() {
            return deal_date;
        }

        public String getCity() {
            return city;
        }

        public int getId() {
            return id;
        }

        public String getCover() {
            return cover;
        }

        public String getConfirm_date() {
            return confirm_date;
        }

        public String getDescription() {
            return description;
        }

        public String getProvince() {
            return province;
        }

        public int getUser_id() {
            return user_id;
        }

        public String getDistrict() {
            return district;
        }

        public String getDeliver_date() {
            return deliver_date;
        }

        public String getCreated_date() {
            return created_date;
        }

        public String getOrder_number() {
            return order_number;
        }

        public String getZip() {
            return zip;
        }

        public String getStatus() {
            return status;
        }

        public String getInvoice_title() {
            return invoice_title;
        }

        public String getReceiving_time() {
            return receiving_time;
        }

        public String getDeliver_order_number() {
            return deliver_order_number;
        }

        public double getTotal_price() {
            return total_price;
        }

        public double getFreight() {
            return freight;
        }

        public String getPay_date() {
            return pay_date;
        }

        public String getPayment_type() {
            return payment_type;
        }

        public List<OrderItemsEntity> getOrder_items() {
            return order_items;
        }

        public String getMarketing() {
            return marketing;
        }

        public void setMarketing(String marketing) {
            this.marketing = marketing;
        }

        public Integer getMarketing_id() {
            return marketing_id;
        }

        public void setMarketing_id(Integer marketing_id) {
            this.marketing_id = marketing_id;
        }

        public String getMarketing_description() {
            return marketing_description;
        }

        public void setMarketing_description(String marketing_description) {
            this.marketing_description = marketing_description;
        }

        public static class OrderItemsEntity {
            private int id;
            private String cover;
            private double price;
            private double cost_price;
            private double final_price;
            private int product_id;
            private String status;
            private String product_name;
            private int quantity;
            private int order_id;

            public void setId(int id) {
                this.id = id;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public void setPrice(double price) {
                this.price = price;
            }

            public void setCost_price(double cost_price) {
                this.cost_price = cost_price;
            }

            public void setFinal_price(double final_price) {
                this.final_price = final_price;
            }

            public void setProduct_id(int product_id) {
                this.product_id = product_id;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public void setProduct_name(String product_name) {
                this.product_name = product_name;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }

            public void setOrder_id(int order_id) {
                this.order_id = order_id;
            }

            public int getId() {
                return id;
            }

            public String getCover() {
                return cover;
            }

            public double getPrice() {
                return price;
            }

            public double getCost_price() {
                return cost_price;
            }

            public double getFinal_price() {
                return final_price;
            }

            public int getProduct_id() {
                return product_id;
            }

            public String getStatus() {
                return status;
            }

            public String getProduct_name() {
                return product_name;
            }

            public int getQuantity() {
                return quantity;
            }

            public int getOrder_id() {
                return order_id;
            }
        }
    }

}
