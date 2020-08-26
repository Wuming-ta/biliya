package com.jfeat.member.model.param;

/**
 * @author jackyhuang
 * @date 2018/8/17
 */
public class CouponParam {

    private int pageNumber;
    private int pageSize;
    private String code;
    private String name;
    private String status;
    private Integer userId;
    private String user;

    public CouponParam(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public CouponParam setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public CouponParam setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getCode() {
        return code;
    }

    public CouponParam setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public CouponParam setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public CouponParam setStatus(String status) {
        this.status = status;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public CouponParam setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public String getUser() {
        return user;
    }

    public CouponParam setUser(String user) {
        this.user = user;
        return this;
    }
}
