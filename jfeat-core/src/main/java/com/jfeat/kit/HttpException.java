package com.jfeat.kit;

/**
 * Created by jackyhuang on 2018/1/9.
 */
public class HttpException extends RuntimeException {
    private int statusCode;
    public HttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
    protected int getStatusCode() {
        return statusCode;
    }
}
