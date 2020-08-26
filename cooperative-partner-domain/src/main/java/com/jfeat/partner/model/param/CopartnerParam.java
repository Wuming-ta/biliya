package com.jfeat.partner.model.param;

/**
 * @author jackyhuang
 * @date 2018/8/22
 */
public class CopartnerParam {
    private String name;
    private String status;

    private int pageNumber = 1;
    private int pageSize = 30;

    public CopartnerParam() {

    }

    public CopartnerParam(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public CopartnerParam setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public CopartnerParam setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getName() {
        return name;
    }

    public CopartnerParam setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public CopartnerParam setStatus(String status) {
        this.status = status;
        return this;
    }
}
