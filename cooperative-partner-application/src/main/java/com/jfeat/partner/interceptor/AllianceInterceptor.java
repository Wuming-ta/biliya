package com.jfeat.partner.interceptor;

import com.jfeat.core.RestController;
import com.jfeat.identity.authc.ShiroUser;
import com.jfeat.partner.model.Alliance;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author jackyhuang
 * @date 2019/10/15
 */
public class AllianceInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(AllianceInterceptor.class);

    private List<String> whiteListUrl;

    public AllianceInterceptor(List<String> urls) {
        this.whiteListUrl = urls;
        this.whiteListUrl.addAll(Arrays.asList(
                "/rest/phone",
                "/rest/profile",
                "/rest/register",
                "/rest/login",
                "/rest/login_wxapp",
                "/rest/login_wxa",
                "/rest/"));
    }

    private boolean inWhitelist(String target) {
        return !target.startsWith("/rest") || this.whiteListUrl.contains(target) || target.startsWith("/rest/pub/");
    }

    @Override
    public void intercept(Invocation invocation) {

        if (this.inWhitelist(invocation.getControllerKey())) {
            logger.debug("{} is in whitelist", invocation.getControllerKey());
            invocation.invoke();
            return;
        }

        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null) {
            ShiroUser shiroUser = (ShiroUser) currentUser.getPrincipal();
            if (shiroUser != null) {
                Integer userId = shiroUser.id;
                Alliance alliance = Alliance.dao.findByUserId(userId);
                if (alliance != null && alliance.isRegularAlliance()) {
                    invocation.invoke();
                    return;
                }
                logger.debug("user {} is not a alliance.", userId);
            }
        }

        logger.debug("user is not allowed to access this api. {}", invocation.getControllerKey());

        RestController c = (RestController) invocation.getController();
        c.renderFailure("not.valid.alliance");
    }
}
