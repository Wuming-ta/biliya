package com.jfeat.product.model.param;

/**
 * @author jackyhuang
 * @date 2018/8/27
 */
public class ProductParam {
    private int pageNumber;
    private int pageSize;
    private Integer mid;
    private String name;
    private String status;
    private Integer categoryId;
    private Integer promoted;
    private Integer zone;
    private String[] orderByList;
    private String[] orderByDescList;
    private Integer purchaseStrategyId;
    private String barCode;
    private String storeLocation;
    private Integer wholesale;
    private Integer isVirtual;
    private Integer isPresale;
    private String[] tags;

    public ProductParam(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public Integer getIsPresale() {
        return isPresale;
    }

    public ProductParam setIsPresale(Integer isPresale) {
        this.isPresale = isPresale;
        return this;
    }

    public String[] getTags() {
        return tags;
    }

    public ProductParam setTags(String[] tags) {
        this.tags = tags;
        return this;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public ProductParam setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public ProductParam setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getIsVirtual() {
        return isVirtual;
    }

    public ProductParam setIsVirtual(Integer isVirtual) {
        this.isVirtual = isVirtual;
        return this;
    }

    public Integer getMid() {
        return mid;
    }

    public ProductParam setMid(Integer mid) {
        this.mid = mid;
        return this;
    }

    public String getName() {
        return name;
    }

    public ProductParam setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ProductParam setStatus(String status) {
        this.status = status;
        return this;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public ProductParam setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public Integer getPromoted() {
        return promoted;
    }

    public ProductParam setPromoted(Integer promoted) {
        this.promoted = promoted;
        return this;
    }

    public Integer getZone() {
        return zone;
    }

    public ProductParam setZone(Integer zone) {
        this.zone = zone;
        return this;
    }

    public String[] getOrderByList() {
        return orderByList;
    }

    public ProductParam setOrderByList(String[] orderByList) {
        this.orderByList = orderByList;
        return this;
    }

    public String[] getOrderByDescList() {
        return orderByDescList;
    }

    public ProductParam setOrderByDescList(String[] orderByDescList) {
        this.orderByDescList = orderByDescList;
        return this;
    }

    public Integer getPurchaseStrategyId() {
        return purchaseStrategyId;
    }

    public ProductParam setPurchaseStrategyId(Integer purchaseStrategyId) {
        this.purchaseStrategyId = purchaseStrategyId;
        return this;
    }

    public String getBarCode() {
        return barCode;
    }

    public ProductParam setBarCode(String barCode) {
        this.barCode = barCode;
        return this;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public ProductParam setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
        return this;
    }

    public Integer getWholesale() {
        return wholesale;
    }

    public ProductParam setWholesale(Integer wholesale) {
        this.wholesale = wholesale;
        return this;
    }
}
