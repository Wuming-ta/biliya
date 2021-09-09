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

package com.jfeat.identity.filter;

import com.jfeat.identity.model.User;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ehngjen on 9/2/2015.
 */
public class ServletUtils {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String DEVICE_ID = "device_id";
    private static final String CLIENT_NAME = "client_name";

    private static final String LAST_ERROR = "_last_error_";

    public static void setLastError(ServletRequest servletRequest, String error) {
        servletRequest.setAttribute(LAST_ERROR, error);
    }

    public static String getLastError(ServletRequest servletRequest, String defaultError) {
        return servletRequest.getAttribute(LAST_ERROR) == null ? defaultError : (String) servletRequest.getAttribute(LAST_ERROR);
    }

    public static String getLastError(ServletRequest servletRequest) {
        return (String) servletRequest.getAttribute(LAST_ERROR);
    }

    public static String getDeviceId(ServletRequest servletRequest) {
        HttpServletRequest request = WebUtils.toHttp(servletRequest);
        String result= request.getHeader(DEVICE_ID);
        return result==null?"":result;
    }

    public static String getClientName(ServletRequest servletRequest) {
        HttpServletRequest request = WebUtils.toHttp(servletRequest);
        String result= request.getHeader(CLIENT_NAME);
        return result==null?"":result;
    }

    public static String getAuthzHeader(ServletRequest request) {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        String result =  httpRequest.getHeader(AUTHORIZATION_HEADER);
        return result==null?"":result;
    }

    public static void sendBadResponse(ServletResponse servletResponse, String message, int errorCode) throws IOException {
        sendResponse(servletResponse, message, HttpServletResponse.SC_BAD_REQUEST, errorCode);
    }

    public static void sendBadResponse(ServletResponse servletResponse, String message) throws IOException {
        sendResponse(servletResponse, message, HttpServletResponse.SC_BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST);
    }

    public static void send401Response(ServletResponse servletResponse, String message, int errorCode) throws IOException {
        sendResponse(servletResponse, message, HttpServletResponse.SC_UNAUTHORIZED, errorCode);
    }

    public static void send401Response(ServletResponse servletResponse, String message) throws IOException {
        sendResponse(servletResponse, message, HttpServletResponse.SC_UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED);
    }

    public static void sendResponse(ServletResponse servletResponse, String message, int statusCode, int errorCode) throws IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type,Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With, Accept");
        httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Authorization");
        Map<String, Object> map = new HashMap<>();
        map.put("status_code", errorCode);
        map.put("message", message);
        PrintWriter writer = httpServletResponse.getWriter();
        writer.println(com.jfinal.kit.JsonKit.toJson(map));
        httpServletResponse.setStatus(statusCode);
    }
}
