package com.jfeat.ext.plugin.wms.services.domain.model;

/**
 * Created by kang on 2018/6/23.
 */
public class Inventory {
    private Long id;
    private Long warehouseId; //仓库id
    private Long slotId; //储位id
    private Long skuId;
    private Integer minInventory; //库存下限
    private Integer maxInventory; //库存上限
    private Integer advanceQuantities; //预购量
    private Integer transmitQuantities; //在途量
    private Integer validSku; //可用库存量

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getMinInventory() {
        return minInventory;
    }

    public void setMinInventory(Integer minInventory) {
        this.minInventory = minInventory;
    }

    public Integer getAdvanceQuantities() {
        return advanceQuantities;
    }

    public void setAdvanceQuantities(Integer advanceQuantities) {
        this.advanceQuantities = advanceQuantities;
    }

    public Integer getTransmitQuantities() {
        return transmitQuantities;
    }

    public void setTransmitQuantities(Integer transmitQuantities) {
        this.transmitQuantities = transmitQuantities;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Integer getMaxInventory() {
        return maxInventory;
    }

    public void setMaxInventory(Integer maxInventory) {
        this.maxInventory = maxInventory;
    }

    public Integer getValidSku() {
        return validSku;
    }

    public void setValidSku(Integer validSku) {
        this.validSku = validSku;
    }
}
