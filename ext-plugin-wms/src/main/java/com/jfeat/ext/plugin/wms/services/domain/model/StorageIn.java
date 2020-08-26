package com.jfeat.ext.plugin.wms.services.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/8/18
 */
public class StorageIn {
    private String transactionCode;
    private String transactionType;
    private Long warehouseId;
    private String outOrderNum;
    private String distributorCustomer;
    private String originatorName;
    private String note;
    private List<StorageItem> storageInItems = new ArrayList<>();

    public String getDistributorCustomer() {
        return distributorCustomer;
    }

    public StorageIn setDistributorCustomer(String distributorCustomer) {
        this.distributorCustomer = distributorCustomer;
        return this;
    }

    public String getOriginatorName() {
        return originatorName;
    }

    public StorageIn setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
        return this;
    }

    public String getOutOrderNum() {
        return outOrderNum;
    }

    public StorageIn setOutOrderNum(String outOrderNum) {
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

    public List<StorageItem> getStorageInItems() {
        return storageInItems;
    }

    public void setStorageInItems(List<StorageItem> storageInItems) {
        this.storageInItems = storageInItems;
    }
}
