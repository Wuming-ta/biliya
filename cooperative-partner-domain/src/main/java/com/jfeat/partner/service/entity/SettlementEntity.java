package com.jfeat.partner.service.entity;

import java.math.BigDecimal;

/**
 * Created by kang on 2017/6/27.
 */
public class SettlementEntity {

    private BigDecimal monthlyExpectedSettledAmount;
    private BigDecimal monthlySettledAmount;

    public BigDecimal getMonthlySettledAmount() {
        return monthlySettledAmount;
    }

    public void setMonthlySettledAmount(BigDecimal monthlySettledAmount) {
        this.monthlySettledAmount = monthlySettledAmount;
    }

    public BigDecimal getMonthlyExpectedSettledAmount() {
        return monthlyExpectedSettledAmount;
    }

    public void setMonthlyExpectedSettledAmount(BigDecimal monthlyExpectedSettledAmount) {
        this.monthlyExpectedSettledAmount = monthlyExpectedSettledAmount;
    }

}
