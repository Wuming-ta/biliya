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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2018/8/20
 */
public class ClientAccessMayAuthenticationFilter extends OncePerRequestFilter {
    private static Logger logger = LoggerFactory.getLogger(ClientAccessMayAuthenticationFilter.class);
    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("authenticating");
        String authorizationHeader = ServletUtils.getAuthzHeader(servletRequest);
        if (StrKit.isBlank(authorizationHeader)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (authorizationHeader.startsWith(BEARER)) {
            authorizationHeader = authorizationHeader.substring(BEARER.length());
        }

        if (StrKit.isBlank(authorizationHeader)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            AttemptLogin login = new AttemptLogin();
            boolean result = login.login(authorizationHeader);

        } catch (Exception e) {

        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}