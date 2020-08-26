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

package com.jfeat.marketing;

/**
 * Created by jackyhuang on 17/5/8.
 */
public enum CouponUsage {
    DISABLED(0),
    ENABLED_MARKETING(1),
    ENABLED_SYSTEM(2);

    private int val;
    CouponUsage(int val) {
        this.val = val;
    }
    public int getValue() {
        return this.val;
    }
}
