package com.jfeat.ext.plugin.wms.services.domain.model;

/**
 * @author jackyhuang
 * @date 2018/11/30
 */
public class Warehouse {
    private Long id;
    private String warehouseAddress;
    private String warehouseCode;
    private String warehouseName;
    private String warehousePCD;

    public Long getId() {
        return id;
    }

    public Warehouse setId(Long id) {
        this.id = id;
        return this;
    }

    public String getWarehouseAddress() {
        return warehouseAddress;
    }

    public Warehouse setWarehouseAddress(String warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
        return this;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public Warehouse setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
        return this;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public Warehouse setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
        return this;
    }

    public String getWarehousePCD() {
        return warehousePCD;
    }

    public Warehouse setWarehousePCD(String warehousePCD) {
        this.warehousePCD = warehousePCD;
        return this;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "id=" + id +
                ", warehouseAddress='" + warehouseAddress + '\'' +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", warehouseName='" + warehouseName + '\'' +
                ", warehousePCD='" + warehousePCD + '\'' +
                '}';
    }
}
