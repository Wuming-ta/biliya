package com.jfeat.member.model.param;

import java.util.Date;

/**
 * @author jackyhuang
 * @date 2018/9/26
 */
public class WalletHistoryParam {
    private int pageNumber;
    private int pageSize;
    private Date startTime;
    private Date endTime;
    private Integer walletId;
    private String type;

    public WalletHistoryParam(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public String getType() {
        return type;
    }

    public WalletHistoryParam setType(String type) {
        this.type = type;
        return this;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public WalletHistoryParam setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public WalletHistoryParam setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public WalletHistoryParam setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public WalletHistoryParam setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Integer getWalletId() {
        return walletId;
    }

    public WalletHistoryParam setWalletId(Integer walletId) {
        this.walletId = walletId;
        return this;
    }

    @Override
    public String toString() {
        return "WalletHistoryParam{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", walletId=" + walletId +
                '}';
    }
}
