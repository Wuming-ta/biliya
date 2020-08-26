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

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by huangjacky on 16/6/16.
 */
public class OrderCustomerServiceEntity {

    /**
     * order_number : 2342323432432
     * service_type : RETURN
     * reason : AFSFSF
     * express_company : ABC
     * express_number : 1324324
     * * content : afa
     * otherway: true
     */

    /**
     * true: 其他方式退款, false: 原路退回
     */
    private Boolean otherway = false;
    private String order_number;
    private String service_type;
    private String reason;
    private String express_company;
    private String express_number;
    private String content;
    private List<String> images;
    private BigDecimal supplementary_fee;

    private String store_id;
    private String store_name;
    private String store_user_id;
    private String store_user_name;

    public BigDecimal getSupplementary_fee() {
        return supplementary_fee;
    }

    public OrderCustomerServiceEntity setSupplementary_fee(BigDecimal supplementary_fee) {
        this.supplementary_fee = supplementary_fee;
        return this;
    }

    public Boolean getOtherway() {
        return otherway;
    }

    public OrderCustomerServiceEntity setOtherway(Boolean otherway) {
        this.otherway = otherway;
        return this;
    }

    private List<OrderCustomerServiceItemEntity> returns;  //退回项（退货退款单和换货单都需要指定退回项）

    private List<OrderCustomerServiceItemEntity> exchanges;  //置换项（换货单需要指定置换项）



    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExpress_company() {
        return express_company;
    }

    public void setExpress_company(String express_company) {
        this.express_company = express_company;
    }

    public String getExpress_number() {
        return express_number;
    }

    public void setExpress_number(String express_number) {
        this.express_number = express_number;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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

    public String getStore_user_id() {
        return store_user_id;
    }

    public void setStore_user_id(String store_user_id) {
        this.store_user_id = store_user_id;
    }

    public String getStore_user_name() {
        return store_user_name;
    }

    public void setStore_user_name(String store_user_name) {
        this.store_user_name = store_user_name;
    }

    public List<OrderCustomerServiceItemEntity> getReturns() {
        return returns;
    }

    public void setReturns(List<OrderCustomerServiceItemEntity> returns) {
        this.returns = returns;
    }

    public List<OrderCustomerServiceItemEntity> getExchanges() {
        return exchanges;
    }

    public void setExchanges(List<OrderCustomerServiceItemEntity> exchanges) {
        this.exchanges = exchanges;
    }
}
