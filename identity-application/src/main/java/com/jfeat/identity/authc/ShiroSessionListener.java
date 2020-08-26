package com.jfeat.identity.authc;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jackyhuang
 * @date 2018/11/29
 */
public class ShiroSessionListener implements SessionListener {

    private static final Logger logger = LoggerFactory.getLogger(ShiroSessionListener.class);

    @Override
    public void onStart(Session session) {
        logger.debug("session started. {}", session.getId());
    }

    @Override
    public void onStop(Session session) {
        logger.debug("session stopped. {}", session.getId());
        LoginUserStore.me().clear(session.getId().toString());
    }

    @Override
    public void onExpiration(Session session) {
        logger.debug("session expired. {}", session.getId());
        LoginUserStore.me().clear(session.getId().toString());
    }
}
