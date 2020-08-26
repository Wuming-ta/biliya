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

package com.jfeat.mall.api;

import com.google.common.net.HttpHeaders;
import com.jfinal.kit.StrKit;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacky on 5/3/16.
 */
public abstract class BaseApi {

    protected static Logger logger = LoggerFactory.getLogger(BaseApi.class);

    protected static String host = "http://localhost:9990";
    protected static String username = "sys";
    protected static String password = "sys";

    protected Map<String, String> headers = new HashMap<>();

    public BaseApi() {
        if (StrKit.notBlank(SysApiConfigHolder.me().getHost())) {
            host = SysApiConfigHolder.me().getHost();
        }
        if (StrKit.notBlank(SysApiConfigHolder.me().getUsername())) {
            username = SysApiConfigHolder.me().getUsername();
        }
        if (StrKit.notBlank(SysApiConfigHolder.me().getPassword())) {
            password = SysApiConfigHolder.me().getPassword();
        }
        buildAuthorizationHeader();
    }

    private void buildAuthorizationHeader() {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        addHeader(HttpHeaders.AUTHORIZATION, authHeader);
    }

    public void addHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

}
