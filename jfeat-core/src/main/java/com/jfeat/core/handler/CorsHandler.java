package com.jfeat.core.handler;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jacky on 4/18/16.
 */
public class CorsHandler extends Handler {
    @Override
    public void handle(String s, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean[] booleans) {
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type,Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With, Accept");
        httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
        if (httpServletRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
            booleans[0] = true;
            httpServletResponse.setStatus(204);
            return;
        }

        next.handle(s, httpServletRequest, httpServletResponse, booleans);

    }
}
