package com.jfeat.kit;

/**
 * Created by jackyhuang on 2018/1/9.
 */
public class HttpInternalErrorException extends HttpException {
    public HttpInternalErrorException(String url) {
        super(500, url + " internal server error");
    }
}
