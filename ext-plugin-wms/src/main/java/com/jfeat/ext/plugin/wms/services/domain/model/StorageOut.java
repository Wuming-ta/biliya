package com.jfeat.ext.plugin.wms.services.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/8/18
 */
public class StorageOut {
    private String transactionType;
    private String transactionCode;
    private String outOrderNum;
    private String distributorCustomer;
    private String originatorName;
    private Long warehouseId;
    private String note;
    private List<StorageItem> storageOutItems = new ArrayList<>();

    public String getDistributorCustomer() {
        return distributorCustomer;
    }

    public StorageOut setDistributorCustomer(String distributorCustomer) {
        this.distributorCustomer = distributorCustomer;
        return this;
    }

    public String getOriginatorName() {
        return originatorName;
    }

    public StorageOut setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
        return this;
    }

    public String getOutOrderNum() {
        return outOrderNum;
    }

    public StorageOut setOutOrderNum(String outOrderNum) {
        this.outOrderNum = outOrderNum;
        return this;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<StorageItem> getStorageOutItems() {
        return storageOutItems;
    }

    public void setStorageOutItems(List<StorageItem> storageOutItems) {
        this.storageOutItems = storageOutItems;
    }
}
