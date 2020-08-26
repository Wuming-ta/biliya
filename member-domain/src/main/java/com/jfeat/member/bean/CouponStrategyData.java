package com.jfeat.member.bean;

import java.util.Date;

/**
 * @author jackyhuang
 * @date 2018/10/23
 */
public class CouponStrategyData {

    private Integer userType;
    private Integer sex;
    private Date birthday;

    private Date beCustomerTime;

    private Date beMemberTime;

    private Integer walletAmount;

    private Integer creditAmount;

    private Integer walletBalance;

    private Integer creditBalance;


    private Integer consumeAmount;

    private Integer consumeCount;

    private Integer consumeAverageAmount;

    private Integer walletConsumeAmount;

    private Integer creditConsumeAmount;
    private Date lastConsumeTime;

    public Integer getUserType() {
        return userType;
    }

    public CouponStrategyData setUserType(Integer userType) {
        this.userType = userType;
        return this;
    }

    public Integer getSex() {
        return sex;
    }

    public CouponStrategyData setSex(Integer sex) {
        this.sex = sex;
        return this;
    }

    public Date getBirthday() {
        return birthday;
    }

    public CouponStrategyData setBirthday(Date birthday) {
        this.birthday = birthday;
        return this;
    }

    public Date getBeCustomerTime() {
        return beCustomerTime;
    }

    public CouponStrategyData setBeCustomerTime(Date beCustomerTime) {
        this.beCustomerTime = beCustomerTime;
        return this;
    }

    public Date getBeMemberTime() {
        return beMemberTime;
    }

    public CouponStrategyData setBeMemberTime(Date beMemberTime) {
        this.beMemberTime = beMemberTime;
        return this;
    }

    public Integer getWalletAmount() {
        return walletAmount;
    }

    public CouponStrategyData setWalletAmount(Integer walletAmount) {
        this.walletAmount = walletAmount;
        return this;
    }

    public Integer getCreditAmount() {
        return creditAmount;
    }

    public CouponStrategyData setCreditAmount(Integer creditAmount) {
        this.creditAmount = creditAmount;
        return this;
    }

    public Integer getWalletBalance() {
        return walletBalance;
    }

    public CouponStrategyData setWalletBalance(Integer walletBalance) {
        this.walletBalance = walletBalance;
        return this;
    }

    public Integer getCreditBalance() {
        return creditBalance;
    }

    public CouponStrategyData setCreditBalance(Integer creditBalance) {
        this.creditBalance = creditBalance;
        return this;
    }

    public Integer getConsumeAmount() {
        return consumeAmount;
    }

    public CouponStrategyData setConsumeAmount(Integer consumeAmount) {
        this.consumeAmount = consumeAmount;
        return this;
    }

    public Integer getConsumeCount() {
        return consumeCount;
    }

    public CouponStrategyData setConsumeCount(Integer consumeCount) {
        this.consumeCount = consumeCount;
        return this;
    }

    public Integer getConsumeAverageAmount() {
        return consumeAverageAmount;
    }

    public CouponStrategyData setConsumeAverageAmount(Integer consumeAverageAmount) {
        this.consumeAverageAmount = consumeAverageAmount;
        return this;
    }

    public Integer getWalletConsumeAmount() {
        return walletConsumeAmount;
    }

    public CouponStrategyData setWalletConsumeAmount(Integer walletConsumeAmount) {
        this.walletConsumeAmount = walletConsumeAmount;
        return this;
    }

    public Integer getCreditConsumeAmount() {
        return creditConsumeAmount;
    }

    public CouponStrategyData setCreditConsumeAmount(Integer creditConsumeAmount) {
        this.creditConsumeAmount = creditConsumeAmount;
        return this;
    }

    public Date getLastConsumeTime() {
        return lastConsumeTime;
    }

    public CouponStrategyData setLastConsumeTime(Date lastConsumeTime) {
        this.lastConsumeTime = lastConsumeTime;
        return this;
    }
}
