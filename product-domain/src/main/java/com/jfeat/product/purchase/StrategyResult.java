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

package com.jfeat.product.purchase;

/**
 * Created by jackyhuang on 16/10/14.
 */
public class StrategyResult {
    private boolean result;
    private String errorMessage;

    public StrategyResult() {
        this(true, "ok");
    }

    public StrategyResult(String errorMessage) {
        this(false, errorMessage);
    }

    public StrategyResult(boolean result, String errorMessage) {
        this.result = result;
        this.errorMessage = errorMessage;
    }

    public boolean isSucceed() {
        return getResult();
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "StrategyResult{" +
                "result=" + result +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
