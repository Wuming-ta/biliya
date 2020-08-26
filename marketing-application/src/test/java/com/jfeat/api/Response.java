/*
 * Copyright (C) 2014-2017 www.kequandian.net
 *
 *  The program may be used and/or copied only with the written permission
 *  from kequandian.net, or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the program
 *  has been supplied.
 *
 *  All rights reserved.
 */

package com.jfeat.api;

import com.google.gson.annotations.SerializedName;

public class Response {
    @SerializedName("status_code")
    private int statusCode;

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
