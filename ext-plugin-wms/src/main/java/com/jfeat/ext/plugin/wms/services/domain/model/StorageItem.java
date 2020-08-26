package com.jfeat.ext.plugin.wms.services.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/8/18
 */
public class StorageItem {
    private Long skuId;
    private Integer transactionQuantities;
    private BigDecimal transactionSkuPrice;

    public BigDecimal getTransactionSkuPrice() {
        return transactionSkuPrice;
    }

    public StorageItem setTransactionSkuPrice(BigDecimal transactionSkuPrice) {
        this.transactionSkuPrice = transactionSkuPrice;
        return this;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getTransactionQuantities() {
        return transactionQuantities;
    }

    public void setTransactionQuantities(Integer transactionQuantities) {
        this.transactionQuantities = transactionQuantities;
    }
}
