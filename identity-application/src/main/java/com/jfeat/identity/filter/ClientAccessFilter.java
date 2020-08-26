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

import com.jfinal.kit.StrKit;
import org.apache.shiro.web.servlet.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ehngjen on 9/1/2015.
 */
public class ClientAccessFilter extends OncePerRequestFilter  {
    private static Logger logger = LoggerFactory.getLogger(ClientAccessFilter.class);
    private List<String> clientFilter = new LinkedList<>();

    @Override
    protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        boolean authenticatingResult = false;
        ServletUtils.setLastError(servletRequest, null);//clear the error

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (httpServletRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (clientFilter.size() > 0) {
            for (String name : clientFilter) {
                ClientFilter clientFilter = ClientFilterManager.me().getFilter(name);
                if (clientFilter == null) {
                    throw new RuntimeException("unknown client filter.");
                }
                authenticatingResult = clientFilter.authenticating(servletRequest);
                if (authenticatingResult) {
                    break;
                }
            }
        }
        else {
            for (ClientFilter clientFilter : ClientFilterManager.me().getFilters()) {
                authenticatingResult = clientFilter.authenticating(servletRequest);
                if (authenticatingResult) {
                    break;
                }
            }
        }

        if (!authenticatingResult) {
            ServletUtils.send401Response(servletResponse, ServletUtils.getLastError(servletRequest, "401 unauthorized"));
            return;
        }


        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void setClientFilter(String nameList) {
        logger.debug(nameList);
        if (StrKit.notBlank()) {
            String[] names = nameList.split(",");
            for (String name : names) {
                clientFilter.add(name.trim());
            }
        }
    }
}
