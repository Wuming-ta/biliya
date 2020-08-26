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

package com.jfeat.mall.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jacky on 5/3/16.
 */
public class ApiResponse {

    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;

    @SerializedName("status_code")
    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSucceed() {
        return getStatusCode() == SUCCESS;
    }
}
