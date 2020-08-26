package com.jfeat.identity.model.param;

/**
 * @author jackyhuang
 * @date 2018/8/29
 */
public class UserParam {
    private int pageNumber;
    private int pageSize;
    private String name;
    private String phone;
    private String status;
    private Integer roleId;
    private Integer appUser;

    public UserParam(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public UserParam setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public UserParam setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserParam setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserParam setStatus(String status) {
        this.status = status;
        return this;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public UserParam setRoleId(Integer roleId) {
        this.roleId = roleId;
        return this;
    }

    public Integer getAppUser() {
        return appUser;
    }

    public UserParam setAppUser(Integer appUser) {
        this.appUser = appUser;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserParam setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
