package com.jfeat.kit;

/**
 * Created by jackyhuang on 2018/1/9.
 */
public class HttpNotFoundException extends HttpException {
    public HttpNotFoundException(String url) {
        super(404, url + " not found.");
    }
}
