package com.jfeat.ext.plugin.vip.bean;

import java.math.BigDecimal;

/**
 * @author jackyhuang
 * @date 2018/9/8
 */
public class Grade {
    // 充值
    private BigDecimal depositBonusPlan = BigDecimal.ZERO;
    // 赠送值
    private BigDecimal depositWinBonus = BigDecimal.ZERO;

    // 每使用xxx积分
    private Integer creditCashPlan = 0;
    // 抵扣xx元
    private BigDecimal creditWinCash = BigDecimal.ZERO;
    // 是否启用积分抵扣功能
    private Integer creditCashPlanEnabled = 0;
    // 每次可抵扣最大现金, 0 无限制
    private BigDecimal creditCashMaxAmount = BigDecimal.ZERO;

    public Integer getCreditCashPlan() {
        return creditCashPlan;
    }

    public Grade setCreditCashPlan(Integer creditCashPlan) {
        this.creditCashPlan = creditCashPlan;
        return this;
    }

    public BigDecimal getCreditWinCash() {
        return creditWinCash;
    }

    public Grade setCreditWinCash(BigDecimal creditWinCash) {
        this.creditWinCash = creditWinCash;
        return this;
    }

    public Integer getCreditCashPlanEnabled() {
        return creditCashPlanEnabled;
    }

    public Grade setCreditCashPlanEnabled(Integer creditCashPlanEnabled) {
        this.creditCashPlanEnabled = creditCashPlanEnabled;
        return this;
    }

    public BigDecimal getCreditCashMaxAmount() {
        return creditCashMaxAmount;
    }

    public Grade setCreditCashMaxAmount(BigDecimal creditCashMaxAmount) {
        this.creditCashMaxAmount = creditCashMaxAmount;
        return this;
    }

    public BigDecimal getDepositBonusPlan() {
        return depositBonusPlan;
    }

    public Grade setDepositBonusPlan(BigDecimal depositBonusPlan) {
        this.depositBonusPlan = depositBonusPlan;
        return this;
    }

    public BigDecimal getDepositWinBonus() {
        return depositWinBonus;
    }

    public Grade setDepositWinBonus(BigDecimal depositWinBonus) {
        this.depositWinBonus = depositWinBonus;
        return this;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "depositBonusPlan=" + depositBonusPlan +
                ", depositWinBonus=" + depositWinBonus +
                '}';
    }
}
