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

import com.jfeat.identity.authc.AccessToken;
import com.jfeat.identity.authc.AccessTokenToken;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.identity.model.User;
import com.jfeat.kit.DateKit;
import com.jfinal.kit.StrKit;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * Created by ehngjen on 8/31/2015.
 */
public class ClientApiAuthcFilter implements ClientFilter {
    private static Logger logger = LoggerFactory.getLogger(ClientApiAuthcFilter.class);

    private static final String BEARER = "Bearer ";

    @Override
    public boolean authenticating(ServletRequest servletRequest) {
        logger.debug("authenticating");
        String authorizationHeader = ServletUtils.getAuthzHeader(servletRequest);
        if (StrKit.isBlank(authorizationHeader)) {
            logger.debug("authorization header is null or missing.");
            ServletUtils.setLastError(servletRequest, "authorization.header.missing");
            return false;
        }

        if (authorizationHeader.startsWith(BEARER)) {
            authorizationHeader = authorizationHeader.substring(BEARER.length());
        }

        if (StrKit.isBlank(authorizationHeader)) {
            logger.debug("authorization header is null or missing.");
            ServletUtils.setLastError(servletRequest, "authorization.header.missing");
            return false;
        }

        try {
            AttemptLogin login = new AttemptLogin();
            boolean result = login.login(authorizationHeader);
            if (!result) {
                ServletUtils.setLastError(servletRequest, login.getError());
            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            ServletUtils.setLastError(servletRequest, e.getMessage());
            return false;
        }
    }


}
