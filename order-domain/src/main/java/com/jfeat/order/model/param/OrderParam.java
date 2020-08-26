package com.jfeat.order.model.param;

import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/8/17
 */
public class OrderParam {
    private Integer pageNumber;
    private Integer pageSize;
    private String type;
    private String orderNumber;
    private String status;
    private String contactUser;
    private String contactPhone;
    private String phone;
    private Integer reminder;
    private Integer userId;
    private String paymentType;
    private String deliveryType;
    private String startTime;
    private String endTime;
    private String marketing;
    private String mname;
    private String productName;
    private String[] statuses;
    private String storeId;
    private String barcode;
    private Boolean queryMarketing;
    private String search;
    private Boolean queryReturnRefund;
    private Boolean commented;
    private Boolean showDeleted;
    // 门店销售员
    private String storeUserCode;

    private String skuId;
    private String warehouseId;
    private String[] orderNumbers;

    public OrderParam() {

    }
    
    public OrderParam(Integer pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public Boolean getShowDeleted() {
        return showDeleted;
    }

    public OrderParam setShowDeleted(Boolean showDeleted) {
        this.showDeleted = showDeleted;
        return this;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public OrderParam setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        return this;
    }

    public String[] getOrderNumbers() {
        return orderNumbers;
    }

    public OrderParam setOrderNumbers(String[] orderNumbers) {
        this.orderNumbers = orderNumbers;
        return this;
    }

    public String getSkuId() {
        return skuId;
    }

    public OrderParam setSkuId(String skuId) {
        this.skuId = skuId;
        return this;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public OrderParam setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
        return this;
    }

    public String getStoreUserCode() {
        return storeUserCode;
    }

    public OrderParam setStoreUserCode(String storeUserCode) {
        this.storeUserCode = storeUserCode;
        return this;
    }

    public Boolean getCommented() {
        return commented;
    }

    public OrderParam setCommented(Boolean commented) {
        this.commented = commented;
        return this;
    }

    public Boolean getQueryReturnRefund() {
        return queryReturnRefund;
    }

    public OrderParam setQueryReturnRefund(Boolean queryReturnRefund) {
        this.queryReturnRefund = queryReturnRefund;
        return this;
    }

    public String getSearch() {
        return search;
    }

    public OrderParam setSearch(String search) {
        this.search = search;
        return this;
    }

    public Boolean getQueryMarketing() {
        return queryMarketing;
    }

    public OrderParam setQueryMarketing(Boolean queryMarketing) {
        this.queryMarketing = queryMarketing;
        return this;
    }

    public String getBarcode() {
        return barcode;
    }

    public OrderParam setBarcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public OrderParam setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public OrderParam setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getType() {
        return type;
    }

    public OrderParam setType(String type) {
        this.type = type;
        return this;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public OrderParam setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public OrderParam setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getContactUser() {
        return contactUser;
    }

    public OrderParam setContactUser(String contactUser) {
        this.contactUser = contactUser;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public OrderParam setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Integer getReminder() {
        return reminder;
    }

    public OrderParam setReminder(Integer reminder) {
        this.reminder = reminder;
        return this;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public OrderParam setPaymentType(String paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public OrderParam setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
        return this;
    }

    public String getStartTime() {
        return startTime;
    }

    public OrderParam setStartTime(String startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getEndTime() {
        return endTime;
    }

    public OrderParam setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getMarketing() {
        return marketing;
    }

    public OrderParam setMarketing(String marketing) {
        this.marketing = marketing;
        return this;
    }

    public String getMname() {
        return mname;
    }

    public OrderParam setMname(String mname) {
        this.mname = mname;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public OrderParam setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public OrderParam setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public String[] getStatuses() {
        return statuses;
    }

    public OrderParam setStatuses(String[] statuses) {
        this.statuses = statuses;
        return this;
    }

    public String getStoreId() {
        return storeId;
    }

    public OrderParam setStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }
}
