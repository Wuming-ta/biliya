package com.jfeat.ext.plugin.vip.bean;

import java.math.BigDecimal;

/**
 * @author jackyhuang
 * @date 2018/9/17
 */
public class DepositPackage {
    private Long id;
    // 充xx元
    private BigDecimal depositBonusPlan;
    // 送xx元
    private BigDecimal depositWinBonus;
    // 是否启动
    private Integer depositBonusPlanEnabled;

    public Long getId() {
        return id;
    }

    public DepositPackage setId(Long id) {
        this.id = id;
        return this;
    }

    public BigDecimal getDepositBonusPlan() {
        return depositBonusPlan;
    }

    public DepositPackage setDepositBonusPlan(BigDecimal depositBonusPlan) {
        this.depositBonusPlan = depositBonusPlan;
        return this;
    }

    public Integer getDepositBonusPlanEnabled() {
        return depositBonusPlanEnabled;
    }

    public DepositPackage setDepositBonusPlanEnabled(Integer depositBonusPlanEnabled) {
        this.depositBonusPlanEnabled = depositBonusPlanEnabled;
        return this;
    }

    public BigDecimal getDepositWinBonus() {
        return depositWinBonus;
    }

    public DepositPackage setDepositWinBonus(BigDecimal depositWinBonus) {
        this.depositWinBonus = depositWinBonus;
        return this;
    }
}
