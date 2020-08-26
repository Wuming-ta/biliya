/*
 *   Copyright (C) 2014-2016 www.kequandian.net
 *
 *    The program may be used and/or copied only with the written permission
 *    from www.kequandian.net or in accordance with the terms and
 *    conditions stipulated in the agreement/contract under which the program
 *    has been supplied.
 *
 *    All rights reserved.
 *
 */

package com.jfeat.product.service;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jackyhuang on 16/9/5.
 */
public class CarriageCalcResult {
    private BigDecimal carriage = BigDecimal.ZERO;
    private BigDecimal delta = BigDecimal.ZERO;
    private boolean inclPostage = false;
    private String message;

    public BigDecimal getResult() {
        return carriage;
    }

    public void setResult(BigDecimal carriage) {
        this.carriage = carriage;
    }

    public BigDecimal getDelta() {
        return delta;
    }

    public void setDelta(BigDecimal delta) {
        this.delta = delta;
    }

    public void setInclPostage(boolean inclPostage) {
        this.inclPostage = inclPostage;
    }

    public boolean isInclPostage() {
        return inclPostage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CarriageCalcResult{" +
                "carriage=" + carriage +
                ", delta=" + delta +
                ", inclPostage=" + inclPostage +
                ", message='" + message + '\'' +
                '}';
    }
}
